package com.github.yufiriamazenta.whitelist4qq;

import crypticlib.BukkitPlugin;
import crypticlib.command.CommandInfo;
import crypticlib.command.impl.RootCmdExecutor;
import crypticlib.util.MsgUtil;
import me.dreamvoid.miraimc.api.MiraiMC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;

import static crypticlib.command.CommandManager.subcommand;

public class Whitelist4QQ extends BukkitPlugin {
    private static File whitelist;
    private static Whitelist4QQ INSTANCE;
    private int whitelistMode;


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
    public void enable() {
        this.whitelistMode = getConfig().getInt("general.whitelist_mode", 1);
        new RootCmdExecutor()
            .regSub(subcommand("reload")
                .setExecutor((sender, args) -> {
                    reloadConfig();
                    this.whitelistMode = getConfig().getInt("general.whitelist_mode", 1);
                    MsgUtil.sendMsg(sender, "&a配置文件已经重新加载！");
                    return true;
                })
                .setPermission("whitelist4qq.command.reload"))
            .regSub(subcommand("remove")
                .setExecutor((sender, args) -> {
                    if (args.isEmpty()) {
                        MsgUtil.sendMsg(sender, "&c用法：/whitelist4qq remove <玩家名>");
                        return true;
                    }
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args.get(0));
                    MiraiMC.removeBind(player.getUniqueId());
                    MsgUtil.sendMsg(sender, "&a已移除指定玩家的绑定！");
                    return true;
                }).setPermission("whitelist4qq.command.remove"))
            .setExecutor((sender, args) -> {
                MsgUtil.sendMsg(sender, "This server is running " + getDescription().getName() + " version " + getDescription().getVersion() + " by " + getDescription().getAuthors().toString().replace("[", "").replace("]", "") + " (MiraiMC version " + Bukkit.getPluginManager().getPlugin("MiraiMC").getDescription().getVersion() + ")");
                return true;
            }).register(this, new CommandInfo(
                "whitelist4qq",
                "whitelist4qq.command",
                new String[]{"qwl", "qwhitelist"},
                "Whitelist4QQ main command.")
            );
    }

    @Override
    public void disable() {
    }

    public int whitelistMode() {
        return whitelistMode;
    }

}
