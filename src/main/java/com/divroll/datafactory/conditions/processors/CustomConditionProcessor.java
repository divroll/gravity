package com.divroll.datafactory.conditions.processors;

import com.divroll.datafactory.conditions.CustomCondition;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

public class CustomConditionProcessor extends UnsatisfiedConditionProcessorBase<CustomCondition> {
    public CustomConditionProcessor() {
        super(CustomCondition.class);
    }

    @Override
    protected void processCondition(AtomicReference<EntityIterable> scope, CustomCondition entityCondition, Entity entityInContext, StoreTransaction txn) {
        entityCondition.execute(entityInContext);
    }

    @Override
    public Class<?> canProcess() {
        return CustomCondition.class;
    }
}
