package com.divroll.datafactory.conditions.processors;

import com.divroll.datafactory.conditions.PropertyStartsWithCondition;
import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

public class PropertyStartsWithConditionProcessor extends UnsatisfiedConditionProcessorBase<PropertyStartsWithCondition> {
    public PropertyStartsWithConditionProcessor() {
        super(PropertyStartsWithCondition.class);
    }

    @Override
    protected void processCondition(AtomicReference<EntityIterable> scope, PropertyStartsWithCondition entityCondition,
                                    Entity entityInContext, StoreTransaction txn) throws UnsatisfiedConditionException {
        PropertyStartsWithCondition startsWithCondition =
                (PropertyStartsWithCondition) entityCondition;
        String propertyName = startsWithCondition.propertyName();
        String startsWith = startsWithCondition.startsWith();
        Comparable propertyValue = entityInContext.getProperty(propertyName);
        if (!String.class.isAssignableFrom(propertyValue.getClass())) {
            throw new UnsatisfiedConditionException(entityCondition);
        } else {
            String stringProperty = (String) propertyValue;
            if (!stringProperty.startsWith(startsWith)) {
                throw new UnsatisfiedConditionException(entityCondition);
            }
        }
    }
}

