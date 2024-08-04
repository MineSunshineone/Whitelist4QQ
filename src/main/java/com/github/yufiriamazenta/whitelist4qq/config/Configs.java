package com.github.yufiriamazenta.whitelist4qq.config;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.BooleanConfig;
import crypticlib.config.node.impl.bukkit.IntConfig;
import crypticlib.config.node.impl.bukkit.LongListConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;

import java.util.ArrayList;
import java.util.Arrays;

@ConfigHandler(path = "config.yml")
public class Configs {

    public static final BooleanConfig preventQQRebind = new BooleanConfig("prevent_qq_rebind", true);
    public static final LongListConfig usedBotAccounts = new LongListConfig("used_bot_accounts", new ArrayList<>(Arrays.asList(123456789L, 23456789L)));
    public static final LongListConfig usedGroups = new LongListConfig("used_groups", new ArrayList<>(Arrays.asList(123456789L, 23456789L)));
    public static final IntConfig whitelistMode = new IntConfig("whitelist_mode", 1);
    public static final IntConfig mode2AllowVisitSecond = new IntConfig("mode_2_allow_visit_second", 600);
    public static final BooleanConfig checkQQInGroup = new BooleanConfig("check_qq_in_group", true);
    public static final BooleanConfig remove_bind_when_qq_quit = new BooleanConfig("remove_bind_when_qq_quit", false);
    public static final StringConfig bindCommandPrefix = new StringConfig("bind_command_prefix", "申请白名单");
    public static final StringConfig selectPlayerCommandPrefix = new StringConfig("select_player_command_prefix", "查询玩家绑定");
    public static final StringConfig selectQQCommandPrefix = new StringConfig("select_qq_command_prefix", "查询QQ绑定");
    public static final IntConfig codeTimeoutSecond = new IntConfig("code_timeout_second", 300);
    public static final IntConfig mode2HintCd = new IntConfig("mode_2_hint_cd", 30);
    public static final StringConfig messagesMode2BindHintMessage = new StringConfig("messages.mode_2_bind_hint_message", "&a如果您觉得我们服务器不错,可以使用命令/bind申请白名单噢");
    public static final IntConfig mode2VisitorChatCd = new IntConfig("mode_2_visitor_chat_cd", 10);

    public static final StringConfig dataType = new StringConfig("data.type", "mysql");
    public static final StringConfig mysqlDriver = new StringConfig("data.mysql.driver", "com.mysql.cj.jdbc.Driver");
    public static final StringConfig mysqlUrl = new StringConfig("data.mysql.url", "jdbc:mysql://localhost:3306/guild");
    public static final StringConfig mysqlUsername = new StringConfig("data.mysql.username", "root");
    public static final StringConfig mysqlTable = new StringConfig("data.mysql.table", "whitelist4qq");
    public static final StringConfig mysqlPassword = new StringConfig("data.mysql.password", "");
    public static final IntConfig mysqlHikariConnTimeout = new IntConfig("data.mysql.hikari.connection-timeout", 3000);
    public static final IntConfig mysqlHikariMinIdle = new IntConfig("data.mysql.hikari.min-idle", 10);
    public static final IntConfig mysqlHikariMaxLifetime = new IntConfig("data.mysql.hikari.max-life-time", 3000);
    public static final IntConfig mysqlHikariMaxPoolSize = new IntConfig("data.mysql.hikari.max-pool-size", 10);
    public static final BooleanConfig mysqlHikariAutoCommit = new BooleanConfig("data.mysql.hikari.auto-commit", true);

}
