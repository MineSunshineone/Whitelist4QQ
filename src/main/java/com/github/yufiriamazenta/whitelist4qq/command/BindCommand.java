package com.github.yufiriamazenta.whitelist4qq.command;

import com.github.yufiriamazenta.whitelist4qq.Whitelist4QQ;
import com.github.yufiriamazenta.whitelist4qq.WhitelistManager;
import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import crypticlib.CrypticLib;
import crypticlib.chat.MsgSender;
import crypticlib.chat.TextProcessor;
import crypticlib.command.CommandHandler;
import crypticlib.command.CommandInfo;
import crypticlib.command.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Command
public class BindCommand extends CommandHandler {


    public BindCommand() {
        super(new CommandInfo(
            "bind",
            null,
            new String[]{"bd"}));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
        if (!(sender instanceof Player player)) {
            MsgSender.sendMsg(sender, Configs.messagesCommandPlayerOnly.value());
            return true;
        }
        if (Configs.whitelistMode.value() != 2)
            return true;
        if (!WhitelistManager.isVisitor(player.getUniqueId())) {
            MsgSender.sendMsg(player, Configs.messagesCommandBindBound.value());
            return true;
        }
        CrypticLib.platform().scheduler().runTaskAsync(Whitelist4QQ.instance(), () -> {
            UUID uuid = player.getUniqueId();
            String code;
            if (WhitelistManager.getReverseBindCodeMap().containsKey(uuid)) {
                code = WhitelistManager.getReverseBindCodeMap().get(uuid);
            } else {
                code = UUID.randomUUID().toString();
                code = code.substring(code.length() - 6);
                WhitelistManager.addBindCodeCache(code, uuid, player.getName());
            }
            String bindHintMsg = TextProcessor.color(Configs.messagesCommandBindWait.value().replace("%code%", code));
            CrypticLib.platform().scheduler().runTask(Whitelist4QQ.instance(), () -> {
                MsgSender.sendMsg(player, bindHintMsg);
            });
        });
        return true;
    }

}
