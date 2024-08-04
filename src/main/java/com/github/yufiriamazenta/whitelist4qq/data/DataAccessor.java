package com.github.yufiriamazenta.whitelist4qq.data;

import org.bukkit.plugin.Plugin;

import java.util.UUID;

public interface DataAccessor {

    void reload(Plugin plugin);

    boolean addBind(UUID uuid, long qq);

    boolean removeBind(UUID uuid);

    boolean removeBind(long qq);

    UUID getBind(long qq);

    long getBind(UUID uuid);

}
