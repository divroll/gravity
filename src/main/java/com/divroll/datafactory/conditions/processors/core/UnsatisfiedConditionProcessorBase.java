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

import com.divroll.datafactory.conditions.EntityCondition;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The UnsatisfiedConditionProcessorBase class is a base class for processors that handle unsatisfied conditions.
 * It provides a constructor that throws an exception to prevent instantiation of the base class.
 *
 * Note: This class is not meant to be instantiated directly. Use a derived class instead.
 *
 * @param <T> The type of the condition that this processor can handle.
 */
public abstract class UnsatisfiedConditionProcessorBase<T extends EntityCondition> implements UnsatisfiedConditionProcessor<T> {
    /**
     * The conditionClass variable is a private final Class object that represents the type of condition that a processor can handle.
     *
     * It is used as a parameter in the constructor of processors that handle unsatisfied conditions.
     * It is also used to check if a processor can handle a specific condition.
     *
     * It is a generic type parameter, so the actual type is determined at runtime.
     */
    private final Class<T> conditionClass;

    /**
     * The UnsatisfiedConditionProcessorBase class is a base class for processors that handle unsatisfied conditions.
     * It provides a constructor that throws an exception to prevent instantiation of the base class.
     *
     * Note: This class is not meant to be instantiated directly. Use a derived class instead.
     */
    private UnsatisfiedConditionProcessorBase() {
        throw new UnsupportedOperationException("No-arg constructor is not supported");
    }

    /**
     * Base class for unsatisfied condition processors.
     * Implements the {@link UnsatisfiedConditionProcessor} interface.
     *
     * @param <T> The type of the condition that this processor can handle.
     */
    public UnsatisfiedConditionProcessorBase(Class<T> conditionClass) {
        this.conditionClass = conditionClass;
    }

    /**
     * Processes the entity condition by checking its type and invoking the corresponding processing method.
     *
     * @param scope             The atomic reference to hold the entity iterable.
     * @param entityCondition   The entity condition to process.
     * @param entityInContext   The entity in context.
     * @param txn               The store transaction.
     *
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
     * @see com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException
     */
    @Override
    public void process(AtomicReference<EntityIterable> scope, EntityCondition entityCondition, Entity entityInContext, StoreTransaction txn) {
        if(conditionClass.isInstance(entityCondition)) {
            processCondition(scope, conditionClass.cast(entityCondition), entityInContext, txn);
        }
    }

    /**
     * Returns the class type that the processor can handle.
     * @return The class type that the processor can handle.
     */
    @Override
    public Class<?> canProcess() {
        return conditionClass;
    }

    /**
     * Process the given entity condition based on the processor's condition class.
     *
     * @param scope         The atomic reference to hold the entity iterable.
     * @param entityCondition The entity condition to process.
     * @param entity        The entity in context.
     * @param txn           The store transaction.
     */
    protected abstract void processCondition(AtomicReference<EntityIterable> scope, T entityCondition, Entity entity, StoreTransaction txn);
}
