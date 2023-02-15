package me.dreamvoid.whitelist4qq.bukkit.runable;

import me.dreamvoid.miraimc.api.MiraiMC;
import me.dreamvoid.whitelist4qq.bukkit.BukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.regex.Pattern;

import static me.dreamvoid.whitelist4qq.bukkit.Config.*;


public class AsyncCheckBind implements Runnable {
    private final BukkitPlugin bukkitPlugin;
    private final BukkitPlugin plugin;

    public AsyncCheckBind(BukkitPlugin bukkitPlugin, BukkitPlugin plugin) {
        this.bukkitPlugin = bukkitPlugin;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (bukkitPlugin.getCache().size() > 0) {
            for (Player player : bukkitPlugin.getCache()) {
                if (!GEN_UseSelfData) {
                    if (MiraiMC.getBind(player.getUniqueId()) != 0) {
                        bukkitPlugin.getCache().remove(player);
                        if (GEN_CheckRange_SPEC) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.setGameMode(Bukkit.getDefaultGameMode());
                                }
                            }.runTask(plugin);
                        }
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', GEN_BindSuccessMessage));
                    } else player.sendMessage(ChatColor.translateAlternateColorCodes('&', GEN_NotifyMessage));
                } else {
                    YamlConfiguration white = YamlConfiguration.loadConfiguration(BukkitPlugin.getWhitelist());
                    if (GEN_UsePlayerName) {
                        List<String> names = white.getStringList("name");
                        if (names.contains(player.getName())) {
                            bukkitPlugin.getCache().remove(player);
                            if (GEN_CheckRange_SPEC) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        player.setGameMode(Bukkit.getDefaultGameMode());
                                    }
                                }.runTask(plugin);
                            }
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', GEN_BindSuccessMessage));
                        }
                    } else {
                        List<String> uuids = white.getStringList("uuid");
                        if (uuids.contains(player.getUniqueId().toString())) {
                            bukkitPlugin.getCache().remove(player);
                            if (GEN_CheckRange_SPEC) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        player.setGameMode(Bukkit.getDefaultGameMode());
                                    }
                                }.runTask(plugin);
                            }
                        }
                    }
                }
            }
        }
    }
}
