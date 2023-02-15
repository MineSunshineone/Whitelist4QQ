package me.dreamvoid.whitelist4qq.bukkit.listener;

import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.api.MiraiMC;
import me.dreamvoid.whitelist4qq.bukkit.BukkitPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;
import java.util.NoSuchElementException;

import static me.dreamvoid.whitelist4qq.bukkit.Config.*;

/**
 * 玩家即将加入服务器事件
 */
public class PlayerLogin implements Listener {
    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent e) {
        boolean allow = false;

        if (GEN_CheckRange_JOIN) { // 加入服务器的时候检测
            if (!(GEN_UseSelfData)) {
                long binder = MiraiMC.getBind(e.getUniqueId());
                if (binder != 0) {
                    // 是否需要进一步检测是否在群内
                    if (BOT_CheckQQInGroup) {
                        for (long group : BOT_UsedGroupAccounts) {
                            if (allow) break; // 如果下面的代码已经检测到在群里了，就不继续检测

                            for (long bot : BOT_UsedBotAccounts) {
                                try { // 加个try防止服主忘记登录机器人然后尝试获取的时候报错的问题
                                    if (MiraiBot.getBot(bot).getGroup(group).contains(binder)) {
                                        allow = true;
                                        break;
                                    }
                                } catch (NoSuchElementException ignored) {
                                } // 不需要处理报错，直接ignored
                            }
                        }
                    } else allow = true; // 不需要则直接true
                }
            } else {
                YamlConfiguration white = YamlConfiguration.loadConfiguration(BukkitPlugin.getWhitelist());
                if (GEN_UsePlayerName) {
                    List<String> names = white.getStringList("name");
                    allow = names.contains(e.getName());
                } else {
                    List<String> uuids = white.getStringList("uuid");
                    allow = uuids.contains(e.getUniqueId().toString());
                }
            }

        } else allow = true; // 如果不在加入服务器的时候检测，直接放行

        if (allow) {
            e.allow();
        } else {
            e.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                    ChatColor.translateAlternateColorCodes('&', GEN_KickMessage)
            );
        }
    }
}
