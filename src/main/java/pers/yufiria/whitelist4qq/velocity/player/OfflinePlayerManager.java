package pers.yufiria.whitelist4qq.velocity.player;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.proxy.Player;
import crypticlib.VelocityPlugin;
import crypticlib.config.VelocityConfigWrapper;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.lifecycle.VelocityLifeCycleTask;
import crypticlib.listener.EventListener;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.whitelist4qq.velocity.Whitelist4QQ;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE)
    }
)
@EventListener
public enum OfflinePlayerManager implements VelocityLifeCycleTask {

    INSTANCE;

    private VelocityConfigWrapper userCacheFile;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final Cache<UUID, OfflinePlayer> uuidOfflinePlayerCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    private final Cache<String, OfflinePlayer> nameOfflinePlayerCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    @Subscribe
    public void onPlayerJoin(PreLoginEvent event) {
        UUID playerId = event.getUniqueId();
        String playerName = event.getUsername();
        OfflinePlayer offlinePlayer = getOfflinePlayer(playerId);
        if (offlinePlayer == null) {
            addOfflinePlayer(new OfflinePlayer(playerId, playerName));
            return;
        }
        if (offlinePlayer.name().equals(playerName)) {
            return;
        }
        //如果存储的名字和玩家加入的名字不一样,说明他改名了,要及时更新数据
        changeName(playerId, offlinePlayer.name(), playerName);
    }

    public @Nullable OfflinePlayer getOfflinePlayer(String name) {
        Optional<Player> playerOpt = Whitelist4QQ.instance().getPlayerOpt(name);
        if (playerOpt.isPresent()) {
            return OfflinePlayer.fromPlayer(playerOpt.get());
        }
        OfflinePlayer cacheOfflinePlayer = nameOfflinePlayerCache.getIfPresent(name);
        if (cacheOfflinePlayer != null) {
            return cacheOfflinePlayer;
        }
        lock.readLock().lock();
        try {
            if (userCacheFile.contains(name)) {
                UUID uuid = UUID.fromString(userCacheFile.config().get(name));
                OfflinePlayer offlinePlayer = new OfflinePlayer(uuid, name);
                nameOfflinePlayerCache.put(name, offlinePlayer);
                return offlinePlayer;
            } else {
                return null;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public @Nullable OfflinePlayer getOfflinePlayer(UUID uuid) {
        Optional<Player> playerOpt = Whitelist4QQ.instance().getPlayerOpt(uuid);
        if (playerOpt.isPresent()) {
            return OfflinePlayer.fromPlayer(playerOpt.get());
        }
        OfflinePlayer cacheOfflinePlayer = uuidOfflinePlayerCache.getIfPresent(uuid);
        if (cacheOfflinePlayer != null) {
            return cacheOfflinePlayer;
        }
        lock.readLock().lock();
        try {
            CommentedFileConfig config = userCacheFile.config();
            for (CommentedConfig.Entry entry : config.entrySet()) {
                UUID configUuid = UUID.fromString(entry.getValue().toString());
                if (configUuid.equals(uuid)) {
                    OfflinePlayer offlinePlayer = new OfflinePlayer(configUuid, entry.getKey());
                    uuidOfflinePlayerCache.put(uuid, offlinePlayer);
                    return offlinePlayer;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addOfflinePlayer(OfflinePlayer offlinePlayer) {
        lock.writeLock().lock();
        try {
            nameOfflinePlayerCache.put(offlinePlayer.name(), offlinePlayer);
            uuidOfflinePlayerCache.put(offlinePlayer.uuid(), offlinePlayer);
            userCacheFile.set(offlinePlayer.name(), offlinePlayer.uuid().toString());
            userCacheFile.saveConfig();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void changeName(UUID uuid, String oldName, String newName) {
        lock.writeLock().lock();
        try {
            nameOfflinePlayerCache.invalidate(oldName);
            uuidOfflinePlayerCache.invalidate(uuid);
            OfflinePlayer offlinePlayer = new OfflinePlayer(uuid, oldName);
            CommentedFileConfig config = userCacheFile.config();
            config.remove(oldName);
            config.set(newName, uuid.toString());
            nameOfflinePlayerCache.put(offlinePlayer.name(), offlinePlayer);
            uuidOfflinePlayerCache.put(offlinePlayer.uuid(), offlinePlayer);
            userCacheFile.set(offlinePlayer.name(), offlinePlayer.uuid().toString());
            userCacheFile.saveConfig();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void run(VelocityPlugin velocityPlugin, LifeCycle lifeCycle) {
        userCacheFile = new VelocityConfigWrapper(velocityPlugin, "user_cache.yml");
        userCacheFile.reloadConfig();
    }

}
