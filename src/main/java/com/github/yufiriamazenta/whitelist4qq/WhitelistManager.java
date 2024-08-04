package com.github.yufiriamazenta.whitelist4qq;

import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import com.github.yufiriamazenta.whitelist4qq.data.DataAccessor;
import com.github.yufiriamazenta.whitelist4qq.data.DataType;
import com.github.yufiriamazenta.whitelist4qq.data.impl.MysqlDataAccessor;
import com.github.yufiriamazenta.whitelist4qq.listener.PlayerListener;
import crypticlib.CrypticLibBukkit;
import crypticlib.lifecycle.BukkitEnabler;
import crypticlib.lifecycle.BukkitReloader;
import crypticlib.lifecycle.annotation.OnEnable;
import crypticlib.lifecycle.annotation.OnReload;
import org.bukkit.plugin.Plugin;

import java.util.*;

@OnEnable
@OnReload
public enum WhitelistManager implements BukkitReloader, BukkitEnabler {

    INSTANCE;
    
    private DataAccessor dataAccessor;

    WhitelistManager() {

    }

    public void addBind(long bindQQ, String bindCode) {
        UUID bindUuid = bindCodeMap.get(bindCode);
        MiraiMC.addBind(bindUuid, bindQQ);
        removeBindCodeCache(bindCode);
        PlayerListener.INSTANCE.getVisitorChatTimestampMap().remove(bindUuid);
        visitors.remove(bindUuid);
    }

    public void removeBind(long bindQQ) {
        //TODO
    }

    public void removeBind(UUID uuid) {

    }

    public long getBind(UUID uuid) {

    }

    public UUID getBind(long qq) {

    }

    @Override
    public void enable(Plugin plugin) {
        reload(plugin);
    }

    @Override
    public void reload(Plugin plugin) {
        DataType dataType = DataType.valueOf(Configs.dataType.value().toUpperCase());
        switch (dataType) {
            case MYSQL -> {
                dataAccessor = MysqlDataAccessor.INSTANCE;
            }
            case SQLITE -> {
                //TODO
            }
        }
        dataAccessor.reload(plugin);
    }

    public enum WhitelistState {
        HAS_WHITELIST, NOT_IN_GROUP, NO_WHITELIST
    }

}
