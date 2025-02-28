package pers.yufiria.whitelist4qq.velocity;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import crypticlib.util.IOHelper;
import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.api.MiraiMC;
import me.dreamvoid.miraimc.api.bot.MiraiGroup;
import pers.yufiria.whitelist4qq.velocity.config.Configs;

public class WhitelistManager {

    private static final Cache<String, UUID> bindCodes = CacheBuilder.newBuilder().expireAfterWrite(Configs.codeTimeoutSecond.value(), TimeUnit.SECONDS).build();
    private static final Map<UUID, String> bindPlayerNameCache = new ConcurrentHashMap<>();
    private static final Cache<UUID, String> reverseBindCodeCache = CacheBuilder.newBuilder().expireAfterWrite(Configs.codeTimeoutSecond.value(), TimeUnit.SECONDS).build();
    private static final List<UUID> visitors = new CopyOnWriteArrayList<>();

    public static String getBindPlayerName(UUID uuid) {
        return bindPlayerNameCache.get(uuid);
    }

    public static Map<String, UUID> getBindCodes() {
        return bindCodes.asMap();
    }

    public static Map<UUID, String> getReverseBindCodes() {
        return reverseBindCodeCache.asMap();
    }

    public static void addBindCodeCache(String code, UUID uuid, String name) {
        bindCodes.put(code, uuid);
        bindPlayerNameCache.put(uuid, name);
        reverseBindCodeCache.put(uuid, code);
    }

    public static void removeBindCodeCache(String code) {
        UUID bindPlayerId = bindCodes.getIfPresent(code);
        if (bindPlayerId != null) {
            bindCodes.invalidate(code);
            reverseBindCodeCache.invalidate(bindPlayerId);
            bindPlayerNameCache.remove(bindPlayerId);
        }
    }

    public static void addBind(long bindQQ, String bindCode) {
        UUID bindUuid = bindCodes.getIfPresent(bindCode);
        if (bindUuid == null) {
            IOHelper.info("&eBind code " + bindCode + " do not belongs to a player!");
            return;
        }
        MiraiMC.Bind.addBind(bindUuid, bindQQ);
        removeBindCodeCache(bindCode);
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
            for (Number group : Configs.usedGroups.value()) {
                try {
                    MiraiBot miraiBot = MiraiBot.getBot(bot);
                    MiraiGroup group1 = miraiBot.getGroup(group.longValue());
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
            for (Number group : Configs.usedGroups.value()) {
                try {
                    MiraiBot miraiBot = MiraiBot.getBot(bot);
                    MiraiGroup group1 = miraiBot.getGroup(group.longValue());
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
