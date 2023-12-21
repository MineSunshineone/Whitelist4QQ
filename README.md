# Whitelist4QQ
在QQ群内自助申请Minecraft白名单！

## 介绍

#### 什么是 Whitelist4QQ？
Whitelist4QQ 是一个基于 [MiraiMC](https://github.com/DreamVoid/MiraiMC) 的 Bukkit 插件，能够让玩家在 QQ 群内自助申请 Minecraft 白名单。支持Folia端

本项目基于DreamVoid编写的初始版本，由YufiriaMazenta重写

## 开始使用

* 下载插件，并将插件文件放入 plugins 文件夹；
* 下载 [MiraiMC](https://github.com/DreamVoid/MiraiMC) 插件（如果尚未下载），并将插件文件放入 plugins 文件夹；
* 启动服务端（如果尚未启动）或使用诸如 PlugMan 的插件加载插件；
* 使用指令“**/mirai login <账号> <密码>**”登录你的机器人账号；
* 调整插件的配置文件；
* 输入/whitelist4qq reload重载配置
* 享受插件吧！

## 命令&权限

```
/whitelist4qq | /qwl | /qwhitelist
插件的主命令
权限：whitelist4qq.command

/whitelist4qq reload
重载插件配置
权限：whitelist4qq.command.reload

/whitelist4qq remove <player_name>
删除玩家的白名单
权限：whitelist4qq.command.remove

/whitelist4qq getbind <qq>
获取某QQ绑定的玩家
权限：whitelist4qq.command.getbind

/whitelist4qq getqq <player_name>
获取某玩家绑定的QQ
权限：whitelist4qq.command.getqq

/bind
模式2下，参观玩家主动发起绑定的命令，使用此命令可生成绑定码进行绑定
权限：无
```

## 群内指令

### 申请白名单

用于在群内申请白名单，后接玩家的绑定码，可在config.yml中修改`bind_command_prefix`配置项来修改命令文本

示例：`申请白名单 abcdef`

### 查询玩家绑定

用于在群内查询玩家绑定的QQ，可在config.yml中修改`select_player_command_prefix`配置项来修改命令文本

示例：`查询玩家绑定 abc`

### 查询QQ绑定

用于在群内查询QQ绑定的玩家，可在config.yml中修改`select_qq_command_prefix`配置项来修改命令文本

示例：`查询QQ绑定 123456`

## 绑定流程

### 模式1

玩家尝试进入服务器，不符合条件（无白名单、不在群内等）的玩家将获取一个六位的绑定码，在群内发送`<申请白名单命令> <绑定码>`即可完成绑定

例如“申请白名单 abcdef”

### 模式2

此模式下，将区分玩家为普通玩家和参观玩家。

没有白名单的玩家进入服务器将可以参观设定好的时间。在参观时间内，参观玩家会固定为冒险模式，无法与方块和实体交互，无法捡起物品，且每10秒才能发送一条消息。

参观玩家可以在参观时间内，主动使用命令/bind发起白名单申请，此时将会获得一个绑定码，在群内发送`<申请白名单命令> <绑定码>`即可完成绑定”

参观时间结束后，参观玩家将会被强制踢出游戏，再次尝试进入游戏时与模式1流程相同

### 注意事项

绑定码的默认有效时长为5分钟，可以通过修改config.yml中的`code_timeout_second`来修改有效时长，单位为秒