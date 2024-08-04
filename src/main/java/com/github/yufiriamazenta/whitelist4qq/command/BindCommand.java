package com.github.yufiriamazenta.whitelist4qq.command;

import com.github.yufiriamazenta.whitelist4qq.Whitelist4QQ;
import com.github.yufiriamazenta.whitelist4qq.WhitelistManager;
import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import crypticlib.CrypticLibBukkit;
import crypticlib.chat.BukkitMsgSender;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.command.BukkitCommand;
import crypticlib.command.CommandInfo;
import crypticlib.command.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Command
public class BindCommand extends BukkitCommand {


    public BindCommand() {
        super(new CommandInfo(
            "bind",
            null,
            List.of("bd")));
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull List<String> args) {
        if (!(sender instanceof Player player)) {
            BukkitMsgSender.INSTANCE.sendMsg(sender, Configs.messagesCommandPlayerOnly.value());
            return;
        }
        if (Configs.whitelistMode.value() != 2)
            return;
        if (!WhitelistManager.INSTANCE.isVisitor(player.getUniqueId())) {
            BukkitMsgSender.INSTANCE.sendMsg(player, Configs.messagesCommandBindBound.value());
            return;
        }
        CrypticLibBukkit.scheduler().runTaskAsync(Whitelist4QQ.instance(), () -> {
            UUID uuid = player.getUniqueId();
            String code;
            if (WhitelistManager.INSTANCE.getReverseBindCodeMap().containsKey(uuid)) {
                code = WhitelistManager.INSTANCE.getReverseBindCodeMap().get(uuid);
            } else {
                code = UUID.randomUUID().toString();
                code = code.substring(code.length() - 6);
                WhitelistManager.INSTANCE.addBindCodeCache(code, uuid, player.getName());
            }
            String bindHintMsg = BukkitTextProcessor.color(Configs.messagesCommandBindWait.value().replace("%code%", code));
            CrypticLibBukkit.scheduler().runTask(Whitelist4QQ.instance(), () -> {
                BukkitMsgSender.INSTANCE.sendMsg(player, bindHintMsg);
            });
        });
        return;
    }

}
