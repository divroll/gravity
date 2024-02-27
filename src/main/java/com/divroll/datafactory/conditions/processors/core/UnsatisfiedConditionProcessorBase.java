package com.divroll.datafactory.conditions.processors.core;

import com.divroll.datafactory.conditions.EntityCondition;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

public abstract class UnsatisfiedConditionProcessorBase<T extends EntityCondition> implements UnsatisfiedConditionProcessor<T> {
    private final Class<T> conditionClass;

    private UnsatisfiedConditionProcessorBase() {
        throw new UnsupportedOperationException("No-arg constructor is not supported");
    }

    public UnsatisfiedConditionProcessorBase(Class<T> conditionClass) {
        this.conditionClass = conditionClass;
    }

    @Override
    public void process(AtomicReference<EntityIterable> scope, EntityCondition entityCondition, Entity entityInContext, StoreTransaction txn) {
        if(conditionClass.isInstance(entityCondition)) {
            processCondition(scope, conditionClass.cast(entityCondition), entityInContext, txn);
        }
    }

    @Override
    public Class<?> canProcess() {
        return conditionClass;
    }

    protected abstract void processCondition(AtomicReference<EntityIterable> scope, T entityCondition, Entity entity, StoreTransaction txn);
}
