package io.github.singlerr.semaphore.proxy;

import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import io.github.singlerr.semaphore.interactors.access.call.CallConnectionHandler;
import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.admin.AdminInteractor;
import io.github.singlerr.semaphore.policy.admin.SimpleAdminInteractor;
import io.github.singlerr.semaphore.policy.callhandler.VoicechatCallConnectionHandler;
import io.github.singlerr.semaphore.policy.database.PlayerDatabase;

public final class ServerProxy extends CommonProxy{

    @Override
    public void preInit() {

    }

    @Override
    public void init() {

    }

    @Override
    public void postInit() {

    }

    public void serverStarted(VoicechatServerStartedEvent event){
        // InMemory database
        DatabaseGateway database = new PlayerDatabase();

        // Voicechat based call connection handler
        CallConnectionHandler callConnectionHandler = new VoicechatCallConnectionHandler(database, event.getVoicechat());

        AdminInteractor adminInteractor = new SimpleAdminInteractor(database, callConnectionHandler);
    }
}
