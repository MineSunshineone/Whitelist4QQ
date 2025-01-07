package com.github.yufiriamazenta.whitelist4qq.listener;

import com.github.yufiriamazenta.whitelist4qq.WhitelistManager;
import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import crypticlib.chat.MsgSender;
import crypticlib.chat.TextProcessor;
import crypticlib.listener.BukkitListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Statistic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家相关事件监听
 */
@BukkitListener
public enum PlayerListener implements Listener {

    INSTANCE;
    private final Map<UUID, Long> visitorChatTimeMap = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerLoginOnWhitelistMode1(AsyncPlayerPreLoginEvent event) {
        if (Configs.whitelistMode.value() != 1) {
            return;
        }
        UUID uuid = event.getUniqueId();
        WhitelistManager.WhitelistState whitelistState = WhitelistManager.getWhitelistState(uuid);
        switch (whitelistState) {
            case HAS_WHITELIST:
                return;
            case NOT_IN_GROUP:
                String notInGroupMsg = Configs.messagesKickMessageNotInGroup.value();
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, TextProcessor.color(notInGroupMsg));
                break;
            case NO_WHITELIST:
                String code;
                if (WhitelistManager.getReverseBindCodeMap().containsKey(uuid)) {
                    code = WhitelistManager.getReverseBindCodeMap().get(uuid);
                } else {
                    code = UUID.randomUUID().toString();
                    code = code.substring(code.length() - 6);
                    WhitelistManager.addBindCodeCache(code, uuid, event.getName());
                }
                String bindHintMsg = TextProcessor.color(Configs.messagesKickMessageMode1.value().replace("%code%", code));
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, bindHintMsg);
                break;
            default:
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "Error");
        }
    }

    @EventHandler
    public void playerLoginOnWhitelistMode2(AsyncPlayerPreLoginEvent event) {
        if (Configs.whitelistMode.value() != 2) {
            return;
        }
        UUID uuid = event.getUniqueId();
        WhitelistManager.WhitelistState state = WhitelistManager.getWhitelistState(uuid);
        switch (state) {
            case NO_WHITELIST:
                long playedTime = Bukkit.getOfflinePlayer(uuid).getStatistic(Statistic.PLAY_ONE_MINUTE);
                long allowVisitTime = Configs.mode2AllowVisitSecond.value() * 20;
                if (playedTime >= allowVisitTime) {
                    String code;
                    if (WhitelistManager.getReverseBindCodeMap().containsKey(uuid)) {
                        code = WhitelistManager.getReverseBindCodeMap().get(uuid);
                    } else {
                        code = UUID.randomUUID().toString();
                        code = code.substring(code.length() - 6);
                        WhitelistManager.addBindCodeCache(code, uuid, event.getName());
                    }
                    String bindHintMsg = TextProcessor.color(Configs.messagesKickMessageMode2.value().replace("%code%", code));
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, TextProcessor.color(bindHintMsg));
                    return;
                }
                WhitelistManager.addToVisitors(uuid);
                break;
            case NOT_IN_GROUP:
                String notInGroupMsg = Configs.messagesKickMessageNotInGroup.value();
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, TextProcessor.color(notInGroupMsg));
                break;
            case HAS_WHITELIST:
                return;
            default:
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "Error");
        }
    }

    @EventHandler
    public void onVisitorGameModeChange(PlayerGameModeChangeEvent event) {
        if (Configs.whitelistMode.value() != 2) {
            return;
        }
        Player player = event.getPlayer();
        if (!WhitelistManager.isVisitor(player.getUniqueId())) {
            return;
        }
        if (!event.getNewGameMode().equals(GameMode.ADVENTURE)) {
            event.setCancelled(true);
            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    @EventHandler
    public void onVisitorInteract(PlayerInteractEvent event) {
        if (Configs.whitelistMode.value() != 2) {
            return;
        }
        Player player = event.getPlayer();
        if (!WhitelistManager.isVisitor(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onVisitorInteractEntity(PlayerInteractEntityEvent event) {
        if (Configs.whitelistMode.value() != 2) {
            return;
        }
        Player player = event.getPlayer();
        if (!WhitelistManager.isVisitor(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onVisitorPickUpItem(EntityPickupItemEvent event) {
        if (Configs.whitelistMode.value() != 2) {
            return;
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!WhitelistManager.isVisitor(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onVisitorDamageEntity(EntityDamageByEntityEvent event) {
        if (Configs.whitelistMode.value() != 2) {
            return;
        }
        if (!(event.getDamager() instanceof Player) && !(event.getEntity() instanceof Player)) {
            return;
        }
        Entity damager = event.getDamager();
        Entity player = event.getEntity();
        if (!WhitelistManager.isVisitor(damager.getUniqueId()) && !WhitelistManager.isVisitor(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onVisitorChat(AsyncPlayerChatEvent event) {
        if (Configs.whitelistMode.value() != 2) {
            return;
        }
        Player player = event.getPlayer();
        if (!WhitelistManager.isVisitor(player.getUniqueId())) {
            return;
        }
        if (visitorChatTimeMap.containsKey(player.getUniqueId())) {
            long time = System.currentTimeMillis();
            long lastChatTime = visitorChatTimeMap.get(player.getUniqueId());
            int chatCd = Configs.mode2VisitorChatCd.value() * 1000;
            if (time - lastChatTime < chatCd) {
                MsgSender.sendMsg(player, Configs.messagesVisitorChatInCd.value());
                event.setCancelled(true);
                return;
            }
            visitorChatTimeMap.put(player.getUniqueId(), time);
        } else {
            visitorChatTimeMap.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onVisitorQuit(PlayerQuitEvent event) {
        if (Configs.whitelistMode.value() != 2) {
            return;
        }
        Player player = event.getPlayer();
        if (!WhitelistManager.isVisitor(player.getUniqueId())) {
            return;
        }
        WhitelistManager.removeFromVisitors(player.getUniqueId());
        visitorChatTimeMap.remove(player.getUniqueId());
    }

    public Map<UUID, Long> getVisitorChatTimestampMap() {
        return visitorChatTimeMap;
    }

}
