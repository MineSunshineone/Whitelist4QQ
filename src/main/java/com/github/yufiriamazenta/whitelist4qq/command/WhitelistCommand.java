package com.github.yufiriamazenta.whitelist4qq.command;

import com.github.yufiriamazenta.whitelist4qq.Whitelist4QQ;
import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import crypticlib.CrypticLib;
import crypticlib.chat.MsgSender;
import crypticlib.chat.TextProcessor;
import crypticlib.command.CommandHandler;
import crypticlib.command.CommandInfo;
import crypticlib.command.SubcommandHandler;
import crypticlib.command.annotation.Command;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;
import me.clip.placeholderapi.util.Msg;
import me.dreamvoid.miraimc.api.MiraiMC;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Command
public class WhitelistCommand extends CommandHandler {

    public WhitelistCommand() {
        super(
                new CommandInfo(
                        "whitelist4qq",
                        new PermInfo("whitelist4qq.command"),
                        new String[]{"qwl", "qwhitelist"},
                        "Whitelist4QQ main command."
                )
        );
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
        Plugin plugin = Whitelist4QQ.instance();
        MsgSender.sendMsg(sender,
                "This server is running "
                + plugin.getDescription().getName()
                + " version "
                + plugin.getDescription().getVersion()
                + " by "
                + plugin.getDescription().getAuthors().toString().replace("[", "").replace("]", "")
                + " (MiraiMC version "
                + Bukkit.getPluginManager().getPlugin("MiraiMC").getDescription().getVersion()
                + ")"
        );
        return true;
    }

    @Subcommand
    public SubcommandHandler reload = new SubcommandHandler("reload") {
        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
            Whitelist4QQ.instance().reloadConfig();
            MsgSender.sendMsg(sender, Configs.messagesCommandReload.value());
            return true;
        }
    }.setPermission("whitelist4qq.command.reload");

    @Subcommand
    public SubcommandHandler remove = new SubcommandHandler("remove") {
        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
            if (args.isEmpty()) {
                MsgSender.sendMsg(sender, Configs.messagesCommandRemoveUsage.value());
                return true;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(args.get(0));
            me.dreamvoid.miraimc.api.MiraiMC.removeBind(player.getUniqueId());
            MsgSender.sendMsg(sender, Configs.messagesCommandRemoveSuccess.value());
            return true;
        }
    }.setPermission("whitelist4qq.command.remove");

    @Subcommand
    public SubcommandHandler getBind = new SubcommandHandler("getbind") {
        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
            if (args.isEmpty()) {
                MsgSender.sendMsg(sender, Configs.messagesCommandGetBindInvalidQQ.value());
                return true;
            }
            try {
                long qq = Long.parseLong(args.get(0));
                MsgSender.sendMsg(sender, Configs.messagesCommandGetBindSelecting.value());
                CrypticLib.platform().scheduler().runTaskAsync(Whitelist4QQ.instance(), () -> {
                    UUID bind = MiraiMC.Bind.getBind(qq);
                    if (bind == null) {
                        MsgSender.sendMsg(sender, Configs.messagesCommandGetBindNotExist.value());
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
                                    new BaseComponent[]{TextProcessor.toComponent(TextProcessor.color(
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
                    MsgSender.sendMsg(sender, answerComponent);
                });
                return true;
            } catch (NumberFormatException e) {
                MsgSender.sendMsg(sender, Configs.messagesCommandGetBindInvalidQQ.value());
                return true;
            }
        }
    }.setPermission("whitelist4qq.command.getbind");

    @Subcommand
    public SubcommandHandler getQQ = new SubcommandHandler("getqq") {
        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
            if (args.isEmpty()) {
                MsgSender.sendMsg(sender, Configs.messagesCommandGetQQInvalidPlayer.value());
                return true;
            }
            MsgSender.sendMsg(sender, Configs.messagesCommandGetQQSelecting.value());
            CrypticLib.platform().scheduler().runTaskAsync(Whitelist4QQ.instance(), () -> {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args.get(0));
                UUID uuid = offlinePlayer.getUniqueId();
                long bind = MiraiMC.Bind.getBind(uuid);
                if (bind == 0) {
                    MsgSender.sendMsg(sender, Configs.messagesCommandGetQQNotExist.value());
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
                                new BaseComponent[]{
                                    TextProcessor.toComponent(TextProcessor.color(Configs.messagesCommandGetQQHover.value()))
                                }
                        ));
                answerComponent.setClickEvent(
                        new ClickEvent(
                                ClickEvent.Action.COPY_TO_CLIPBOARD,
                                bind + ""
                        )
                );
                MsgSender.sendMsg(sender, answerComponent);
            });

            return true;
        }

        @Override
        public @Nullable
        List<String> tab(@NotNull CommandSender sender, @NotNull List<String> args) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }
    }.setPermission("whitelist4qq.command.getqq");

}
