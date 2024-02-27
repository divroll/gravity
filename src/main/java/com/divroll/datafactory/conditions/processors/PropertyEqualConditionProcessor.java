package com.divroll.datafactory.conditions.processors;

import com.divroll.datafactory.conditions.PropertyEqualCondition;
import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

public class PropertyEqualConditionProcessor extends UnsatisfiedConditionProcessorBase<PropertyEqualCondition> {
    public PropertyEqualConditionProcessor() {
        super(PropertyEqualCondition.class);
    }

    @Override
    protected void processCondition(AtomicReference<EntityIterable> scope, PropertyEqualCondition entityCondition, Entity entityInContext, StoreTransaction txn) {
        PropertyEqualCondition equalCondition = (PropertyEqualCondition) entityCondition;
        String propertyName = equalCondition.propertyName();
        Comparable expectedPropertyValue = equalCondition.propertyValue();
        Comparable propertyValue = entityInContext.getProperty(propertyName);
        if (!expectedPropertyValue.equals(propertyValue)) {
            throw new UnsatisfiedConditionException(entityCondition);
        }
    }
}
