package com.github.yufiriamazenta.whitelist4qq.bukkit.listener;

import com.github.yufiriamazenta.whitelist4qq.bukkit.Whitelist4QQ;
import com.github.yufiriamazenta.whitelist4qq.bukkit.WhitelistManager;
import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.api.MiraiMC;
import me.dreamvoid.miraimc.bukkit.event.group.member.MiraiMemberLeaveEvent;
import me.dreamvoid.miraimc.bukkit.event.message.passive.MiraiGroupMessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 机器人相关事件监听
 */
public enum BotListener implements Listener {

    INSTANCE;

    @EventHandler
    public void onGroupMessage(MiraiGroupMessageEvent e) {
        YamlConfiguration config = (YamlConfiguration) Whitelist4QQ.getInstance().getConfig();

        if (!config.getLongList("bot.used-bot-accounts").contains(e.getBotID()))
            return;
        if (!config.getLongList("bot.used-group-numbers").contains(e.getGroupID()))
            return;
        if (!e.getMessage().startsWith(config.getString("bot.bind-command-prefix")))
            return;
        if (MiraiMC.getBind(e.getSenderID()) != null) {
            String boundMsg = config.getString("bot.messages.bind-failed.bound", "bot.messages.bind-failed.bound");
            boundMsg = boundMsg.replace("%player%", Bukkit.getOfflinePlayer(MiraiMC.getBind(e.getSenderID())).getName());
            MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(boundMsg);
            return;
        }
        String bindCode = e.getMessage().replace(config.getString("bot.bind-command-prefix"), "");
        bindCode = bindCode.replace("\\s", "");

        if (!WhitelistManager.getBindCodeMap().containsKey(bindCode)) {
            String bindFailedNotExistCode = config.getString("bot.messages.bind-failed.not-exist-code", "bot.messages.bind-failed.not-exist-code");
            MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(bindFailedNotExistCode);
            return;
        }

        UUID uuid = WhitelistManager.getBindCodeMap().get(bindCode);
        MiraiMC.addBind(uuid, e.getSenderID());
        String replyMsg = config.getString("bot.messages.bind-success");
        replyMsg = replyMsg.replace("%player%", Bukkit.getOfflinePlayer(uuid).getName());
        MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(replyMsg);
        WhitelistManager.removeBindCodeCache(bindCode);
    }

    @EventHandler
    public void onSelectPlayer(MiraiGroupMessageEvent e) {
        YamlConfiguration config = (YamlConfiguration) Whitelist4QQ.getInstance().getConfig();
        if (!config.getLongList("bot.used-bot-accounts").contains(e.getBotID()))
            return;
        if (!config.getLongList("bot.used-group-numbers").contains(e.getGroupID()))
            return;
        if (!e.getMessage().startsWith(config.getString("bot.select-player-command-prefix")) && !e.getMessage().startsWith(config.getString("bot.select-qq-command-prefix")))
            return;
        if (e.getMessage().startsWith(config.getString("bot.select-qq-command-prefix"))) {
            String qqStr = e.getMessage().replace(config.getString("bot.select-qq-command-prefix"), "");
            qqStr = qqStr.replaceAll("\\s", "");
            try {
                long qq = Long.parseLong(qqStr);
                UUID bind = MiraiMC.getBind(qq);
                if (bind == null) {
                    MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(config.getString("bot.messages.select-qq-failed-not-exist"));
                } else {
                    String msg = config.getString("bot.messages.select-qq-success");
                    msg = msg.replace("%player%", Bukkit.getOfflinePlayer(bind).getName());
                    MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(msg);
                }
            } catch (NumberFormatException exc) {
                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(config.getString("bot.messages.select-qq-failed-number-format"));
            }
        } else {
            String player = e.getMessage().replace(config.getString("bot.select-player-command-prefix"), "");
            player = player.replaceAll("\\s", "");
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
            if (offlinePlayer == null) {
                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(config.getString("bot.messages.select-player-failed-not-exist"));
                return;
            }
            long bind = MiraiMC.getBind(offlinePlayer.getUniqueId());
            if (bind == 0L) {
                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(config.getString("bot.messages.select-player-failed-not-exist"));
            } else {
                String msg = config.getString("bot.messages.select-player-success");
                msg = msg.replace("%qq%", bind + "");
                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(msg);
            }
        }
    }

    @EventHandler
    public void onGroupQuit(MiraiMemberLeaveEvent e) {
        YamlConfiguration config = (YamlConfiguration) Whitelist4QQ.getInstance().getConfig();
        if (!config.getBoolean("bot.remove-bind-when-qq-quit"))
            return;
        if (config.getLongList("bot.used-bot-accounts").contains(e.getBotID()) && config.getLongList("bot.used-group-accounts").contains(e.getGroupID())) {
            MiraiMC.removeBind(e.getTargetID());
        }
    }
}
