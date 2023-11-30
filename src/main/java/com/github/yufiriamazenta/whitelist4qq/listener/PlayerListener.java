package com.github.yufiriamazenta.whitelist4qq.listener;

import com.github.yufiriamazenta.whitelist4qq.Whitelist4QQ;
import com.github.yufiriamazenta.whitelist4qq.WhitelistManager;
import crypticlib.listener.BukkitListener;
import crypticlib.util.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

/**
 * 玩家相关事件监听
 */
@BukkitListener
public enum PlayerListener implements Listener {

    INSTANCE;

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerLoginOnWhitelistMode1(AsyncPlayerPreLoginEvent event) {
        if (Whitelist4QQ.getInstance().whitelistMode() != 1)
            return;
        UUID uuid = event.getUniqueId();
        WhitelistManager.WhitelistState whitelistState = WhitelistManager.getWhitelistState(uuid);
        YamlConfiguration config = (YamlConfiguration) Whitelist4QQ.getInstance().getConfig();
        switch (whitelistState) {
            case HAS_WHITELIST:
                return;
            case NOT_IN_GROUP:
                String notInGroupMsg = config.getString("general.kick-message-not-in-group", "general.kick-message-not-in-group");
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, TextUtil.color(notInGroupMsg));
                break;
            case NO_WHITELIST:
                String code;
                if (WhitelistManager.getReverseBindCodeMap().containsKey(uuid)) {
                    code = WhitelistManager.getReverseBindCodeMap().get(uuid);
                } else {
                    code = UUID.randomUUID().toString();
                    code = code.substring(code.length() - 6);
                    WhitelistManager.addBindCodeCache(code, uuid);
                }
                String bindHintMsg = config.getString("general.kick-message", "%code%");
                bindHintMsg = bindHintMsg.replace("%code%", code);
                bindHintMsg = TextUtil.color(bindHintMsg);
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, bindHintMsg);
                break;
            default:
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "Error");
        }
    }

    @EventHandler
    public void playerLoginOnWhitelistMode2(PlayerLoginEvent event) {
        if (Whitelist4QQ.getInstance().whitelistMode() != 2)
            return;
        YamlConfiguration config = (YamlConfiguration) Whitelist4QQ.getInstance().getConfig();
        Player player = event.getPlayer();
        WhitelistManager.WhitelistState state = WhitelistManager.getWhitelistState(player.getUniqueId());
        switch (state) {
            case NO_WHITELIST:
                long firstPlayed = player.getFirstPlayed();
                long playedTime = System.currentTimeMillis() - firstPlayed;
                long allowVisitTime = config.getLong("general.allow_visit_second", 600L) * 1000;
                if (playedTime > allowVisitTime) {
                    String msg = config.getString("general.kick_message", "general.kick_message");
                    event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, TextUtil.color(msg));
                    return;
                }
                //TODO 添加标识
                break;
            case NOT_IN_GROUP:
                String notInGroupMsg = config.getString("general.kick-message-not-in-group", "general.kick-message-not-in-group");
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, TextUtil.color(notInGroupMsg));
                break;
            case HAS_WHITELIST:
                return;
            default:
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "Error");
        }
    }

}
