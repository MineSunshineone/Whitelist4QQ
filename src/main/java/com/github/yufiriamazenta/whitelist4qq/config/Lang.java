package com.github.yufiriamazenta.whitelist4qq.config;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.StringConfig;

@ConfigHandler(path = "lang.yml")
public class Lang {

    public static final StringConfig messagesVisitorChatInCd = new StringConfig("messages.visitor_chat_in_cd", "&c您当前只能每10秒发送一条消息");
    public static final StringConfig messagesCommandBindBind = new StringConfig("messages.command.bind.bind", "&a您已绑定%qq%，现在你可以游玩本服务器了！");
    public static final StringConfig messagesPlaceholderVisitorTag = new StringConfig("messages.placeholder.visitor_tag", "&a[参观玩家]");
    public static final StringConfig messagesCommandGetBindInvalidQQ = new StringConfig("messages.command.get_bind.invalid_qq", "&c请输入有效的qq号");
    public static final StringConfig messagesCommandGetBindSelecting = new StringConfig("messages.command.get_bind.selecting", "&a&l正在查找绑定中");
    public static final StringConfig messagesCommandGetBindNotExist = new StringConfig("messages.command.get_bind.not_exist", "&a查询完成，此QQ没有绑定玩家");
    public static final StringConfig messagesCommandGetBindSuccess = new StringConfig("messages.command.get_bind.success", "&a查询完成，%qq%绑定的玩家为%player%");
    public static final StringConfig messagesCommandGetBindHover = new StringConfig("messages.command.get_bind.hover", "&a点击复制名字");
    public static final StringConfig messagesCommandGetQQInvalidPlayer = new StringConfig("messages.command.get_qq.invalid_player", "&c此玩家未在服务器注册过");
    public static final StringConfig messagesCommandGetQQSelecting = new StringConfig("messages.command.get_qq.selecting", "&a&l正在查找绑定中");
    public static final StringConfig messagesCommandGetQQNotExist = new StringConfig("messages.command.get_qq.not_exist", "&a查询完成，此玩家没有绑定QQ");
    public static final StringConfig messagesCommandGetQQSuccess = new StringConfig("messages.command.get_qq.success", "&a查询完成，%player%绑定的QQ为%qq%");
    public static final StringConfig messagesCommandGetQQHover = new StringConfig("messages.command.get_qq.hover", "&a点击复制QQ");
    public static final StringConfig messagesKickMessageMode1 = new StringConfig("messages.kick_message.mode_1",
        """
            &c你不在本服的白名单中，请加入我们的QQ群123456进行申请
            &c申请方式：在群内发送“申请白名单%code%”
            &c有效时长5分钟""");
    public static final StringConfig messagesKickMessageMode2 = new StringConfig("messages.kick_message.mode_2",
        """
            &c你不在本服的白名单中，请加入我们的QQ群123456进行申请
            &c申请方式：在群内发送“申请白名单%code%”
            &c有效时长5分钟""");
    public static final StringConfig messagesKickMessageNotInGroup = new StringConfig("messages.kick_message.not_in_group", "&c你不在本服的群中，无法加入服务器");
    public static final StringConfig messagesBotMessageBindSuccess = new StringConfig("messages.bot_message.bind_success", "您已成功绑定玩家%player%，现在你可以游玩本服务器了！");
    public static final StringConfig messagesBotMessageBindFailedNotExistCode = new StringConfig("messages.bot_message.bind_failed.not_exist_code", "不存在此绑定码");
    public static final StringConfig messagesBotMessageBindFailedBound = new StringConfig("messages.bot_message.bind_failed.bound", "你已经绑定了一个游戏账号%player%，无法重复绑定");
    public static final StringConfig messagesBotMessageSelectQQFailedNumberFormat = new StringConfig("messages.bot_message.select_qq_failed.number_format", "查询错误，你查询的内容并不是一个QQ号");
    public static final StringConfig messagesBotMessageSelectQQFailedNotExist = new StringConfig("messages.bot_message.select_qq_failed.not_exist", "查询完成，不存在此QQ绑定的玩家");
    public static final StringConfig messagesBotMessageSelectQQSuccess = new StringConfig("messages.bot_message.select_qq_success", "查询完成，此QQ绑定的玩家为%player%");
    public static final StringConfig messagesBotMessageSelectPlayerFailedNotExist = new StringConfig("messages.bot_message.select_player_failed_not_exist", "查询完成，此玩家没有绑定QQ号");
    public static final StringConfig messagesBotMessageSelectPlayerSuccess = new StringConfig("messages.bot_message.select_player_success", "查询完成，该玩家绑定的QQ为%qq%");
    public static final StringConfig messagesCommandPlayerOnly = new StringConfig("messages.command.player_only", "&a此命令只允许玩家使用");
    public static final StringConfig messagesCommandReload = new StringConfig("messages.command.reload", "&a已重载Whitelist4QQ");
    public static final StringConfig messagesCommandRemoveUsage = new StringConfig("messages.command.remove.usage", "&c用法：/whitelist4qq remove <玩家名>");
    public static final StringConfig messagesCommandRemoveSuccess = new StringConfig("messages.command.remove.success", "&a已移除指定玩家的绑定！");
    public static final StringConfig messagesCommandBindBound = new StringConfig("messages.command.bind.bound", "&a您已经绑定过，无需再次绑定");
    public static final StringConfig messagesCommandBindWait = new StringConfig("messages.command.bind.wait", "&a您的绑定码为%code%，请在群内使用“申请白名单%code%”进行申请");
}
