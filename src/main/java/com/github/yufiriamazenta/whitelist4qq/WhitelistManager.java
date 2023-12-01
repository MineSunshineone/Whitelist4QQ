package com.github.yufiriamazenta.whitelist4qq;

import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import com.github.yufiriamazenta.whitelist4qq.listener.PlayerListener;
import crypticlib.CrypticLib;
import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.api.MiraiMC;
import me.dreamvoid.miraimc.api.bot.MiraiGroup;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WhitelistManager {

    private static final Map<String, UUID> bindCodeMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> bindCodeTimeStampMap = new ConcurrentHashMap<>();
    private static final Map<UUID, String> reverseBindCodeMap = new ConcurrentHashMap<>();
    public static final NamespacedKey PLAYER_VISIT_TAG_KEY = new NamespacedKey(Whitelist4QQ.instance(), "visit");

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

    public static void addBind(long bindQQ, String bindCode) {
        UUID bindUuid = bindCodeMap.get(bindCode);
        MiraiMC.addBind(bindUuid, bindQQ);
        removeBindCodeCache(bindCode);
        PlayerListener.INSTANCE.getVisitorChatTimeMap().remove(bindUuid);
        if (Whitelist4QQ.instance().whitelistMode() == 2) {
            CrypticLib.platform().scheduler().runTask(Whitelist4QQ.instance(), () -> {
                Player player = Bukkit.getPlayer(bindUuid);
                if (player != null)
                    removeVisitTag4Player(player);
            });
        }
    }

    public static boolean hasVisitTag(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        return dataContainer.has(WhitelistManager.PLAYER_VISIT_TAG_KEY, PersistentDataType.BYTE);
    }

    public static void addVisitTag2Player(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        if (hasVisitTag(player)) {
            return;
        }
        dataContainer.set(WhitelistManager.PLAYER_VISIT_TAG_KEY, PersistentDataType.BYTE, (byte) 1);
    }

    public static void removeVisitTag4Player(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        if (!hasVisitTag(player)) {
            return;
        }
        dataContainer.remove(WhitelistManager.PLAYER_VISIT_TAG_KEY);
        player.setGameMode(GameMode.SURVIVAL);
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

        if (!Configs.checkQQInGroup.value()) {
            return WhitelistState.HAS_WHITELIST;
        }
        for (long bot : Configs.usedBotAccounts.value()) {
            for (Long group : Configs.usedGroups.value()) {
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

    public static WhitelistState getWhitelistState(long qq) {
        UUID bindPlayer = MiraiMC.getBind(qq);
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
                    if (group1.contains(qq))
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
