package com.github.yufiriamazenta.whitelist4qq;

import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import crypticlib.BukkitPlugin;
import crypticlib.CrypticLib;
import crypticlib.command.CommandInfo;
import crypticlib.command.impl.RootCmdExecutor;
import crypticlib.util.MsgUtil;
import crypticlib.util.TextUtil;
import me.dreamvoid.miraimc.api.MiraiMC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

import static crypticlib.command.CommandManager.subcommand;

public class Whitelist4QQ extends BukkitPlugin {
    private static Whitelist4QQ INSTANCE;
    private int whitelistMode;

    public Whitelist4QQ() {
        INSTANCE = this;
    }

    public static Whitelist4QQ instance() {
        return INSTANCE;
    }

    @Override
    public void enable() {
        saveDefaultConfig();
        Configs.reload();

        this.whitelistMode = Configs.whitelistMode.value();
        CrypticLib.platform().scheduler().runTaskTimer(this, () -> {
            if (whitelistMode != 2)
                return;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!WhitelistManager.hasVisitTag(player))
                    continue;
                long playedTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
                long allowVisitTime = Configs.mode2AllowVisitSecond.value() * 20;
                if (playedTime >= allowVisitTime) {
                    String code;
                    if (WhitelistManager.getReverseBindCodeMap().containsKey(player.getUniqueId())) {
                        code = WhitelistManager.getReverseBindCodeMap().get(player.getUniqueId());
                    } else {
                        code = UUID.randomUUID().toString();
                        code = code.substring(code.length() - 6);
                        WhitelistManager.addBindCodeCache(code, player.getUniqueId());
                    }
                    String bindHintMsg = TextUtil.color(Configs.messagesKickMessageMode2.value().replace("%code%", code));
                    player.kickPlayer(bindHintMsg);
                }
            }
        }, 1, 20);
        new RootCmdExecutor()
            .regSub(subcommand("reload")
                .setExecutor((sender, args) -> {
                    Configs.reload();
                    this.whitelistMode = Configs.whitelistMode.value();
                    MsgUtil.sendMsg(sender, Configs.messagesCommandReload.value());
                    return true;
                })
                .setPermission("whitelist4qq.command.reload"))
            .regSub(subcommand("remove")
                .setExecutor((sender, args) -> {
                    if (args.isEmpty()) {
                        MsgUtil.sendMsg(sender, Configs.messagesCommandRemoveUsage.value());
                        return true;
                    }
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args.get(0));
                    MiraiMC.removeBind(player.getUniqueId());
                    MsgUtil.sendMsg(sender, Configs.messagesCommandRemoveSuccess.value());
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

    @Override
    public FileConfiguration getConfig() {
        return super.getConfig();
    }

    public int whitelistMode() {
        return whitelistMode;
    }

}
