package com.github.yufiriamazenta.whitelist4qq;

import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import com.github.yufiriamazenta.whitelist4qq.listener.PlayerListener;
import crypticlib.CrypticLib;
import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.api.MiraiMC;
import me.dreamvoid.miraimc.api.bot.MiraiGroup;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class WhitelistManager {

    private static final Map<String, UUID> bindCodeMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> bindCodeTimeStampMap = new ConcurrentHashMap<>();
    private static final Map<UUID, String> bindPlayerNameCache = new ConcurrentHashMap<>();
    private static final Map<UUID, String> reverseBindCodeMap = new ConcurrentHashMap<>();
    private static final List<UUID> visitors = new CopyOnWriteArrayList<>();

    static {
        CrypticLib.platform().scheduler().runTaskTimer(Whitelist4QQ.instance(), () -> {
            long timeStamp = System.currentTimeMillis();
            long timeout = Configs.codeTimeoutSecond.value() * 1000;
            for (String key : bindCodeTimeStampMap.keySet()) {
                if (timeStamp - bindCodeTimeStampMap.get(key) >= timeout) {
                    removeBindCodeCache(key);
                }
            }
        }, 1, 1);
    }

    public static String getBindPlayerName(UUID uuid) {
        return bindPlayerNameCache.get(uuid);
    }

    public static Map<String, UUID> getBindCodeMap() {
        return bindCodeMap;
    }

    public static Map<UUID, String> getReverseBindCodeMap() {
        return reverseBindCodeMap;
    }

    public static void addBindCodeCache(String code, UUID uuid, String name) {
        bindCodeMap.put(code, uuid);
        bindPlayerNameCache.put(uuid, name);
        reverseBindCodeMap.put(uuid, code);
        bindCodeTimeStampMap.put(code, System.currentTimeMillis());
    }

    public static void removeBindCodeCache(String code) {
        reverseBindCodeMap.remove(bindCodeMap.get(code));
        bindPlayerNameCache.remove(bindCodeMap.get(code));
        bindCodeMap.remove(code);
        bindCodeTimeStampMap.remove(code);
    }

    public static void addBind(long bindQQ, String bindCode) {
        UUID bindUuid = bindCodeMap.get(bindCode);
        MiraiMC.Bind.addBind(bindUuid, bindQQ);
        removeBindCodeCache(bindCode);
        PlayerListener.INSTANCE.getVisitorChatTimestampMap().remove(bindUuid);
        visitors.remove(bindUuid);
    }

    public static boolean isVisitor(UUID uuid) {
        return visitors.contains(uuid);
    }

    public static void addToVisitors(UUID uuid) {
        visitors.add(uuid);
    }

    public static void removeFromVisitors(UUID uuid) {
        visitors.remove(uuid);
    }

    /**
     * 判断是否有白名单
     *
     * @param uuid 判断的uuid
     * @return 1为拥有,0为拥有白名单但不在群内,-1为没有白名单
     */
    public static WhitelistState getWhitelistState(UUID uuid) {
        long bindQQ = MiraiMC.Bind.getBind(uuid);
        if (bindQQ == 0L) {
            return WhitelistState.NO_WHITELIST;
        }

        if (!Configs.checkQQInGroup.value()) {
            return WhitelistState.HAS_WHITELIST;
        }
        for (long bot : Configs.usedBotAccounts.value()) {
            for (Long group : Configs.usedGroups.value()) {
                try {
                    MiraiBot miraiBot = MiraiBot.getBot(bot);
                    MiraiGroup group1 = miraiBot.getGroup(group);
                    if (group1.contains(bindQQ)) {
                        return WhitelistState.HAS_WHITELIST;
                    }
                } catch (NoSuchElementException ignored) {
                }
            }
        }
        return WhitelistState.NOT_IN_GROUP;
    }

    public static WhitelistState getWhitelistState(long qq) {
        UUID bindPlayer = MiraiMC.Bind.getBind(qq);
        if (bindPlayer == null) {
            return WhitelistState.NO_WHITELIST;
        }

        if (!Configs.checkQQInGroup.value()) {
            return WhitelistState.HAS_WHITELIST;
        }
        for (long bot : Configs.usedBotAccounts.value()) {
            for (Long group : Configs.usedGroups.value()) {
                try {
                    MiraiBot miraiBot = MiraiBot.getBot(bot);
                    MiraiGroup group1 = miraiBot.getGroup(group);
                    if (group1.contains(qq)) {
                        return WhitelistState.HAS_WHITELIST;
                    }
                } catch (NoSuchElementException ignored) {
                }
            }
        }
        return WhitelistState.NOT_IN_GROUP;
    }

    public static List<UUID> visitors() {
        return visitors;
    }

    public enum WhitelistState {
        HAS_WHITELIST, NOT_IN_GROUP, NO_WHITELIST
    }

}
