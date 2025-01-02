package com.github.yufiriamazenta.whitelist4qq;

import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import com.github.yufiriamazenta.whitelist4qq.papi.Whitelist4QQExpansion;
import crypticlib.BukkitPlugin;
import crypticlib.CrypticLib;
import crypticlib.chat.MsgSender;
import crypticlib.chat.TextProcessor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
                    MsgSender.sendMsg(visitor, Configs.messagesMode2BindHintMessage.value());
                    taskRunSecond = 0;
                }

                if (playedTime >= allowVisitTime) {
                    String code;
                    if (WhitelistManager.getReverseBindCodeMap().containsKey(visitor.getUniqueId())) {
                        code = WhitelistManager.getReverseBindCodeMap().get(visitor.getUniqueId());
                    } else {
                        code = UUID.randomUUID().toString();
                        code = code.substring(code.length() - 6);
                        WhitelistManager.addBindCodeCache(code, visitor.getUniqueId(), visitor.getName());
                    }
                    String bindHintMsg = TextProcessor.color(Configs.messagesKickMessageMode2.value().replace("%code%", code));
                    visitor.kickPlayer(bindHintMsg);
                }
            }
            WhitelistManager.visitors().removeAll(tmpUuids);
            taskRunSecond++;
        }, 1, 20);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceHolderAPI")) {
            new Whitelist4QQExpansion().register();
        }
    }

    @Override
    public void disable() {
    }

}
