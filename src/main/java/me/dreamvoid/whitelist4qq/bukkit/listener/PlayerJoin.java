package me.dreamvoid.whitelist4qq.bukkit.listener;

import me.dreamvoid.miraimc.api.MiraiMC;
import me.dreamvoid.whitelist4qq.bukkit.BukkitPlugin;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

import static me.dreamvoid.whitelist4qq.bukkit.Config.*;

/**
 * 玩家加入服务器事件
 */
public class PlayerJoin implements Listener {
    private final BukkitPlugin bukkitPlugin;

    public PlayerJoin(BukkitPlugin bukkitPlugin) {
        this.bukkitPlugin = bukkitPlugin;
    }

    @EventHandler
    public void onPlayerJoined(PlayerJoinEvent e) {
        boolean whitelisted = true;
        if (!GEN_UseSelfData) {
            if (MiraiMC.getBind(e.getPlayer().getUniqueId()) == 0) {
                whitelisted = false;
            }
        } else {
            YamlConfiguration white = YamlConfiguration.loadConfiguration(BukkitPlugin.getWhitelist());
            if (GEN_UsePlayerName) {
                List<String> names = white.getStringList("name");
                whitelisted = names.contains(e.getPlayer().getName());
            } else {
                List<String> uuids = white.getStringList("uuid");
                whitelisted = uuids.contains(e.getPlayer().getUniqueId().toString());
            }
        }

        if (!whitelisted) {
            bukkitPlugin.getCache().add(e.getPlayer());
            if (GEN_CheckRange_SPEC) {
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
            }
        }
    }
}
