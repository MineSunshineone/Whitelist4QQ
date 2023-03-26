package me.dreamvoid.whitelist4qq.bukkit.listener;

import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.api.MiraiMC;
import me.dreamvoid.miraimc.bukkit.event.group.member.MiraiMemberLeaveEvent;
import me.dreamvoid.miraimc.bukkit.event.message.passive.MiraiGroupMessageEvent;
import me.dreamvoid.whitelist4qq.bukkit.BukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.dreamvoid.whitelist4qq.bukkit.Config.*;

/**
 * 机器人事件
 */
public class BotEvent implements Listener {

    private final Pattern pattern = Pattern.compile("^[A-Za-z0-9\\\\_]+$");
    private final Map<Long, String> qqBindConfirmMap;

    public BotEvent() {
        qqBindConfirmMap = new ConcurrentHashMap<>();
    }

    @EventHandler
    public void onGroupMessage(MiraiGroupMessageEvent e) {
        if (BOT_UsedBotAccounts.contains(e.getBotID()) && BOT_UsedGroupAccounts.contains(e.getGroupID()) && BOT_UseGroupMessageCommand && e.getMessage().startsWith(BOT_BindCommandPrefix)) {
            String playerName = e.getMessage().replace(BOT_BindCommandPrefix, "");
            playerName = playerName.replaceAll("\\s", "");
            Matcher matcher = pattern.matcher(playerName);
            if (!matcher.find()) {
                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BukkitPlugin.getInstance().getConfig().getString("bot.messages.bind-failed-unsafe-name", "绑定失败，不合法的正版用户名"));
                return;
            }

            if (!GEN_UseSelfData) {
                if ((GEN_PreventIDRebind && (MiraiMC.getBind(Bukkit.getOfflinePlayer(playerName).getUniqueId()) != 0)) || (GEN_PreventQQRebind && (MiraiMC.getBind(e.getSenderID()) != null))) {
                    // 阻止绑定
                    String id = Bukkit.getOfflinePlayer(MiraiMC.getBind(e.getSenderID())).getName();
                    if (id == null)
                        id = "null";
                    MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindFailed.replace("%id%", id));
                } else {
                    // 允许绑定
                    if (!qqBindConfirmMap.containsKey(e.getSenderID()) || !playerName.equals(qqBindConfirmMap.get(e.getSenderID()))) {
                        String msg = BukkitPlugin.getInstance().getConfig().getString("bot.messages.bind-confirm", "你确定要以名字<name>申请白名单吗？");
                        msg = msg.replace("<name>", playerName);
                        MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(msg);
                        qqBindConfirmMap.put(e.getSenderID(), playerName);
                        return;
                    }
                    qqBindConfirmMap.remove(e.getSenderID());
                    MiraiMC.addBind(Bukkit.getOfflinePlayer(playerName).getUniqueId(), e.getSenderID());
                    MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindSuccess);
                }
            } else {
                if (!qqBindConfirmMap.containsKey(e.getSenderID()) || !playerName.equals(qqBindConfirmMap.get(e.getSenderID()))) {
                    String msg = BukkitPlugin.getInstance().getConfig().getString("bot.messages.bind-confirm", "你确定要以名字<name>申请白名单吗？");
                    msg = msg.replace("<name>", playerName);
                    MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(msg);
                    qqBindConfirmMap.put(e.getSenderID(), playerName);
                    return;
                }
                qqBindConfirmMap.remove(e.getSenderID());
                YamlConfiguration white = YamlConfiguration.loadConfiguration(BukkitPlugin.getWhitelist());
                if (GEN_UsePlayerName) {
                    List<String> names = white.getStringList("name");
                    if (GEN_PreventIDRebind && names.contains(playerName)) {
                        MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindFailed);
                    } else try {
                        names.add(playerName);
                        white.set("name", names);
                        white.save(BukkitPlugin.getWhitelist());
                        MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindSuccess);
                    } catch (IOException ex) {
                        MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindFailedSelfdata);
                        throw new RuntimeException(ex);
                    }
                } else {
                    List<String> uuids = white.getStringList("uuid");
                    if (GEN_PreventIDRebind && uuids.contains(Bukkit.getOfflinePlayer(playerName).getUniqueId().toString())) {
                        MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindFailed);
                    } else try {
                        uuids.add(Bukkit.getOfflinePlayer(playerName).getUniqueId().toString());
                        white.set("uuid", uuids);
                        white.save(BukkitPlugin.getWhitelist());
                        MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindSuccess);
                    } catch (IOException ex) {
                        MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindFailedSelfdata);
                        throw new RuntimeException(ex);
                    }

                }
            }
        }
    }

    @EventHandler
    public void onGroupQuit(MiraiMemberLeaveEvent e) {
        if (!GEN_UseSelfData && BOT_UsedBotAccounts.contains(e.getBotID()) && BOT_UsedGroupAccounts.contains(e.getGroupID()) && BOT_RemoveBindWhenQQQuit) {
            MiraiMC.removeBind(e.getTargetID());
        }
    }
}
