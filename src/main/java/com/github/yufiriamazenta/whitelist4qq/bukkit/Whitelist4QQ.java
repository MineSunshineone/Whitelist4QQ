package com.github.yufiriamazenta.whitelist4qq.bukkit;

import com.github.yufiriamazenta.lib.ParettiaLib;
import com.github.yufiriamazenta.whitelist4qq.bukkit.listener.BotListener;
import com.github.yufiriamazenta.whitelist4qq.bukkit.listener.PlayerListener;
import me.dreamvoid.miraimc.api.MiraiMC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Whitelist4QQ extends JavaPlugin implements Listener {
    private static File whitelist;
    private static Whitelist4QQ INSTANCE;

    public Whitelist4QQ() {
        INSTANCE = this;
    }

    public static Whitelist4QQ getInstance() {
        return INSTANCE;
    }

    public static File getWhitelist() {
        return whitelist;
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
        whitelist = new File(getDataFolder(),"whitelist.yml");
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(PlayerListener.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(BotListener.INSTANCE, this);
    }

    @Override
    public void onDisable() {
        ParettiaLib.INSTANCE.getPlatform().cancelTasks(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("whitelist4qq.command.whitelist4qq.reload")) {
                reloadConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a配置文件已经重新加载！"));
            } else if(args[0].equalsIgnoreCase("remove") && sender.hasPermission("whitelist4qq.command.whitelist4qq.remove")){
                if(args.length>=2){
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    MiraiMC.removeBind(player.getUniqueId());
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a已移除指定玩家的绑定！"));
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
