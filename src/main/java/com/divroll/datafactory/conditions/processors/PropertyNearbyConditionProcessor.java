package com.divroll.datafactory.conditions.processors;

import com.divroll.datafactory.conditions.PropertyNearbyCondition;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

public class PropertyNearbyConditionProcessor extends UnsatisfiedConditionProcessorBase<PropertyNearbyCondition> {
    public PropertyNearbyConditionProcessor() {
        super(PropertyNearbyCondition.class);
    }

    @Override
    protected void processCondition(AtomicReference<EntityIterable> scope, PropertyNearbyCondition entityCondition, Entity entity, StoreTransaction txn) {
        PropertyNearbyCondition nearbyCondition = (PropertyNearbyCondition) entityCondition;
        throw new IllegalArgumentException("Not yet implemented");
    }
}
