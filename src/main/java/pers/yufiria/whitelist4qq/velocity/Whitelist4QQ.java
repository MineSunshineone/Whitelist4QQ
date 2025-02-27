package pers.yufiria.whitelist4qq.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.Scheduler;
import crypticlib.VelocityPlugin;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
    id = "{{id}}",
    name = "{{name}}",
    version = "{{version}}",
    dependencies = {@Dependency(id = "miraimc")},
    authors = {"YufiriaMazenta"}
)
public class Whitelist4QQ extends VelocityPlugin {

    private static Whitelist4QQ INSTANCE;

    public @Inject Whitelist4QQ(Logger logger, ProxyServer proxyServer, PluginContainer pluginContainer, @DataDirectory Path dataDirectory) {
        super(logger, proxyServer, pluginContainer, dataDirectory);
    }

    public static Whitelist4QQ instance() {
        return INSTANCE;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
    }

    public Scheduler.TaskBuilder buildTask(Runnable runnable) {
        return this.getScheduler().buildTask(this, runnable);
    }

}
