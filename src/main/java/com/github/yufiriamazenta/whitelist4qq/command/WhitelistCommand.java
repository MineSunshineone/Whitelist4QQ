package com.github.yufiriamazenta.whitelist4qq.command;

import com.github.yufiriamazenta.whitelist4qq.Whitelist4QQ;
import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import crypticlib.CrypticLibBukkit;
import crypticlib.chat.BukkitMsgSender;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.command.BukkitCommand;
import crypticlib.command.CommandInfo;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.annotation.Command;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Command
public class WhitelistCommand extends BukkitCommand {
    
    public WhitelistCommand() {
        super(
            new CommandInfo(
                "whitelist4qq",
                new PermInfo("whitelist4qq.command"),
                Arrays.asList("qwl", "qwhitelist"),
                "Whitelist4QQ main command.",
                "/whitelist4qq|qwl|qwhitelist <...>"
            )
        );
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull List<String> args) {
        Plugin plugin = Whitelist4QQ.instance();
        BukkitMsgSender.INSTANCE.sendMsg(sender,
            "This server is running " +
                plugin.getDescription().getName() +
                " version " +
                plugin.getDescription().getVersion() +
                " by " +
                plugin.getDescription().getAuthors().toString().replace("[", "").replace("]", "") +
                " (MiraiMC version " +
                Bukkit.getPluginManager().getPlugin("MiraiMC").getDescription().getVersion() +
                ")"
        );
    }

    @Subcommand public BukkitSubcommand reload = new BukkitSubcommand("reload") {
        @Override
        public void execute(@NotNull CommandSender sender, @NotNull List<String> args) {
            Whitelist4QQ.instance().reloadConfig();
            BukkitMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandReload.value());
        }
    }.setPermission("whitelist4qq.command.reload");
    
    @Subcommand public BukkitSubcommand remove = new BukkitSubcommand("remove") {
        @Override
        public void execute(@NotNull CommandSender sender, @NotNull List<String> args) {
            if (args.isEmpty()) {
                BukkitMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandRemoveUsage.value());
                return;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(args.get(0));
            MiraiMC.removeBind(player.getUniqueId());
            BukkitMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandRemoveSuccess.value());
        }
    }.setPermission("whitelist4qq.command.remove");
    
    @Subcommand public BukkitSubcommand getBind = new BukkitSubcommand("getbind") {
        @Override
        public void execute(@NotNull CommandSender sender, @NotNull List<String> args) {
            if (args.isEmpty()) {
                BukkitMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandGetBindInvalidQQ.value());
                return;
            }
            try {
                long qq = Long.parseLong(args.get(0));
                BukkitMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandGetBindSelecting.value());
                CrypticLibBukkit.scheduler().runTaskAsync(Whitelist4QQ.instance(), () -> {
                    UUID bind = MiraiMC.getBind(qq);
                    if (bind == null) {
                        BukkitMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandGetBindNotExist.value());
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
                    BaseComponent answerComponent = BukkitTextProcessor.toComponent(BukkitTextProcessor.color(getBindAnswer));
                    answerComponent.setHoverEvent(
                        new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new BaseComponent[] {BukkitTextProcessor.toComponent(BukkitTextProcessor.color(
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
                    BukkitMsgSender.INSTANCE.sendMsg(sender, answerComponent);
                });
            } catch (NumberFormatException e) {
                BukkitMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandGetBindInvalidQQ.value());
            }
        }
    }.setPermission("whitelist4qq.command.getbind");

    @Subcommand public BukkitSubcommand getQQ = new BukkitSubcommand("getqq") {
        @Override
        public void execute(@NotNull CommandSender sender, @NotNull List<String> args) {
            if (args.isEmpty()) {
                BukkitMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandGetQQInvalidPlayer.value());
                return;
            }
            BukkitMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandGetQQSelecting.value());
            CrypticLibBukkit.scheduler().runTaskAsync(Whitelist4QQ.instance(), () -> {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args.get(0));
                UUID uuid = offlinePlayer.getUniqueId();
                long bind = MiraiMC.getBind(uuid);
                if (bind == 0) {
                    BukkitMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandGetQQNotExist.value());
                    return;
                }
                String getQQAnswer = Configs.messagesCommandGetQQSuccess
                    .value()
                    .replace("%qq%", bind + "")
                    .replace("%player%", args.get(0));
                BaseComponent answerComponent = BukkitTextProcessor.toComponent(BukkitTextProcessor.color(getQQAnswer));
                answerComponent.setHoverEvent(
                    new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new BaseComponent[]{
                            BukkitTextProcessor.toComponent(BukkitTextProcessor.color(Configs.messagesCommandGetQQHover.value()))
                        }
                    ));
                answerComponent.setClickEvent(
                    new ClickEvent(
                        ClickEvent.Action.COPY_TO_CLIPBOARD,
                        bind + ""
                    )
                );
                BukkitMsgSender.INSTANCE.sendMsg(sender, answerComponent);
            });
        }

        @Override
        public @Nullable List<String> tab(@NotNull CommandSender sender, @NotNull List<String> args) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }
    }.setPermission("whitelist4qq.command.getqq");



}

