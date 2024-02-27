package com.divroll.datafactory.conditions.processors;

import com.divroll.datafactory.LocalTimeRange;
import com.divroll.datafactory.conditions.PropertyLocalTimeRangeCondition;
import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import com.google.common.collect.Range;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicReference;

public class PropertyLocalTimeRangeConditionProcessor extends UnsatisfiedConditionProcessorBase<PropertyLocalTimeRangeCondition> {
    public PropertyLocalTimeRangeConditionProcessor() {
        super(PropertyLocalTimeRangeCondition.class);
    }

    @Override
    protected void processCondition(AtomicReference<EntityIterable> scope, PropertyLocalTimeRangeCondition entityCondition, Entity entityInContext, StoreTransaction txn)
            throws UnsatisfiedConditionException {
        PropertyLocalTimeRangeCondition propertyLocalTimeRangeCondition =
                (PropertyLocalTimeRangeCondition) entityCondition;
        String propertyName = propertyLocalTimeRangeCondition.propertyName();
        LocalTime upper = propertyLocalTimeRangeCondition.upper();
        LocalTime lower = propertyLocalTimeRangeCondition.lower();
        Comparable propertyValue = entityInContext.getProperty(propertyName);
        Range<Comparable> range =
                Range.closed(new LocalTimeRange(lower, lower), new LocalTimeRange(upper, upper));
        if (!range.contains(propertyValue)) {
            throw new UnsatisfiedConditionException(entityCondition);
        }
    }
}

