package com.github.yufiriamazenta.whitelist4qq;

import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import com.github.yufiriamazenta.whitelist4qq.papi.Whitelist4QQExpansion;
import crypticlib.BukkitPlugin;
import crypticlib.CrypticLib;
import crypticlib.chat.MessageSender;
import crypticlib.chat.TextProcessor;
import crypticlib.command.CommandInfo;
import crypticlib.command.impl.RootCmdExecutor;
import me.dreamvoid.miraimc.api.MiraiMC;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static crypticlib.command.CommandManager.subcommand;

public class Whitelist4QQ extends BukkitPlugin {
    private static Whitelist4QQ INSTANCE;
    private int taskRunSecond = 0;

    public Whitelist4QQ() {
        INSTANCE = this;
        setHighestSupportVersion(Integer.MAX_VALUE);
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
                    MessageSender.sendMsg(visitor, Configs.messagesMode2BindHintMessage.value());
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
                    String bindHintMsg = TextProcessor.color(Configs.messagesKickMessageMode2.value().replace("%code%", code));
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
                    MessageSender.sendMsg(sender, Configs.messagesCommandReload.value());
                    return true;
                })
                .setPermission("whitelist4qq.command.reload"))
            .regSub(subcommand("remove")
                .setExecutor((sender, args) -> {
                    if (args.isEmpty()) {
                        MessageSender.sendMsg(sender, Configs.messagesCommandRemoveUsage.value());
                        return true;
                    }
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args.get(0));
                    MiraiMC.removeBind(player.getUniqueId());
                    MessageSender.sendMsg(sender, Configs.messagesCommandRemoveSuccess.value());
                    return true;
                }).setPermission("whitelist4qq.command.remove"))
            .regSub(subcommand("getbind")
                .setExecutor((sender, args) -> {
                    if (args.isEmpty()) {
                        MessageSender.sendMsg(sender, Configs.messagesCommandGetBindInvalidQQ.value());
                        return true;
                    }
                    try {
                        long qq = Long.parseLong(args.get(0));
                        MessageSender.sendMsg(sender, Configs.messagesCommandGetBindSelecting.value());
                        CrypticLib.platform().scheduler().runTaskAsync(this, () -> {
                            UUID bind = MiraiMC.getBind(qq);
                            if (bind == null) {
                                MessageSender.sendMsg(sender, Configs.messagesCommandGetBindNotExist.value());
                                return;
                            }
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(bind);
                            String name;
                            if (offlinePlayer.getName() == null) {
                                name = bind.toString();
                            } else {
                                name = offlinePlayer.getName();
                            }
                            String getBindAnswer = Configs.messagesCommandGetBindSuccess
                                .value()
                                .replace("%qq%", args.get(0))
                                .replace("%player%", name);
                            BaseComponent answerComponent = TextProcessor.toComponent(TextProcessor.color(getBindAnswer));
                            answerComponent.setHoverEvent(
                                new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    new BaseComponent[] {TextProcessor.toComponent(TextProcessor.color(
                                        Configs.messagesCommandGetBindHover.value()
                                    ))}
                                )
                            );
                            answerComponent.setClickEvent(
                                new ClickEvent(
                                    ClickEvent.Action.COPY_TO_CLIPBOARD,
                                    name
                                )
                            );
                            MessageSender.sendMsg(sender, answerComponent);
                        });
                        return true;
                    } catch (NumberFormatException e) {
                        MessageSender.sendMsg(sender, Configs.messagesCommandGetBindInvalidQQ.value());
                        return true;
                    }
                })
                .setPermission("whitelist4qq.command.getbind"))
            .regSub(subcommand("getqq")
                .setExecutor((sender, args) -> {
                    if (args.isEmpty()) {
                        MessageSender.sendMsg(sender, Configs.messagesCommandGetQQInvalidPlayer.value());
                        return true;
                    }
                    MessageSender.sendMsg(sender, Configs.messagesCommandGetQQSelecting.value());
                    CrypticLib.platform().scheduler().runTaskAsync(this, () -> {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args.get(0));
                        UUID uuid = offlinePlayer.getUniqueId();
                        long bind = MiraiMC.getBind(uuid);
                        if (bind == 0) {
                            MessageSender.sendMsg(sender, Configs.messagesCommandGetQQNotExist.value());
                            return;
                        }
                        String getQQAnswer = Configs.messagesCommandGetQQSuccess
                            .value()
                            .replace("%qq%", bind + "")
                            .replace("%player%", args.get(0));
                        BaseComponent answerComponent = TextProcessor.toComponent(TextProcessor.color(getQQAnswer));
                        answerComponent.setHoverEvent(
                            new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new BaseComponent[] {
                                    TextProcessor.toComponent(TextProcessor.color(Configs.messagesCommandGetQQHover.value()))
                                }
                            ));
                        answerComponent.setClickEvent(
                            new ClickEvent(
                                ClickEvent.Action.COPY_TO_CLIPBOARD,
                                bind + ""
                            )
                        );
                        MessageSender.sendMsg(sender, answerComponent);
                    });

                    return true;
                })
                .setPermission("whitelist4qq.command.getqq")
                .setTabCompleter(() -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList())))
            .setExecutor((sender, args) -> {
                MessageSender.sendMsg(sender, "This server is running " + getDescription().getName() + " version " + getDescription().getVersion() + " by " + getDescription().getAuthors().toString().replace("[", "").replace("]", "") + " (MiraiMC version " + Bukkit.getPluginManager().getPlugin("MiraiMC").getDescription().getVersion() + ")");
                return true;
            }).register(this, new CommandInfo(
                "whitelist4qq",
                "whitelist4qq.command",
                new String[]{"qwl", "qwhitelist"},
                "Whitelist4QQ main command.")
            );
        new RootCmdExecutor()
            .setExecutor((sender, args) -> {
                if (!(sender instanceof Player player)) {
                    MessageSender.sendMsg(sender, Configs.messagesCommandPlayerOnly.value());
                    return true;
                }
                if (Configs.whitelistMode.value() != 2)
                    return true;
                if (!WhitelistManager.isVisitor(player.getUniqueId())) {
                    MessageSender.sendMsg(player, Configs.messagesCommandBindBound.value());
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
                    String bindHintMsg = TextProcessor.color(Configs.messagesCommandBindWait.value().replace("%code%", code));
                    CrypticLib.platform().scheduler().runTask(this, () -> {
                        MessageSender.sendMsg(player, bindHintMsg);
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
