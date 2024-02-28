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
package com.divroll.datafactory.conditions.processors.core;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The UnsatisfiedConditionProcessor interface defines the contract for processors that handle unsatisfied conditions.
 * It provides a method to process conditions and check if a processor can handle a specific condition.
 *
 * @param <EntityCondition> The type of entity condition that the processor can handle.
 */
public interface UnsatisfiedConditionProcessor<EntityCondition> {
    /**
     * Processes the entity condition and performs the necessary actions based on the condition.
     *
     * @param scope             The atomic reference to hold the entity iterable.
     * @param entityCondition   The entity condition to process.
     * @param entityInContext   The entity in context.
     * @param txn               The store transaction.
     * @see com.divroll.datafactory.conditions.LinkCondition
     * @see com.divroll.datafactory.conditions.OppositeLinkCondition
     * @see com.divroll.datafactory.conditions.PropertyContainsCondition
     * @see com.divroll.datafactory.conditions.PropertyEqualCondition
     * @see com.divroll.datafactory.conditions.PropertyLocalTimeRangeCondition
     * @see com.divroll.datafactory.conditions.PropertyMinMaxCondition
     * @see com.divroll.datafactory.conditions.PropertyNearbyCondition
     * @see com.divroll.datafactory.conditions.PropertyStartsWithCondition
     * @see com.divroll.datafactory.conditions.PropertyUniqueCondition
     * @see com.divroll.datafactory.conditions.CustomCondition
     * @see com.divroll.datafactory.conditions.CustomQueryCondition
     * @see com.divroll.datafactory.exceptions.UnsatisfiedConditionException
     * @throws UnsatisfiedConditionException if the condition is not satisfied
     */
    void process(AtomicReference<EntityIterable> scope, com.divroll.datafactory.conditions.EntityCondition entityCondition, Entity entityInContext, StoreTransaction txn);
    /**
     * Checks if the processor can handle a specific condition.
     *
     * @return The class type that the processor can handle.
     */
    Class<?> canProcess();
}

