package com.divroll.datafactory.conditions.processors;

import com.divroll.datafactory.conditions.PropertyUniqueCondition;
import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

public class PropertyUniqueConditionProcessor extends UnsatisfiedConditionProcessorBase<PropertyUniqueCondition> {
    public PropertyUniqueConditionProcessor() {
        super(PropertyUniqueCondition.class);
    }

    @Override
    protected void processCondition(AtomicReference<EntityIterable> scope, PropertyUniqueCondition entityCondition,
                                    Entity entityInContext, StoreTransaction txn) {
        String propertyName = entityCondition.propertyName();
        Comparable propertyValue = entityCondition.propertyValue();
        if (scope.get()
                .intersect(txn.find(entityInContext.getType(), propertyName, propertyValue))
                .getFirst() != null) {
            throw new UnsatisfiedConditionException(entityCondition);
        }
    }
}
