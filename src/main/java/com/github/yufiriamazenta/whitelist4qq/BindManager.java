package com.github.yufiriamazenta.whitelist4qq;

import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import crypticlib.CrypticLibBukkit;
import crypticlib.lifecycle.BukkitEnabler;
import crypticlib.lifecycle.BukkitReloader;
import crypticlib.lifecycle.annotation.OnEnable;
import crypticlib.lifecycle.annotation.OnReload;
import crypticlib.scheduler.CrypticLibRunnable;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@OnEnable
@OnReload
public enum BindManager implements BukkitEnabler, BukkitReloader {

    INSTANCE;

    private final Map<String, UUID> bindCodeMap = new ConcurrentHashMap<>();
    private final Map<String, Long> bindCodeTimeStampMap = new ConcurrentHashMap<>();
    private final Map<UUID, String> bindPlayerNameCache = new ConcurrentHashMap<>();
    private final Map<UUID, String> reverseBindCodeMap = new ConcurrentHashMap<>();
    private final List<UUID> visitors = new CopyOnWriteArrayList<>();
    private CrypticLibRunnable codeTimeoutTask;

    public Map<String, UUID> getBindCodeMap() {
        return bindCodeMap;
    }

    public Map<UUID, String> getReverseBindCodeMap() {
        return reverseBindCodeMap;
    }

    public void addBindCodeCache(String code, UUID uuid, String name) {
        bindCodeMap.put(code, uuid);
        bindPlayerNameCache.put(uuid, name);
        reverseBindCodeMap.put(uuid, code);
        bindCodeTimeStampMap.put(code, System.currentTimeMillis());
    }

    public void removeBindCodeCache(String code) {
        reverseBindCodeMap.remove(bindCodeMap.get(code));
        bindPlayerNameCache.remove(bindCodeMap.get(code));
        bindCodeMap.remove(code);
        bindCodeTimeStampMap.remove(code);
    }

    public boolean isVisitor(UUID uuid) {
        return visitors.contains(uuid);
    }

    public void addToVisitors(UUID uuid){
        visitors.add(uuid);
    }

    public void removeFromVisitors(UUID uuid) {
        visitors.remove(uuid);
    }

    /**
     * 判断是否有白名单
     * @param uuid 判断的uuid
     * @return 1为拥有,0为拥有白名单但不在群内,-1为没有白名单
     */
    public WhitelistManager.WhitelistState getWhitelistState(UUID uuid) {
        long bindQQ = MiraiMC.getBind(uuid);
        if (bindQQ == 0L)
            return WhitelistManager.WhitelistState.NO_WHITELIST;

        if (!Configs.checkQQInGroup.value()) {
            return WhitelistManager.WhitelistState.HAS_WHITELIST;
        }
        for (long bot : Configs.usedBotAccounts.value()) {
            for (Long group : Configs.usedGroups.value()) {
                try {
                    MiraiBot miraiBot = MiraiBot.getBot(bot);
                    MiraiGroup group1 = miraiBot.getGroup(group);
                    if (group1.contains(bindQQ))
                        return WhitelistManager.WhitelistState.HAS_WHITELIST;
                } catch (NoSuchElementException ignored) {}
            }
        }
        return WhitelistManager.WhitelistState.NOT_IN_GROUP;
    }

    public WhitelistManager.WhitelistState getWhitelistState(long qq) {
        UUID bindPlayer = MiraiMC.getBind(qq);
        if (bindPlayer == null) {
            return WhitelistManager.WhitelistState.NO_WHITELIST;
        }

        if (!Configs.checkQQInGroup.value()) {
            return WhitelistManager.WhitelistState.HAS_WHITELIST;
        }
        for (long bot : Configs.usedBotAccounts.value()) {
            for (Long group : Configs.usedGroups.value()) {
                try {
                    MiraiBot miraiBot = MiraiBot.getBot(bot);
                    MiraiGroup group1 = miraiBot.getGroup(group);
                    if (group1.contains(qq))
                        return WhitelistManager.WhitelistState.HAS_WHITELIST;
                } catch (NoSuchElementException ignored) {}
            }
        }
        return WhitelistManager.WhitelistState.NOT_IN_GROUP;
    }

    public List<UUID> visitors() {
        return visitors;
    }

    @Override
    public void enable(Plugin plugin) {
        reload(plugin);
    }

    @Override
    public void reload(Plugin plugin) {
        if (codeTimeoutTask != null)
            codeTimeoutTask.cancel();
        codeTimeoutTask = new CrypticLibRunnable() {
            @Override
            public void run() {
                long timeStamp = System.currentTimeMillis();
                long timeout = Configs.codeTimeoutSecond.value() * 1000;
                for (String key : bindCodeTimeStampMap.keySet()) {
                    if (timeStamp - bindCodeTimeStampMap.get(key) >= timeout) {
                        removeBindCodeCache(key);
                    }
                }
            }
        };
        codeTimeoutTask.runTaskTimer(plugin, 1, 1);
    }
}
