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

