package com.github.yufiriamazenta.whitelist4qq.config.entry;

import org.bukkit.configuration.ConfigurationSection;

public class IntConfigEntry extends ConfigEntry<Integer> {

    public IntConfigEntry(String key, Integer def) {
        super(key, def);
    }

    @Override
    public void load(ConfigurationSection config) {
        if (!config.contains(key())) {
            config.set(key(), def());
            setValue(def());
            return;
        }
        setValue(config.getInt(key()));
    }
}
