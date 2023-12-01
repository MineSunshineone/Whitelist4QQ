package com.github.yufiriamazenta.whitelist4qq.listener;

import com.github.yufiriamazenta.whitelist4qq.Whitelist4QQ;
import com.github.yufiriamazenta.whitelist4qq.WhitelistManager;
import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import crypticlib.listener.BukkitListener;
import crypticlib.util.MsgUtil;
import crypticlib.util.TextUtil;
import org.bukkit.GameMode;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家相关事件监听
 */
@BukkitListener
public enum PlayerListener implements Listener {

    INSTANCE;
    private Map<UUID, Long> visitorChatTimeMap = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerLoginOnWhitelistMode1(AsyncPlayerPreLoginEvent event) {
        if (Whitelist4QQ.instance().whitelistMode() != 1)
            return;
        UUID uuid = event.getUniqueId();
        WhitelistManager.WhitelistState whitelistState = WhitelistManager.getWhitelistState(uuid);
        switch (whitelistState) {
            case HAS_WHITELIST:
                return;
            case NOT_IN_GROUP:
                String notInGroupMsg = Configs.messagesKickMessageNotInGroup.value();
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
                String bindHintMsg = TextUtil.color(Configs.messagesKickMessageMode1.value().replace("%code%", code));
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, bindHintMsg);
                break;
            default:
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "Error");
        }
    }

    @EventHandler
    public void playerLoginOnWhitelistMode2(PlayerLoginEvent event) {
        if (Whitelist4QQ.instance().whitelistMode() != 2)
            return;
        Player player = event.getPlayer();
        WhitelistManager.WhitelistState state = WhitelistManager.getWhitelistState(player.getUniqueId());
        switch (state) {
            case NO_WHITELIST:
                long playedTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
                long allowVisitTime = Configs.mode2AllowVisitSecond.value() * 20;
                if (playedTime >= allowVisitTime) {
                    String code;
                    if (WhitelistManager.getReverseBindCodeMap().containsKey(player.getUniqueId())) {
                        code = WhitelistManager.getReverseBindCodeMap().get(player.getUniqueId());
                    } else {
                        code = UUID.randomUUID().toString();
                        code = code.substring(code.length() - 6);
                        WhitelistManager.addBindCodeCache(code, player.getUniqueId());
                    }
                    String bindHintMsg = TextUtil.color(Configs.messagesKickMessageMode2.value().replace("%code%", code));
                    event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, TextUtil.color(bindHintMsg));
                    return;
                }
                WhitelistManager.addVisitTag2Player(player);
                break;
            case NOT_IN_GROUP:
                String notInGroupMsg = Configs.messagesKickMessageNotInGroup.value();
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, TextUtil.color(notInGroupMsg));
                break;
            case HAS_WHITELIST:
                WhitelistManager.removeVisitTag4Player(player);
                return;
            default:
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "Error");
        }
    }

    @EventHandler
    public void onVisitorGameModeChange(PlayerGameModeChangeEvent event) {
        if (Whitelist4QQ.instance().whitelistMode() != 2)
            return;
        Player player = event.getPlayer();
        if (!WhitelistManager.hasVisitTag(player))
            return;
        if (!event.getNewGameMode().equals(GameMode.ADVENTURE)) {
            event.setCancelled(true);
            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    @EventHandler
    public void onVisitorInteract(PlayerInteractEvent event) {
        if (Whitelist4QQ.instance().whitelistMode() != 2)
            return;
        Player player = event.getPlayer();
        if (!WhitelistManager.hasVisitTag(player))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVisitorInteractEntity(PlayerInteractEntityEvent event) {
        if (Whitelist4QQ.instance().whitelistMode() != 2)
            return;
        Player player = event.getPlayer();
        if (!WhitelistManager.hasVisitTag(player))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVisitorPickUpItem(EntityPickupItemEvent event) {
        if (Whitelist4QQ.instance().whitelistMode() != 2)
            return;
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (!WhitelistManager.hasVisitTag(player))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVisitorDamageEntity(EntityDamageByEntityEvent event) {
        if (Whitelist4QQ.instance().whitelistMode() != 2)
            return;
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();
        if (!WhitelistManager.hasVisitTag(player))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onVisitorChat(AsyncPlayerChatEvent event) {
        if (Whitelist4QQ.instance().whitelistMode() != 2)
            return;
        Player player = event.getPlayer();
        if (!WhitelistManager.hasVisitTag(player))
            return;
        if (visitorChatTimeMap.containsKey(player.getUniqueId())) {
            long time = System.currentTimeMillis();
            long lastChatTime = visitorChatTimeMap.get(player.getUniqueId());
            int chatCd = Configs.mode2VisitorChatCd.value() * 1000;
            if (time - lastChatTime < chatCd) {
                MsgUtil.sendMsg(player, Configs.messagesVisitorChatInCd.value());
                event.setCancelled(true);
                return;
            }
            visitorChatTimeMap.put(player.getUniqueId(), time);
        } else {
            visitorChatTimeMap.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onVisitorQuit(PlayerQuitEvent event) {
        if (Whitelist4QQ.instance().whitelistMode() != 2)
            return;
        Player player = event.getPlayer();
        if (!WhitelistManager.hasVisitTag(player))
            return;
        visitorChatTimeMap.remove(player.getUniqueId());
    }

    public Map<UUID, Long> getVisitorChatTimeMap() {
        return visitorChatTimeMap;
    }

}
