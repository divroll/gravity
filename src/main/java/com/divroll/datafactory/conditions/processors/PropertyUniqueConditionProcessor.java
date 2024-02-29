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

import com.divroll.datafactory.conditions.PropertyUniqueCondition;
import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessor;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is a processor for handling the PropertyUniqueCondition.
 *
 * @see PropertyUniqueCondition
 * @see UnsatisfiedConditionProcessorBase
 * @see UnsatisfiedConditionProcessor
 */
public class PropertyUniqueConditionProcessor
        extends UnsatisfiedConditionProcessorBase<PropertyUniqueCondition> {
    /**
     * This class is a processor for handling the PropertyUniqueCondition.
     *
     * @see PropertyUniqueCondition
     * @see UnsatisfiedConditionProcessorBase
     * @see UnsatisfiedConditionProcessor
     */
    public PropertyUniqueConditionProcessor() {
        super(PropertyUniqueCondition.class);
    }

    /**
     * Processes the given condition by intersecting the scope with the entities in
     * the store that satisfy the condition. If any entities are found, it throws
     * an UnsatisfiedConditionException.
     *
     * @param scope              The atomic reference to hold the entity iterable.
     * @param entityCondition    The property unique condition to process.
     * @param entityInContext    The entity in context.
     * @param storeTransaction                The store transaction.
     * @throws UnsatisfiedConditionException if any entities are found that satisfy the condition
     */
    @Override
    protected void processCondition(final AtomicReference<EntityIterable> scope,
                                    final PropertyUniqueCondition entityCondition,
                                    final Entity entityInContext,
                                    final StoreTransaction storeTransaction) {
        String propertyName = entityCondition.propertyName();
        Comparable propertyValue = entityCondition.propertyValue();
        if (scope.get()
                .intersect(storeTransaction
                        .find(entityInContext.getType(),
                        propertyName,
                        propertyValue))
                .getFirst() != null) {
            throw new UnsatisfiedConditionException(entityCondition);
        }
    }
}
