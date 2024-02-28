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

import com.divroll.datafactory.conditions.PropertyMinMaxCondition;
import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import com.google.common.collect.Range;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The PropertyMinMaxConditionProcessor class is responsible for processing
 * the PropertyMinMaxCondition entity condition. It extends the
 * UnsatisfiedConditionProcessorBase class and implements the
 * UnsatisfiedConditionProcessor interface.
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * PropertyMinMaxConditionProcessor processor = new PropertyMinMaxConditionProcessor();
 * AtomicReference<EntityIterable> scope = new AtomicReference<>();
 * PropertyMinMaxCondition condition = new PropertyMinMaxCondition("propertyName", minValue, maxValue);
 * Entity entity = ...
 * StoreTransaction txn = ...
 * processor.process(scope, condition, entity, txn);
 * }</pre>
 *
 * @see UnsatisfiedConditionProcessorBase
 * @see UnsatisfiedConditionProcessor
 * @see PropertyMinMaxCondition
 */
public class PropertyMinMaxConditionProcessor extends UnsatisfiedConditionProcessorBase<PropertyMinMaxCondition> {
    /**
     * The PropertyMinMaxConditionProcessor class is responsible for processing
     * the PropertyMinMaxCondition entity condition. It extends the
     * UnsatisfiedConditionProcessorBase class and implements the
     * UnsatisfiedConditionProcessor interface.
     *
     * <p>Usage example:</p>
     * <pre>{@code
     * PropertyMinMaxConditionProcessor processor = new PropertyMinMaxConditionProcessor();
     * AtomicReference<EntityIterable> scope = new AtomicReference<>();
     * PropertyMinMaxCondition condition = new PropertyMinMaxCondition("propertyName", minValue, maxValue);
     * Entity entity = ...
     * StoreTransaction txn = ...
     * processor.process(scope, condition, entity, txn);
     * }</pre>
     *
     * @see UnsatisfiedConditionProcessorBase
     * @see UnsatisfiedConditionProcessor
     * @see PropertyMinMaxCondition
     */
    public PropertyMinMaxConditionProcessor() {
        super(PropertyMinMaxCondition.class);
    }

    /**
     * Processes the given condition by checking if the property value of the entity
     * is within the specified range of minValue and maxValue. If the property value
     * is not within the range, an UnsatisfiedConditionException is thrown.
     *
     * @param scope             The atomic reference to hold the entity iterable.
     * @param entityCondition   The PropertyMinMaxCondition to process.
     * @param entityInContext   The entity in context.
     * @param txn               The store transaction.
     *
     * @throws UnsatisfiedConditionException if the property value is not within the specified range.
     *
     * @see EntityCondition
     * @see PropertyMinMaxCondition
     * @see UnsatisfiedConditionException
     */
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
