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
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.UUID;

import static crypticlib.command.CommandManager.subcommand;

public class Whitelist4QQ extends BukkitPlugin {
    private static Whitelist4QQ INSTANCE;
    private int whitelistMode;
    private int taskRunSecond = 0;

    public Whitelist4QQ() {
        INSTANCE = this;
    }

    public static Whitelist4QQ instance() {
        return INSTANCE;
    }

    @Override
    public void enable() {
        this.whitelistMode = Configs.whitelistMode.value();
        CrypticLib.platform().scheduler().runTaskTimer(this, () -> {
            if (whitelistMode != 2)
                return;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!WhitelistManager.hasVisitTag(player))
                    continue;
                player.setGameMode(GameMode.ADVENTURE);
                long playedTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
                long allowVisitTime = Configs.mode2AllowVisitSecond.value() * 20;
                if (taskRunSecond >= Configs.mode2HintCd.value()) {
                    MsgUtil.sendMsg(player, Configs.messagesMode2BindHintMessage.value());
                    taskRunSecond = 0;
                }

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
            taskRunSecond++;
        }, 1, 20);
        new RootCmdExecutor()
            .regSub(subcommand("reload")
                .setExecutor((sender, args) -> {
                    reloadConfig();
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
        new RootCmdExecutor()
            .setExecutor((sender, args) -> {
                if (!(sender instanceof Player)) {
                    MsgUtil.sendMsg(sender, Configs.messagesCommandPlayerOnly.value());
                    return true;
                }
                if (whitelistMode != 2)
                    return true;
                Player player = (Player) sender;
                if (!WhitelistManager.hasVisitTag(player)) {
                    MsgUtil.sendMsg(player, Configs.messagesCommandBindBound.value());
                    return true;
                }
                CrypticLib.platform().scheduler().runTaskAsync(this, () -> {
                    UUID uuid = player.getUniqueId();
                    String code;
                    if (WhitelistManager.getReverseBindCodeMap().containsKey(uuid)) {
                        code = WhitelistManager.getReverseBindCodeMap().get(uuid);
                    } else {
                        code = UUID.randomUUID().toString();
                        code = code.substring(code.length() - 6);
                        WhitelistManager.addBindCodeCache(code, uuid);
                    }
                    String bindHintMsg = TextUtil.color(Configs.messagesCommandBindWait.value().replace("%code%", code));
                    CrypticLib.platform().scheduler().runTask(this, () -> {
                        MsgUtil.sendMsg(player, bindHintMsg);
                    });
                });
                return true;
            }).register(this, new CommandInfo(
                "bind",
            null,
            new String[]{"bd"})
            );
    }

    @Override
    public void disable() {
    }

    public int whitelistMode() {
        return whitelistMode;
    }

}
