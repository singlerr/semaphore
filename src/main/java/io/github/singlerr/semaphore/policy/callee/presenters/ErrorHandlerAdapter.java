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

public final class ErrorHandlerAdapter implements ErrorHandler {

    private Supplier<ErrorContext> contextSupplier;

    private Collection<PredicatePresenter> registeredPresenters;

    public ErrorHandlerAdapter(Supplier<ErrorContext> contextSupplier) {
        this.contextSupplier = contextSupplier;
        this.registeredPresenters = new ArrayList<>();
    }

    public ErrorHandlerAdapter() {
        this.contextSupplier = () -> null;
        this.registeredPresenters = new ArrayList<>();
    }

    public void setContextSupplier(Supplier<ErrorContext> contextSupplier) {
        this.contextSupplier = contextSupplier;
    }

    public void initialize(EntityPresenterAdapter.PredicatePresenter... presenters) {
        registeredPresenters = Arrays.asList(presenters);
    }

    private void invoke(ErrorContext context, Error entity) {
        for (PredicatePresenter presenter : registeredPresenters) {
            if (presenter.shouldPresent(context)) presenter.getPresenter().error(entity);
        }
    }

    @Override
    public void error(Error entity) {
        ErrorContext context = contextSupplier.get();
        if (context == null) return;

        invoke(context, entity);
    }

    public static class ErrorContext {

        private final GuiScreen currentScreen;

        public ErrorContext(GuiScreen currentScreen) {
            this.currentScreen = currentScreen;
        }

        public GuiScreen getCurrentScreen() {
            return currentScreen;
        }
    }

    public static class PredicatePresenter {

        private final Predicate<ErrorContext> condition;
        private final ErrorHandler presenter;

        public PredicatePresenter(@NotNull Predicate<ErrorContext> condition, @NotNull ErrorHandler presenter) {
            this.condition = condition;
            this.presenter = presenter;
        }

        public boolean shouldPresent(ErrorContext context) {
            return condition.test(context);
        }

        public ErrorHandler getPresenter() {
            return presenter;
        }
    }
}
