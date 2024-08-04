package com.github.yufiriamazenta.whitelist4qq.util;

import com.github.yufiriamazenta.whitelist4qq.Whitelist4QQ;
import crypticlib.CrypticLibBukkit;

public class SchedulerUtil {

    public static void async(Runnable runnable) {
        CrypticLibBukkit.scheduler().runTaskAsync(Whitelist4QQ.instance(), runnable);
    }

    public static void sync(Runnable runnable) {
        CrypticLibBukkit.scheduler().runTask(Whitelist4QQ.instance(), runnable);
    }

}
