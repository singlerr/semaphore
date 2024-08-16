/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.caller.presenters;

import io.github.singlerr.semaphore.interactors.caller.presenter.CallRequestPresenter;
import io.github.singlerr.semaphore.interactors.caller.presenter.data.InverseCallRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public final class CallRequestPresenterAdapter implements CallRequestPresenter {

    private Supplier<PresenterContext> contextSupplier;

    private Collection<PredicatePresenter> registeredPresenters;

    public CallRequestPresenterAdapter(Supplier<PresenterContext> contextSupplier) {
        this.contextSupplier = contextSupplier;
        this.registeredPresenters = new ArrayList<>();
    }

    public CallRequestPresenterAdapter() {
        this.contextSupplier = () -> null;
        this.registeredPresenters = new ArrayList<>();
    }

    public void setContextSupplier(Supplier<PresenterContext> contextSupplier) {
        this.contextSupplier = contextSupplier;
    }

    public void initialize(CallRequestPresenterAdapter.PredicatePresenter... presenters) {
        registeredPresenters = Arrays.asList(presenters);
    }

    private void invoke(PresenterContext context, InverseCallRequest entity) {
        for (PredicatePresenter presenter : registeredPresenters) {
            if (presenter.shouldPresent(context)) presenter.getPresenter().present(entity);
        }
    }

    @Override
    public void present(InverseCallRequest request) {
        PresenterContext context = contextSupplier.get();
        if (context == null) return;

        invoke(context, request);
    }

    public static class PresenterContext {}

    public static class PredicatePresenter {

        private final Predicate<PresenterContext> condition;
        private final CallRequestPresenter presenter;

        public PredicatePresenter(
                @NotNull Predicate<PresenterContext> condition, @NotNull CallRequestPresenter presenter) {
            this.condition = condition;
            this.presenter = presenter;
        }

        public boolean shouldPresent(PresenterContext context) {
            return condition.test(context);
        }

        public CallRequestPresenter getPresenter() {
            return presenter;
        }
    }
}
