package com.github.yufiriamazenta.whitelist4qq;

import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import com.github.yufiriamazenta.whitelist4qq.papi.Whitelist4QQExpansion;
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

import java.util.*;

import static crypticlib.command.CommandManager.subcommand;

public class Whitelist4QQ extends BukkitPlugin {
    private static Whitelist4QQ INSTANCE;
    private int taskRunSecond = 0;

    public Whitelist4QQ() {
        INSTANCE = this;
    }

    public static Whitelist4QQ instance() {
        return INSTANCE;
    }

    @Override
    public void enable() {
        CrypticLib.platform().scheduler().runTaskTimer(this, () -> {
            if (Configs.whitelistMode.value() != 2)
                return;
            Set<UUID> tmpUuids = new HashSet<>();
            for (UUID uuid : WhitelistManager.visitors()) {
                Player visitor = Bukkit.getPlayer(uuid);
                if (visitor == null || !visitor.isOnline()) {
                    tmpUuids.add(uuid);
                    continue;
                }
                visitor.setGameMode(GameMode.ADVENTURE);
                long playedTime = visitor.getStatistic(Statistic.PLAY_ONE_MINUTE);
                long allowVisitTime = Configs.mode2AllowVisitSecond.value() * 20;
                if (taskRunSecond >= Configs.mode2HintCd.value()) {
                    MsgUtil.sendMsg(visitor, Configs.messagesMode2BindHintMessage.value());
                    taskRunSecond = 0;
                }

                if (playedTime >= allowVisitTime) {
                    String code;
                    if (WhitelistManager.getReverseBindCodeMap().containsKey(visitor.getUniqueId())) {
                        code = WhitelistManager.getReverseBindCodeMap().get(visitor.getUniqueId());
                    } else {
                        code = UUID.randomUUID().toString();
                        code = code.substring(code.length() - 6);
                        WhitelistManager.addBindCodeCache(code, visitor.getUniqueId());
                    }
                    String bindHintMsg = TextUtil.color(Configs.messagesKickMessageMode2.value().replace("%code%", code));
                    visitor.kickPlayer(bindHintMsg);
                }
            }
            WhitelistManager.visitors().removeAll(tmpUuids);
            taskRunSecond++;
        }, 1, 20);
        new RootCmdExecutor()
            .regSub(subcommand("reload")
                .setExecutor((sender, args) -> {
                    reloadConfig();
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
            .regSub(subcommand("getbind")
                .setExecutor((sender, args) -> {
                    if (args.isEmpty()) {
                        MsgUtil.sendMsg(sender, Configs.messagesCommandGetBindInvalidQQ.value());
                        return true;
                    }
                    try {
                        long qq = Long.parseLong(args.get(0));
                        MsgUtil.sendMsg(sender, Configs.messagesCommandGetBindSelecting.value());
                        CrypticLib.platform().scheduler().runTaskAsync(this, () -> {
                            UUID bind = MiraiMC.getBind(qq);
                            if (bind == null) {
                                MsgUtil.sendMsg(sender, Configs.messagesCommandGetBindNotExist.value());
                                return;
                            }
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(bind);
                            String name;
                            if (offlinePlayer.getName() == null) {
                                name = bind.toString();
                            } else {
                                name = offlinePlayer.getName();
                            }
                            MsgUtil.sendMsg(sender, Configs.messagesCommandGetBindSuccess.value(), Map.of("%qq%", args.get(0), "%player%", name));
                        });
                        return true;
                    } catch (NumberFormatException e) {
                        MsgUtil.sendMsg(sender, Configs.messagesCommandGetBindInvalidQQ.value());
                        return true;
                    }
                })
                .setPermission("whitelist4qq.command.getbind"))
            .regSub(subcommand("getqq")
                .setExecutor((sender, args) -> {
                    if (args.isEmpty()) {
                        MsgUtil.sendMsg(sender, Configs.messagesCommandGetQQInvalidPlayer.value());
                        return true;
                    }
                    MsgUtil.sendMsg(sender, Configs.messagesCommandGetQQSelecting.value());
                    CrypticLib.platform().scheduler().runTaskAsync(this, () -> {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args.get(0));
                        UUID uuid = offlinePlayer.getUniqueId();
                        long bind = MiraiMC.getBind(uuid);
                        if (bind == 0) {
                            MsgUtil.sendMsg(sender, Configs.messagesCommandGetQQNotExist.value());
                            return;
                        }

                        MsgUtil.sendMsg(sender, Configs.messagesCommandGetQQSuccess.value(), Map.of("%qq%", bind + "", "%player%", args.get(0)));
                    });

                    return true;
                })
                .setPermission("whitelist4qq.command.getqq"))
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
                if (Configs.whitelistMode.value() != 2)
                    return true;
                Player player = (Player) sender;
                if (!WhitelistManager.isVisitor(player.getUniqueId())) {
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
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceHolderAPI")) {
            new Whitelist4QQExpansion().register();
        }
    }

    @Override
    public void disable() {
    }

}
