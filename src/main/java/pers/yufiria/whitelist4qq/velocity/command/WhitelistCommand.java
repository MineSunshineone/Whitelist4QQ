package pers.yufiria.whitelist4qq.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import crypticlib.chat.VelocityMsgSender;
import crypticlib.chat.VelocityTextProcessor;
import crypticlib.command.VelocityCommand;
import crypticlib.command.VelocitySubcommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import pers.yufiria.whitelist4qq.velocity.Whitelist4QQ;
import pers.yufiria.whitelist4qq.velocity.config.Configs;
import crypticlib.command.CommandInfo;
import crypticlib.command.annotation.Command;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;
import me.dreamvoid.miraimc.api.MiraiMC;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.whitelist4qq.velocity.player.OfflinePlayer;
import pers.yufiria.whitelist4qq.velocity.player.OfflinePlayerManager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Command
public class WhitelistCommand extends VelocityCommand {
    
    public WhitelistCommand() {
        super(
            new CommandInfo(
                "whitelist4qq",
                new PermInfo("whitelist4qq.command"),
                Arrays.asList("qwl", "qwhitelist"), 
                "Whitelist4QQ main command."
            )
        );
    }

    @Subcommand public VelocitySubcommand reload = new VelocitySubcommand(
        CommandInfo.builder("reload")
            .permission(new PermInfo("whitelist4qq.command.reload"))
            .build()
    ) {
        @Override
        public void execute(@NotNull CommandSource sender, @NotNull List<String> args) {
            Whitelist4QQ.instance().reloadConfig();
            VelocityMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandReload.value());
        }
    };
    
    @Subcommand public VelocitySubcommand remove = new VelocitySubcommand(
        CommandInfo.builder("remove")
            .permission(new PermInfo("whitelist4qq.command.remove"))
            .build()
    ) {
        @Override
        public void execute(@NotNull CommandSource sender, @NotNull List<String> args) {
            if (args.isEmpty()) {
                VelocityMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandRemoveUsage.value());
                return;
            }
            OfflinePlayer offlinePlayer = OfflinePlayerManager.INSTANCE.getOfflinePlayer(args.get(0));
            if (offlinePlayer == null) {
                VelocityMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandRemoveInvalidPlayer.value(), Map.of("%player_name%", args.get(0)));
                return;
            }
            MiraiMC.removeBind(offlinePlayer.uuid());
            VelocityMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandRemoveSuccess.value());
        }
    };
    
    @Subcommand public VelocitySubcommand getBind = new VelocitySubcommand(
        CommandInfo.builder("getbind")
            .permission(new PermInfo("whitelist4qq.command.getbind"))
            .build()
    ) {
        @Override
        public void execute(@NotNull CommandSource sender, @NotNull List<String> args) {
            if (args.isEmpty()) {
                VelocityMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandGetBindInvalidQQ.value());
                return;
            }
            try {
                long qq = Long.parseLong(args.get(0));
                VelocityMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandGetBindSelecting.value());
                Whitelist4QQ.instance().buildTask(() -> {
                    UUID bind = MiraiMC.getBind(qq);
                    if (bind == null) {
                        VelocityMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandGetBindNotExist.value());
                        return;
                    }
                    OfflinePlayer offlinePlayer = OfflinePlayerManager.INSTANCE.getOfflinePlayer(bind);
                    String name = offlinePlayer.name();
                    String getBindAnswer = Configs.messagesCommandGetBindSuccess
                        .value()
                        .replace("%qq%", args.get(0))
                        .replace("%player%", name);
                    Component answerComponent = VelocityTextProcessor.toComponent(getBindAnswer);
                    answerComponent = answerComponent.hoverEvent(
                        VelocityTextProcessor.toComponent(Configs.messagesCommandGetBindHover.value())
                    );
                    answerComponent = answerComponent.clickEvent(
                        ClickEvent.clickEvent(
                            ClickEvent.Action.COPY_TO_CLIPBOARD,
                            name
                        )
                    );
                    VelocityMsgSender.INSTANCE.sendMsg(sender, answerComponent);
                }).schedule();
            } catch (NumberFormatException e) {
                VelocityMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandGetBindInvalidQQ.value());
            }
        }
    };

    @Subcommand public VelocitySubcommand getQQ = new VelocitySubcommand(
        CommandInfo.builder("getqq")
            .permission(new PermInfo("whitelist4qq.command.getqq"))
            .build()
    ) {
        @Override
        public void execute(@NotNull CommandSource sender, @NotNull List<String> args) {
            if (args.isEmpty()) {
                VelocityMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandGetQQInvalidPlayer.value());
                return;
            }
            VelocityMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandGetQQSelecting.value());

            Whitelist4QQ.instance().buildTask(() -> {
                OfflinePlayer offlinePlayer = OfflinePlayerManager.INSTANCE.getOfflinePlayer(args.get(0));
                if (offlinePlayer == null) {
                    VelocityMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandGetQQInvalidPlayer.value());
                    return;
                }
                UUID uuid = offlinePlayer.uuid();
                long bind = MiraiMC.getBind(uuid);
                if (bind == 0) {
                    VelocityMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandGetQQNotExist.value());
                    return;
                }
                String getQQAnswer = Configs.messagesCommandGetQQSuccess
                    .value()
                    .replace("%qq%", bind + "")
                    .replace("%player%", args.get(0));
                Component answerComponent = VelocityTextProcessor.toComponent(getQQAnswer);
                answerComponent = answerComponent.hoverEvent(VelocityTextProcessor.toComponent(Configs.messagesCommandGetQQHover.value()));
                answerComponent = answerComponent.clickEvent(
                    ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, bind + "")
                );
                VelocityMsgSender.INSTANCE.sendMsg(sender, answerComponent);
            }).schedule();
        }

        @Override
        public @NotNull List<String> tab(@NotNull CommandSource sender, @NotNull List<String> args) {
            return Whitelist4QQ.instance().getAllPlayers().stream().map(Player::getUsername).collect(Collectors.toList());
        }
    };

}

