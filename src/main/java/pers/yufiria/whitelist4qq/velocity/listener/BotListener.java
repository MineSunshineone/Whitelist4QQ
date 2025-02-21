package pers.yufiria.whitelist4qq.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import crypticlib.listener.EventListener;
import me.dreamvoid.miraimc.velocity.event.group.member.MiraiMemberLeaveEvent;
import me.dreamvoid.miraimc.velocity.event.message.passive.MiraiGroupMessageEvent;
import pers.yufiria.whitelist4qq.velocity.WhitelistManager;
import pers.yufiria.whitelist4qq.velocity.config.Configs;
import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.api.MiraiMC;
import pers.yufiria.whitelist4qq.velocity.player.OfflinePlayer;
import pers.yufiria.whitelist4qq.velocity.player.OfflinePlayerManager;

import java.util.UUID;

/**
 * 机器人相关事件监听
 */
@EventListener
public enum BotListener {

    INSTANCE;

    @Subscribe
    public void onGroupMessage(MiraiGroupMessageEvent e) {
        //收到消息的机器人不在配置中时不触发绑定
        if (!Configs.usedBotAccounts.value().contains(e.getBotID()))
            return;
        //收到消息的群不在配置中时不触发绑定
        if (!Configs.usedGroups.value().contains(e.getGroupID()))
            return;
        //只有前缀符合时才触发绑定
        if (!e.getMessage().startsWith(Configs.bindCommandPrefix.value()))
            return;
        //阻止已经绑定的QQ绑定
        if (Configs.preventQQRebind.value()) {
            WhitelistManager.WhitelistState whitelistState = WhitelistManager.getWhitelistState(e.getSenderID());
            if (WhitelistManager.WhitelistState.HAS_WHITELIST.equals(whitelistState)) {
                String playerName;
                UUID bind = MiraiMC.getBind(e.getSenderID());
                if (bind == null) {
                    playerName = "null";
                } else {
                    OfflinePlayer boundPlayer = OfflinePlayerManager.INSTANCE.getOfflinePlayer(bind);
                    if (boundPlayer == null) {
                        playerName = "unknown";
                    } else {
                        playerName = boundPlayer.name();
                    }
                }
                String boundMsg = Configs.messagesBotMessageBindFailedBound.value().replace("%player%", playerName);
                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(boundMsg);
                return;
            }
        }

        //去除无关字符
        String bindCode = e.getMessage()
            .replace(Configs.bindCommandPrefix.value(), "")
            .replace("\\s", "");

        //如果没有对应绑定码则提示绑定失败
        if (!WhitelistManager.getBindCodes().containsKey(bindCode)) {
            MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(Configs.messagesBotMessageBindFailedNotExistCode.value());
            return;
        }

        //添加绑定
        String playerName;
        UUID uuid = WhitelistManager.getBindCodes().get(bindCode);
        OfflinePlayer bindPlayer = OfflinePlayerManager.INSTANCE.getOfflinePlayer(uuid);
        if (bindPlayer == null) {
            playerName = WhitelistManager.getBindPlayerName(uuid);
        } else {
            playerName = bindPlayer.name();
        }
        if (playerName == null) {
            playerName = uuid.toString();
        }
        String replyMsg = Configs.messagesBotMessageBindSuccess.value().replace("%player%", playerName);
        MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(replyMsg);
        WhitelistManager.addBind(e.getSenderID(), bindCode);
    }

    @Subscribe
    public void onSelectPlayer(MiraiGroupMessageEvent e) {
        if (!Configs.usedBotAccounts.value().contains(e.getBotID()))
            return;
        if (!Configs.usedGroups.value().contains(e.getGroupID()))
            return;
        if (!e.getMessage().startsWith(Configs.selectPlayerCommandPrefix.value()) && !e.getMessage().startsWith(Configs.selectQQCommandPrefix.value()))
            return;
        if (e.getMessage().startsWith(Configs.selectQQCommandPrefix.value())) {
            String qqStr = e.getMessage()
                .replace(Configs.selectQQCommandPrefix.value(), "")
                .replace("\\s", "");
            try {
                long qq = Long.parseLong(qqStr);
                UUID bind = MiraiMC.getBind(qq);
                if (bind == null) {
                    MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(Configs.messagesBotMessageSelectQQFailedNotExist.value());
                } else {
                    OfflinePlayer boundPlayer = OfflinePlayerManager.INSTANCE.getOfflinePlayer(bind);
                    String playerName;
                    if (boundPlayer == null) {
                        playerName = "unknown";
                    } else {
                        playerName = boundPlayer.name();
                    }
                    String msg = Configs.messagesBotMessageSelectQQSuccess.value().replace("%player%", playerName);
                    MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(msg);
                }
            } catch (NumberFormatException exc) {
                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(Configs.messagesBotMessageSelectQQFailedNumberFormat.value());
            }
        } else {
            String playerName = e.getMessage()
                .replace(Configs.selectPlayerCommandPrefix.value(), "")
                .replace("\\s", "");
            OfflinePlayer offlinePlayer = OfflinePlayerManager.INSTANCE.getOfflinePlayer(playerName);
            if (offlinePlayer == null) {
                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(Configs.messagesBotMessageSelectPlayerFailedUnknownPlayer.value());
                return;
            }
            long bind = MiraiMC.getBind(offlinePlayer.uuid());
            if (bind == 0L) {
                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(Configs.messagesBotMessageSelectPlayerFailedNotExist.value());
            } else {
                String msg = Configs.messagesBotMessageSelectPlayerSuccess.value().replace("%qq%", bind + "");
                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(msg);
            }
        }
    }

    @Subscribe
    public void onGroupQuit(MiraiMemberLeaveEvent e) {
        if (!Configs.remove_bind_when_qq_quit.value())
            return;
        if (Configs.usedBotAccounts.value().contains(e.getBotID()) && Configs.usedGroups.value().contains(e.getGroupID())) {
            MiraiMC.removeBind(e.getTargetID());
        }
    }
}
