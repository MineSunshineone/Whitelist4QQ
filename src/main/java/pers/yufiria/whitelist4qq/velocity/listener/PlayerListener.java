package pers.yufiria.whitelist4qq.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import crypticlib.chat.VelocityTextProcessor;
import crypticlib.listener.EventListener;
import pers.yufiria.whitelist4qq.velocity.WhitelistManager;
import pers.yufiria.whitelist4qq.velocity.config.Configs;

import java.util.UUID;

/**
 * 玩家相关事件监听
 */
@EventListener
public enum PlayerListener {

    INSTANCE;

    @Subscribe(priority = -1)
    public void playerLoginOnWhitelist(PreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        WhitelistManager.WhitelistState whitelistState = WhitelistManager.getWhitelistState(uuid);
        switch (whitelistState) {
            case HAS_WHITELIST:
                return;
            case NOT_IN_GROUP:
                String notInGroupMsg = Configs.messagesKickMessageNotInGroup.value();
                event.setResult(PreLoginEvent.PreLoginComponentResult.denied(
                    VelocityTextProcessor.toComponent(notInGroupMsg)
                ));
                break;
            case NO_WHITELIST:
                String code;
                if (WhitelistManager.getReverseBindCodeMap().containsKey(uuid)) {
                    code = WhitelistManager.getReverseBindCodeMap().get(uuid);
                } else {
                    code = UUID.randomUUID().toString();
                    code = code.substring(code.length() - 6);
                    WhitelistManager.addBindCodeCache(code, uuid, event.getUsername());
                }
                event.setResult(
                    PreLoginEvent.PreLoginComponentResult.denied(
                        VelocityTextProcessor.toComponent(Configs.messagesKickMessage.value().replace("%code%", code))
                    )
                );
                break;
            default:
                event.setResult(PreLoginEvent.PreLoginComponentResult.denied(VelocityTextProcessor.toComponent("Error")));
        }
    }

}
