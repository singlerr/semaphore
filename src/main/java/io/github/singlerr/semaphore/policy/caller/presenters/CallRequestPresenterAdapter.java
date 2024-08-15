/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.caller.presenters;

import io.github.singlerr.semaphore.interactors.caller.presenter.ErrorPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.Error;
import io.github.singlerr.semaphore.policy.admin.presenters.EntityPresenterAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiScreen;
import org.jetbrains.annotations.NotNull;
import scala.actors.threadpool.Arrays;

public final class CallRequestPresenterAdapter implements ErrorPresenter {

    private final Supplier<PresenterContext> contextSupplier;

    private Collection<PredicatePresenter> registeredPresenters;

    public CallRequestPresenterAdapter(Supplier<PresenterContext> contextSupplier) {
        this.contextSupplier = contextSupplier;
        this.registeredPresenters = new ArrayList<>();
    }

    public void initialize(EntityPresenterAdapter.PredicatePresenter... presenters) {
        registeredPresenters = Arrays.asList(presenters);
    }

    private void invoke(PresenterContext context, Error entity) {
        for (PredicatePresenter presenter : registeredPresenters) {
            if (presenter.shouldPresent(context)) presenter.getPresenter().present(entity);
        }
    }

    @Override
    public void present(Error entity) {
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
        private final ErrorPresenter presenter;

        public PredicatePresenter(@NotNull Predicate<PresenterContext> condition, @NotNull ErrorPresenter presenter) {
            this.condition = condition;
            this.presenter = presenter;
        }

        public boolean shouldPresent(PresenterContext context) {
            return condition.test(context);
        }

        public ErrorPresenter getPresenter() {
            return presenter;
        }
    }
}
