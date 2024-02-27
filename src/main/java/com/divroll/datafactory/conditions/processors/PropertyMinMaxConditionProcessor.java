package com.divroll.datafactory.conditions.processors;

import com.divroll.datafactory.conditions.PropertyMinMaxCondition;
import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import com.google.common.collect.Range;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

public class PropertyMinMaxConditionProcessor extends UnsatisfiedConditionProcessorBase<PropertyMinMaxCondition> {
    public PropertyMinMaxConditionProcessor() {
        super(PropertyMinMaxCondition.class);
    }

    @Override
    protected void processCondition(AtomicReference<EntityIterable> scope, PropertyMinMaxCondition entityCondition, Entity entityInContext, StoreTransaction txn) {
        PropertyMinMaxCondition minMaxCondition = (PropertyMinMaxCondition) entityCondition;
        String propertyName = minMaxCondition.propertyName();
        Comparable minValue = minMaxCondition.minValue();
        Comparable maxValue = minMaxCondition.maxValue();
        Comparable propertyValue = entityInContext.getProperty(propertyName);
        Range<Comparable> range = Range.closed(minValue, maxValue);
        if (!range.contains(propertyValue)) {
            throw new UnsatisfiedConditionException(entityCondition);
        }
    }
}
