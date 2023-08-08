package com.github.yufiriamazenta.whitelist4qq.bukkit.listener;

import com.github.yufiriamazenta.lib.util.MsgUtil;
import com.github.yufiriamazenta.whitelist4qq.bukkit.Whitelist4QQ;
import com.github.yufiriamazenta.whitelist4qq.bukkit.WhitelistManager;
import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.api.MiraiMC;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * 玩家相关事件监听
 */
public enum PlayerListener implements Listener {

    INSTANCE;

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        long bind = MiraiMC.getBind(uuid);
        YamlConfiguration config = (YamlConfiguration) Whitelist4QQ.getInstance().getConfig();
        if (bind == 0L) {
            String code;
            if (WhitelistManager.getReverseBindCodeMap().containsKey(uuid)) {
                code = WhitelistManager.getReverseBindCodeMap().get(uuid);
            } else {
                code = UUID.randomUUID().toString();
                code = code.substring(code.length() - 6);
                WhitelistManager.addBindCodeCache(code, uuid);
            }
            String msg = config.getString("general.kick-message", "%code%");
            msg = msg.replace("%code%", code);
            msg = MsgUtil.color(msg);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, msg);
        } else {
            boolean allow = false;
            if (config.getBoolean("bot.check-qq-in-group", true)) {
                for (long group : config.getLongList("bot.used-group-numbers")) {
                    if (allow) break; // 如果下面的代码已经检测到在群里了，就不继续检测

                    for (long bot : config.getLongList("bot.used-bot-accounts")) {
                        try { // 加个try防止服主忘记登录机器人然后尝试获取的时候报错的问题
                            if (MiraiBot.getBot(bot).getGroup(group).contains(bind)) {
                                allow = true;
                                break;
                            }
                        } catch (NoSuchElementException ignored) {
                        } // 不需要处理报错，直接ignored
                    }
                }
            } else allow = true;
            if (!allow) {
                String msg = config.getString("general.kick-message-not-in-group", "general.kick-message-not-in-group");
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, MsgUtil.color(msg));
            }
        }
    }
}
