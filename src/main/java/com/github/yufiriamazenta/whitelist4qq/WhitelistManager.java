package com.github.yufiriamazenta.whitelist4qq;

import crypticlib.CrypticLib;
import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.api.MiraiMC;
import me.dreamvoid.miraimc.api.bot.MiraiGroup;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WhitelistManager {

    private static final Map<String, UUID> bindCodeMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> bindCodeTimeStampMap = new ConcurrentHashMap<>();
    private static final Map<UUID, String> reverseBindCodeMap = new ConcurrentHashMap<>();

    static {
        CrypticLib.platform().scheduler().runTaskTimer(Whitelist4QQ.getInstance(), () -> {
            long timeStamp = System.currentTimeMillis();
            long timeout = Whitelist4QQ.getInstance().getConfig().getLong("codeTimeoutSecond", 300L) * 1000;
            for (String key : bindCodeTimeStampMap.keySet()) {
                if (timeStamp - bindCodeTimeStampMap.get(key) >= timeout) {
                    removeBindCodeCache(key);
                }
            }
        }, 1, 1);
    }

    public static Map<String, UUID> getBindCodeMap() {
        return bindCodeMap;
    }

    public static Map<UUID, String> getReverseBindCodeMap() {
        return reverseBindCodeMap;
    }

    public static void addBindCodeCache(String code, UUID uuid) {
        bindCodeMap.put(code, uuid);
        reverseBindCodeMap.put(uuid, code);
        bindCodeTimeStampMap.put(code, System.currentTimeMillis());
    }

    public static void removeBindCodeCache(String code) {
        reverseBindCodeMap.remove(bindCodeMap.get(code));
        bindCodeMap.remove(code);
        bindCodeTimeStampMap.remove(code);
    }

    /**
     * 判断是否有白名单
     * @param uuid 判断的uuid
     * @return 1为拥有,0为拥有白名单但不在群内,-1为没有白名单
     */
    public static WhitelistState getWhitelistState(UUID uuid) {
        long bindQQ = MiraiMC.getBind(uuid);
        if (bindQQ == 0L)
            return WhitelistState.NO_WHITELIST;
        YamlConfiguration config = (YamlConfiguration) Whitelist4QQ.getInstance().getConfig();
        if (!config.getBoolean("bot.check-qq-in-group")) {
            return WhitelistState.HAS_WHITELIST;
        }
        for (long bot : config.getLongList("bot.used-bot-accounts")) {
            for (Long group : config.getLongList("bot.used-group-numbers")) {
                try {
                    MiraiBot miraiBot = MiraiBot.getBot(bot);
                    MiraiGroup group1 = miraiBot.getGroup(group);
                    if (group1.contains(bindQQ))
                        return WhitelistState.HAS_WHITELIST;
                } catch (NoSuchElementException ignored) {}
            }
        }
        return WhitelistState.NOT_IN_GROUP;
    }

    public enum WhitelistState {
        HAS_WHITELIST, NOT_IN_GROUP, NO_WHITELIST
    }

}
