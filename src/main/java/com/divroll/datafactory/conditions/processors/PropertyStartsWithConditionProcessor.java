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

import com.divroll.datafactory.conditions.PropertyStartsWithCondition;
import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The PropertyStartsWithConditionProcessor class is a processor class that handles the PropertyStartsWithCondition.
 * It extends the UnsatisfiedConditionProcessorBase class and implements the UnsatisfiedConditionProcessor interface.
 */
public class PropertyStartsWithConditionProcessor extends UnsatisfiedConditionProcessorBase<PropertyStartsWithCondition> {
    /**
     * PropertyStartsWithConditionProcessor is a processor class that handles the PropertyStartsWithCondition.
     * It extends the UnsatisfiedConditionProcessorBase class and implements the UnsatisfiedConditionProcessor interface.
     */
    public PropertyStartsWithConditionProcessor() {
        super(PropertyStartsWithCondition.class);
    }

    /**
     * Process the given condition by checking if the property value of the entity
     * starts with the specified value.
     *
     * @param scope The atomic reference to hold the entity iterable.
     * @param entityCondition The condition to process, must be an instance of PropertyStartsWithCondition.
     * @param entityInContext The entity in context.
     * @param txn The store transaction.
     * @throws UnsatisfiedConditionException If the condition is not satisfied.
     */
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

