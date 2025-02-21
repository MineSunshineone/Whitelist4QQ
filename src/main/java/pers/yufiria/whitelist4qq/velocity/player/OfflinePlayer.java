package pers.yufiria.whitelist4qq.velocity.player;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.whitelist4qq.velocity.Whitelist4QQ;

import java.nio.file.Watchable;
import java.util.UUID;

public class OfflinePlayer {

    private final UUID uuid;
    private final String name;

    public OfflinePlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID uuid() {
        return uuid;
    }

    public String name() {
        return name;
    }

    @Contract("null -> null; !null -> !null")
    public static OfflinePlayer fromPlayer(@Nullable Player player) {
        if (player == null) return null;
        return new OfflinePlayer(player.getUniqueId(), player.getUsername());
    }

    public boolean isOnline() {
        return Whitelist4QQ.instance().getPlayerOpt(uuid).isPresent();
    }

}
