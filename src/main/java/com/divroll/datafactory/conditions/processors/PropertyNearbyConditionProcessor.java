/*
 * Divroll, Platform for Hosting Static Sites
 * Copyright 2024, Divroll, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.divroll.datafactory.conditions.processors;

import com.divroll.datafactory.conditions.PropertyNearbyCondition;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The PropertyNearbyConditionProcessor class is a processor that handles
 * the PropertyNearbyCondition.
 */
public class PropertyNearbyConditionProcessor
        extends UnsatisfiedConditionProcessorBase<PropertyNearbyCondition> {
    /**
     * The PropertyNearbyConditionProcessor class is a processor that handles the
     * PropertyNearbyCondition.
     *
     * @see UnsatisfiedConditionProcessorBase
     * @see PropertyNearbyCondition
     */
    public PropertyNearbyConditionProcessor() {
        super(PropertyNearbyCondition.class);
    }

    /**
     * Process the given PropertyNearbyCondition entity condition.
     *
     * @param scope         The atomic reference to hold the entity iterable.
     * @param entityCondition The PropertyNearbyCondition entity condition to process.
     * @param entity        The entity in context.
     * @param storeTransaction           The store transaction.
     * @throws IllegalArgumentException if the method is not yet implemented
     */
    @Override
    protected void processCondition(final AtomicReference<EntityIterable> scope,
                                    final PropertyNearbyCondition entityCondition,
                                    final Entity entity,
                                    final StoreTransaction storeTransaction) {
        PropertyNearbyCondition nearbyCondition = (PropertyNearbyCondition) entityCondition;
        throw new IllegalArgumentException("Not yet implemented");
    }
}
