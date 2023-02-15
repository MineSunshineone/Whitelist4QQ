package me.dreamvoid.whitelist4qq.bukkit.listener;

import me.dreamvoid.whitelist4qq.bukkit.BukkitPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import static me.dreamvoid.whitelist4qq.bukkit.Config.GEN_CheckRange_SPEC;

/**
 * 玩家动作事件
 */
public class PlayerActions implements Listener {
    private final BukkitPlugin bukkitPlugin;

    public PlayerActions(BukkitPlugin bukkitPlugin) {
        this.bukkitPlugin = bukkitPlugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (bukkitPlugin.getCache().contains(e.getPlayer()) && !GEN_CheckRange_SPEC)
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (bukkitPlugin.getCache().contains(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
        if (bukkitPlugin.getCache().contains(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent e) {
        if (bukkitPlugin.getCache().contains(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if (bukkitPlugin.getCache().contains(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent e) {
        if (bukkitPlugin.getCache().contains(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (bukkitPlugin.getCache().contains(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (bukkitPlugin.getCache().contains(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        if (bukkitPlugin.getCache().contains(e.getPlayer()))
            e.setCancelled(true);
    }
}
