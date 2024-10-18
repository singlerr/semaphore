/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.admin.presenters;

import io.github.singlerr.semaphore.interactors.admin.presenter.CallConnectionPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableCallConnection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class CallConnectionPresenterAdapter implements CallConnectionPresenter {

    private Supplier<PresenterContext> contextSupplier;

    private Collection<PredicatePresenter> registeredPresenters;

    public CallConnectionPresenterAdapter(Supplier<PresenterContext> contextSupplier) {
        this.contextSupplier = contextSupplier;
        this.registeredPresenters = new ArrayList<>();
    }

    public CallConnectionPresenterAdapter() {
        this.contextSupplier = () -> null;
        this.registeredPresenters = new ArrayList<>();
    }

    public void setContextSupplier(Supplier<PresenterContext> contextSupplier) {
        this.contextSupplier = contextSupplier;
    }

    public void initialize(PredicatePresenter... presenters) {
        registeredPresenters.addAll(Arrays.asList(presenters));
    }

    public void add(PredicatePresenter presenter) {
        registeredPresenters.add(presenter);
    }

    private void invoke(PresenterContext context, PresentableCallConnection entity) {
        for (PredicatePresenter presenter : registeredPresenters) {
            if (presenter.shouldPresent(context)) presenter.getPresenter().present(entity);
        }
    }

    private void invoke(PresenterContext context, ErrorEntity entity) {
        for (PredicatePresenter presenter : registeredPresenters) {
            if (presenter.shouldPresentError(context)) presenter.getPresenter().presentError(entity);
        }
    }

    @Override
    public void present(PresentableCallConnection entity) {
        PresenterContext context = contextSupplier.get();
        if (context == null) return;

        invoke(context, entity);
    }

    @Override
    public void presentError(ErrorEntity error) {
        PresenterContext context = contextSupplier.get();
        if (context == null) return;

        invoke(context, error);
    }

    public static class PresenterContext {
    }

    public static class PredicatePresenter {

        private final Predicate<PresenterContext> condition;
        private final Predicate<PresenterContext> errorCondition;
        private final CallConnectionPresenter presenter;

        public PredicatePresenter(
                @NotNull Predicate<PresenterContext> condition,
                @NotNull Predicate<PresenterContext> errorCondition,
                @NotNull CallConnectionPresenter presenter) {
            this.condition = condition;
            this.errorCondition = errorCondition;
            this.presenter = presenter;
        }

        public boolean shouldPresent(PresenterContext context) {
            return condition.test(context);
        }

        public boolean shouldPresentError(PresenterContext context) {
            return errorCondition.test(context);
        }

        public CallConnectionPresenter getPresenter() {
            return presenter;
        }
    }
}
