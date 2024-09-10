/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.callee.presenters;

import io.github.singlerr.semaphore.interactors.callee.presenter.CallResponsePresenter;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.CallResponse;
import io.github.singlerr.semaphore.interactors.callee.presenter.data.Error;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public final class CallPresenterAdapter implements CallResponsePresenter {

    private Supplier<PresenterContext> contextSupplier;

    private Collection<PredicatePresenter> registeredPresenters;

    public CallPresenterAdapter(Supplier<PresenterContext> contextSupplier) {
        this.contextSupplier = contextSupplier;
        this.registeredPresenters = new ArrayList<>();
    }

    public CallPresenterAdapter() {
        this.contextSupplier = () -> null;
        this.registeredPresenters = new ArrayList<>();
    }

    public void setContextSupplier(Supplier<PresenterContext> contextSupplier) {
        this.contextSupplier = contextSupplier;
    }

    public void initialize(CallPresenterAdapter.PredicatePresenter... presenters) {
        registeredPresenters.addAll(Arrays.asList(presenters));
    }

    public void add(PredicatePresenter presenter) {
        synchronized (registeredPresenters) {
            registeredPresenters.add(presenter);
        }
    }

    private void invoke(PresenterContext context, Error entity) {
        for (PredicatePresenter presenter : registeredPresenters) {
            if (presenter.shouldPresent(context)) presenter.getPresenter().error(entity);
        }
    }

    private void invoke(PresenterContext context, CallResponse entity) {
        for (PredicatePresenter presenter : registeredPresenters) {
            if (presenter.shouldPresent(context)) presenter.getPresenter().present(entity);
        }
    }

    @Override
    public void error(Error entity) {
        PresenterContext context = contextSupplier.get();
        if (context == null) return;

        invoke(context, entity);
    }

    @Override
    public void present(CallResponse entity) {
        PresenterContext context = contextSupplier.get();
        if (context == null) return;

        invoke(context, entity);
    }

    public static class PresenterContext {}

    public static class PredicatePresenter {

        private final Predicate<PresenterContext> condition;
        private final CallResponsePresenter presenter;

        public PredicatePresenter(
                @NotNull Predicate<PresenterContext> condition, @NotNull CallResponsePresenter presenter) {
            this.condition = condition;
            this.presenter = presenter;
        }

        public boolean shouldPresent(PresenterContext context) {
            return condition.test(context);
        }

        public CallResponsePresenter getPresenter() {
            return presenter;
        }
    }
}
