/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.proxy;

import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import io.github.singlerr.semaphore.instances.CallConnectionHandlerAccess;
import io.github.singlerr.semaphore.instances.DatabaseAccess;
import io.github.singlerr.semaphore.interactors.access.call.CallConnectionHandler;
import io.github.singlerr.semaphore.policy.callhandler.CallConnectionHandlerAdapter;
import io.github.singlerr.semaphore.policy.callhandler.VoicechatCallConnectionHandler;

public final class ServerProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void postInit() {
        super.postInit();
    }

    public void serverStarted(VoicechatServerStartedEvent event) {
        // InMemory database
        CallConnectionHandler voicechatBasedHandler =
                new VoicechatCallConnectionHandler(DatabaseAccess.getInstance(), event.getVoicechat());
        if (CallConnectionHandlerAccess.getInstance() instanceof CallConnectionHandlerAdapter) {
            ((CallConnectionHandlerAdapter) CallConnectionHandlerAccess.getInstance())
                    .setAdapter(voicechatBasedHandler);
        }
    }
}
