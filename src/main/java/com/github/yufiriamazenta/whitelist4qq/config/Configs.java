package com.github.yufiriamazenta.whitelist4qq.config;

import crypticlib.config.yaml.YamlConfigHandler;
import crypticlib.config.yaml.entry.BooleanConfigEntry;
import crypticlib.config.yaml.entry.IntConfigEntry;
import crypticlib.config.yaml.entry.LongListConfigEntry;
import crypticlib.config.yaml.entry.StringConfigEntry;

import java.util.ArrayList;
import java.util.Arrays;

@YamlConfigHandler(path = "config.yml")
public class Configs {

    public static final StringConfigEntry messagesKickMessageMode1 = new StringConfigEntry("messages.kick_message.mode_1",
        "&c你不在本服的白名单中，请加入我们的QQ群123456进行申请\n" +
            "&c申请方式：在群内发送“申请白名单%code%”\n" +
            "&c有效时长5分钟");
    public static final StringConfigEntry messagesKickMessageMode2 = new StringConfigEntry("messages.kick_message.mode_2",
        "&c你不在本服的白名单中，请加入我们的QQ群123456进行申请\n" +
            "&c申请方式：在群内发送“申请白名单%code%”\n" +
            "&c有效时长5分钟");
    public static final StringConfigEntry messagesKickMessageNotInGroup = new StringConfigEntry("messages.kick_message.not_in_group", "&c你不在本服的群中，无法加入服务器");
    public static final StringConfigEntry messagesBotMessageBindSuccess = new StringConfigEntry("messages.bot_message.bind_success", "您已成功绑定玩家%player%，现在你可以游玩本服务器了！");
    public static final StringConfigEntry messagesBotMessageBindFailedNotExistCode = new StringConfigEntry("messages.bot_message.bind_failed.not_exist_code", "不存在此绑定码");
    public static final StringConfigEntry messagesBotMessageBindFailedBound = new StringConfigEntry("messages.bot_message.bind_failed.bound", "你已经绑定了一个游戏账号%player%，无法重复绑定");
    public static final StringConfigEntry messagesBotMessageSelectQQFailedNumberFormat = new StringConfigEntry("messages.bot_message.select_qq_failed.number_format", "查询错误，你查询的内容并不是一个QQ号");
    public static final StringConfigEntry messagesBotMessageSelectQQFailedNotExist = new StringConfigEntry("messages.bot_message.select_qq_failed.not_exist", "查询完成，不存在此QQ绑定的玩家");
    public static final StringConfigEntry messagesBotMessageSelectQQSuccess = new StringConfigEntry("messages.bot_message.select_qq_success", "查询完成，此QQ绑定的玩家为%player%");
    public static final StringConfigEntry messagesBotMessageSelectPlayerFailedNotExist = new StringConfigEntry("messages.bot_message.select_player_failed_not_exist", "查询完成，此玩家没有绑定QQ号");
    public static final StringConfigEntry messagesBotMessageSelectPlayerSuccess = new StringConfigEntry("messages.bot_message.select_player_success", "查询完成，该玩家绑定的QQ为%qq%");
    public static final StringConfigEntry messagesCommandPlayerOnly = new StringConfigEntry("messages.command.player_only", "&a此命令只允许玩家使用");
    public static final StringConfigEntry messagesCommandReload = new StringConfigEntry("messages.command.reload", "&a已重载Whitelist4QQ");
    public static final StringConfigEntry messagesCommandRemoveUsage = new StringConfigEntry("messages.command.remove.usage", "&c用法：/whitelist4qq remove <玩家名>");
    public static final StringConfigEntry messagesCommandRemoveSuccess = new StringConfigEntry("messages.command.remove.success", "&a已移除指定玩家的绑定！");
    public static final StringConfigEntry messagesCommandBindBound = new StringConfigEntry("messages.command.bind.bound", "&a您已经绑定过，无需再次绑定");
    public static final StringConfigEntry messagesCommandBindWait = new StringConfigEntry("messages.command.bind.wait", "&a您的绑定码为%code%，请在群内使用“申请白名单%code%”进行申请");
    public static final BooleanConfigEntry preventQQRebind = new BooleanConfigEntry("prevent_qq_rebind", true);
    public static final LongListConfigEntry usedBotAccounts = new LongListConfigEntry("used_bot_accounts", new ArrayList<>(Arrays.asList(123456789L, 23456789L)));
    public static final LongListConfigEntry usedGroups = new LongListConfigEntry("used_groups", new ArrayList<>(Arrays.asList(123456789L, 23456789L)));
    public static final IntConfigEntry whitelistMode = new IntConfigEntry("whitelist_mode", 1);
    public static final IntConfigEntry mode2AllowVisitSecond = new IntConfigEntry("mode_2_allow_visit_second", 600);
    public static final BooleanConfigEntry checkQQInGroup = new BooleanConfigEntry("check_qq_in_group", true);
    public static final BooleanConfigEntry remove_bind_when_qq_quit = new BooleanConfigEntry("remove_bind_when_qq_quit", false);
    public static final StringConfigEntry bindCommandPrefix = new StringConfigEntry("bind_command_prefix", "申请白名单");
    public static final StringConfigEntry selectPlayerCommandPrefix = new StringConfigEntry("select_player_command_prefix", "查询玩家绑定");
    public static final StringConfigEntry selectQQCommandPrefix = new StringConfigEntry("select_qq_command_prefix", "查询QQ绑定");
    public static final IntConfigEntry codeTimeoutSecond = new IntConfigEntry("code_timeout_second", 300);
    public static final IntConfigEntry mode2HintCd = new IntConfigEntry("mode_2_hint_cd", 30);
    public static final StringConfigEntry messagesMode2BindHintMessage = new StringConfigEntry("messages.mode_2_bind_hint_message", "&a如果您觉得我们服务器不错,可以使用命令/bind申请白名单噢");
    public static final IntConfigEntry mode2VisitorChatCd = new IntConfigEntry("mode_2_visitor_chat_cd", 10);
    public static final StringConfigEntry messagesVisitorChatInCd = new StringConfigEntry("messages.visitor_chat_in_cd", "&c您当前只能每10秒发送一条消息");

}
