package me.dreamvoid.whitelist4qq.bukkit;

import me.dreamvoid.miraimc.api.MiraiMC;
import me.dreamvoid.whitelist4qq.bukkit.listener.BotEvent;
import me.dreamvoid.whitelist4qq.bukkit.listener.PlayerActions;
import me.dreamvoid.whitelist4qq.bukkit.listener.PlayerJoin;
import me.dreamvoid.whitelist4qq.bukkit.listener.PlayerLogin;
import me.dreamvoid.whitelist4qq.bukkit.runable.AsyncCheckBind;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.dreamvoid.whitelist4qq.bukkit.Config.*;

public class BukkitPlugin extends JavaPlugin implements Listener {
    private Config config;
    private final ArrayList<Player> cache = new ArrayList<>();
    private static File whitelist;
    private static BukkitPlugin INSTANCE;

    public BukkitPlugin() {
        INSTANCE = this;
    }

    public static BukkitPlugin getInstance() {
        return INSTANCE;
    }

    public ArrayList<Player> getCache() {
        return cache;
    }

    public static File getWhitelist() {
        return whitelist;
    }

    @Override
    public void onLoad() {
        this.config = new Config(this);
        whitelist = new File(getDataFolder(),"whitelist.yml");
    }

    @Override
    public void onEnable() {
        config.loadConfig();
        Bukkit.getPluginManager().registerEvents(new PlayerLogin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
        if(GEN_CheckRange_ACTION) Bukkit.getPluginManager().registerEvents(new PlayerActions(this), this);
        Bukkit.getPluginManager().registerEvents(new BotEvent(), this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new AsyncCheckBind(this, this), 0L, 60L);
        if (GEN_bStats) new Metrics(this, 13112);

        if(GEN_UseSelfData){
            if(!whitelist.exists()) {
                try {
                    whitelist.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("whitelist4qq.command.whitelist4qq.reload")) {
                config.loadConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a配置文件已经重新加载！"));
            } else if(args[0].equalsIgnoreCase("remove") && sender.hasPermission("whitelist4qq.command.whitelist4qq.remove")){
                if(args.length>=2){
                    if(!GEN_UseSelfData){
                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                        MiraiMC.removeBind(player.getUniqueId());
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a已移除指定玩家的绑定！"));
                    } else {
                        YamlConfiguration white = YamlConfiguration.loadConfiguration(whitelist);
                        if(GEN_UsePlayerName){
                            try {
                                List<String> names = white.getStringList("name");
                                names.remove(args[1]);
                                white.set("name",names);
                                white.save(whitelist);
                            } catch (IOException e) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c保存文件时出现异常，原因："+e));
                            }
                        } else try {
                            List<String> uuids = white.getStringList("uuid");
                            uuids.remove(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
                            white.set("uuid",uuids);
                            white.save(whitelist);
                        } catch (IOException e) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c保存文件时出现异常，原因："+e));
                        }
                    }
                } else sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c用法：/whitelist4qq remove <玩家名>"));
            } else {
                sender.sendMessage("This server is running " + getDescription().getName() + " version " + getDescription().getVersion() + " by " + getDescription().getAuthors().toString().replace("[", "").replace("]", "") + " (MiraiMC version " + Bukkit.getPluginManager().getPlugin("MiraiMC").getDescription().getVersion() + ")");
            }
        } else {
            sender.sendMessage("This server is running " + getDescription().getName() + " version " + getDescription().getVersion() + " by " + getDescription().getAuthors().toString().replace("[", "").replace("]", "") + " (MiraiMC version " + Bukkit.getPluginManager().getPlugin("MiraiMC").getDescription().getVersion() + ")");
        }
        return true;
    }

}
