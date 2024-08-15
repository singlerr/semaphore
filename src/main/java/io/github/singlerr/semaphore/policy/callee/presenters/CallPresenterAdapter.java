/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.callee.presenters;

import io.github.singlerr.semaphore.interactors.callee.presenter.ErrorHandler;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error;
import io.github.singlerr.semaphore.policy.admin.presenters.EntityPresenterAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiScreen;
import org.jetbrains.annotations.NotNull;
import scala.actors.threadpool.Arrays;

public final class CallPresenterAdapter implements ErrorHandler {

    private final Supplier<PresenterContext> contextSupplier;

    private Collection<PredicatePresenter> registeredPresenters;

    public CallPresenterAdapter(Supplier<PresenterContext> contextSupplier) {
        this.contextSupplier = contextSupplier;
        this.registeredPresenters = new ArrayList<>();
    }

    public void initialize(EntityPresenterAdapter.PredicatePresenter... presenters) {
        registeredPresenters = Arrays.asList(presenters);
    }

    private void invoke(PresenterContext context, Error entity) {
        for (PredicatePresenter presenter : registeredPresenters) {
            if (presenter.shouldPresent(context)) presenter.getPresenter().error(entity);
        }
    }

    @Override
    public void error(Error entity) {
        PresenterContext context = contextSupplier.get();
        if (context == null) return;

        invoke(context, entity);
    }

    public static class PresenterContext {

        private final GuiScreen currentScreen;

        public PresenterContext(GuiScreen currentScreen) {
            this.currentScreen = currentScreen;
        }

        public GuiScreen getCurrentScreen() {
            return currentScreen;
        }
    }

    public static class PredicatePresenter {

        private final Predicate<PresenterContext> condition;
        private final ErrorHandler presenter;

        public PredicatePresenter(@NotNull Predicate<PresenterContext> condition, @NotNull ErrorHandler presenter) {
            this.condition = condition;
            this.presenter = presenter;
        }

        public boolean shouldPresent(PresenterContext context) {
            return condition.test(context);
        }

        public ErrorHandler getPresenter() {
            return presenter;
        }
    }
}
