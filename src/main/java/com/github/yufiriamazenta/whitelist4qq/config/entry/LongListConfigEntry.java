package com.github.yufiriamazenta.whitelist4qq.config.entry;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class LongListConfigEntry extends ConfigEntry<List<Long>> {

    public LongListConfigEntry(String key, List<Long> def) {
        super(key, def);
    }

    @Override
    public void load(ConfigurationSection config) {
        if (!config.contains(key())) {
            config.set(key(), def());
            setValue(def());
            return;
        }
        setValue(config.getLongList(key()));
    }
}
