/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.proxy;

import io.github.singlerr.semaphore.instances.AdminInteractorAccess;
import io.github.singlerr.semaphore.instances.CalleeInteractorAccess;
import io.github.singlerr.semaphore.instances.CallerInteractorAccess;
import io.github.singlerr.semaphore.instances.DatabaseAccess;
import io.github.singlerr.semaphore.interactors.access.call.CallConnectionHandler;
import io.github.singlerr.semaphore.interactors.access.database.DatabaseGateway;
import io.github.singlerr.semaphore.interactors.admin.AdminInteractor;
import io.github.singlerr.semaphore.interactors.admin.presenter.CallConnectionPresenter;
import io.github.singlerr.semaphore.interactors.callee.CalleeInteractor;
import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter;
import io.github.singlerr.semaphore.interactors.callee.presenter.ErrorHandler;
import io.github.singlerr.semaphore.interactors.caller.CallerInteractor;
import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.ErrorPresenter;
import io.github.singlerr.semaphore.policy.admin.SimpleAdminInteractor;
import io.github.singlerr.semaphore.policy.admin.presenters.CallConnectionPresenterAdapter;
import io.github.singlerr.semaphore.policy.admin.presenters.EntityPresenterAdapter;
import io.github.singlerr.semaphore.policy.callee.SimpleCalleeInteractor;
import io.github.singlerr.semaphore.policy.callee.presenters.CallPresenterAdapter;
import io.github.singlerr.semaphore.policy.callee.presenters.ErrorHandlerAdapter;
import io.github.singlerr.semaphore.policy.caller.SimpleCallerInteractor;
import io.github.singlerr.semaphore.policy.caller.presenters.CallRequestPresenterAdapter;
import io.github.singlerr.semaphore.policy.caller.presenters.ErrorPresenterAdapter;
import io.github.singlerr.semaphore.policy.callhandler.CallConnectionHandlerAdapter;
import io.github.singlerr.semaphore.policy.callhandler.VoicechatCallConnectionHandler;
import io.github.singlerr.semaphore.policy.database.PlayerDatabase;

public abstract class CommonProxy {

    public void preInit() {
        initPolicy();
    }

    public void init() {}

    public void postInit() {}

    private void initPolicy(){
        DatabaseGateway database = new PlayerDatabase();

        // Due to different binding point of Minecraft and Voicechat, leave it to adapter
        CallConnectionHandler callConnectionHandler =
                new CallConnectionHandlerAdapter();

        // Lazy init
        // Register adapter and context supplier after all Minecraft components loaded
        CallConnectionPresenter callConnectionPresenter = new CallConnectionPresenterAdapter();
        EntityPresenterAdapter entityPresenterAdapter = new EntityPresenterAdapter();

        AdminInteractor adminInteractor = new SimpleAdminInteractor(database, callConnectionHandler, callConnectionPresenter, entityPresenterAdapter);

        // Lazy init
        CallRequestPresenter callRequestPresenter = new CallRequestPresenterAdapter();
        ErrorPresenter errorPresenter = new ErrorPresenterAdapter();
        CallerInteractor callerInteractor = new SimpleCallerInteractor(database, errorPresenter, callRequestPresenter);

        // Lazy init
        ErrorHandler errorHandler = new ErrorHandlerAdapter();
        CallResponsePresenter callResponsePresenter = new CallPresenterAdapter();

        CalleeInteractor calleeInteractor = new SimpleCalleeInteractor(database, callConnectionHandler, errorHandler, callResponsePresenter);


        // Make Accessor store
        DatabaseAccess.setInstance(database);
        AdminInteractorAccess.setInstance(adminInteractor);
        CallerInteractorAccess.setInstance(callerInteractor);
        CalleeInteractorAccess.setInstance(calleeInteractor);
    }
}
