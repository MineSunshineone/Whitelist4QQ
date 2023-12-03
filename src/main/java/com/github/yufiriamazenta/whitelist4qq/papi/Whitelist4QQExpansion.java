package com.github.yufiriamazenta.whitelist4qq.papi;

import com.github.yufiriamazenta.whitelist4qq.Whitelist4QQ;
import com.github.yufiriamazenta.whitelist4qq.WhitelistManager;
import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import crypticlib.util.TextUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Whitelist4QQExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "whitelist4qq";
    }

    @Override
    public @NotNull String getAuthor() {
        return "YufiriaMazenta";
    }

    @Override
    public @NotNull String getVersion() {
        return Whitelist4QQ.instance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        switch (params) {
            case "visitor_tag":
                UUID uuid = player.getUniqueId();
                if (WhitelistManager.isVisitor(uuid)) {
                    return TextUtil.color(Configs.messagesPlaceholderVisitorTag.value());
                } else {
                    return null;
                }
            case "whitelist_state":
                return WhitelistManager.getWhitelistState(player.getUniqueId()).toString().toLowerCase();
            case "bind_code":
                return WhitelistManager.getReverseBindCodeMap().get(player.getUniqueId());
            default:
                return null;
        }
    }
}
