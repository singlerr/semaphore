/* (C) 2024 singlerr */
package io.github.singlerr.semaphore.policy.admin.presenters;

import io.github.singlerr.semaphore.interactors.admin.presenter.EntityPresenter;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.ErrorEntity;
import io.github.singlerr.semaphore.interactors.admin.presenter.data.PresentableEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class EntityPresenterAdapter implements EntityPresenter {

    private Supplier<PresenterContext> contextSupplier;

    private Collection<PredicatePresenter> registeredPresenters;

    public EntityPresenterAdapter(Supplier<PresenterContext> contextSupplier) {
        this.contextSupplier = contextSupplier;
        this.registeredPresenters = new ArrayList<>();
    }

    public EntityPresenterAdapter() {
        this.contextSupplier = () -> null;
        this.registeredPresenters = new ArrayList<>();
    }

    public void initialize(PredicatePresenter... presenters) {
        this.registeredPresenters.addAll(Arrays.asList(presenters));
    }

    public void add(PredicatePresenter presenter) {
        registeredPresenters.add(presenter);
    }

    private void invoke(PresenterContext context, PresentableEntity entity) {
        for (PredicatePresenter presenter : registeredPresenters) {
            if (presenter.shouldPresent(context)) presenter.getPresenter().present(entity);
        }
    }

    private void invoke(PresenterContext context, ErrorEntity entity) {
        for (PredicatePresenter presenter : registeredPresenters) {
            if (presenter.shouldPresentError(context)) presenter.getPresenter().presentError(entity);
        }
    }

    private void invoke(PresenterContext context, List<PresentableEntity> entities) {
        for (PredicatePresenter presenter : registeredPresenters) {
            if (presenter.shouldPresentError(context)) presenter.getPresenter().present(entities);
        }
    }

    @Override
    public void present(List<PresentableEntity> entities) {
        PresenterContext context = contextSupplier.get();
        if (context == null) return;
        invoke(context, entities);
    }

    @Override
    public void present(PresentableEntity entity) {
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
        private final EntityPresenter presenter;

        public PredicatePresenter(
                @NotNull Predicate<PresenterContext> condition,
                @NotNull Predicate<PresenterContext> errorCondition,
                @NotNull EntityPresenter presenter) {
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

        public EntityPresenter getPresenter() {
            return presenter;
        }
    }
}
