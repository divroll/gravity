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

import com.divroll.datafactory.conditions.CustomCondition;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The CustomConditionProcessor class is responsible for processing CustomCondition objects.
 * It extends the UnsatisfiedConditionProcessorBase class and implements the processing logic.
 */
public class CustomConditionProcessor extends UnsatisfiedConditionProcessorBase<CustomCondition> {
    /**
     * CustomConditionProcessor is a class that extends UnsatisfiedConditionProcessorBase
     * and implements the processing logic for CustomCondition objects.
     *
     * @see UnsatisfiedConditionProcessorBase
     * @see CustomCondition
     */
    public CustomConditionProcessor() {
        super(CustomCondition.class);
    }

    /**
     * Processes the given condition by executing the entityCondition on the entityInContext.
     *
     * @param scope The atomic reference to hold the entity iterable.
     * @param entityCondition The custom condition to execute.
     * @param entityInContext The entity on which the condition will be executed.
     * @param storeTransaction The store transaction.
     * @see CustomCondition#execute(Entity)
     * @since 0-SNAPSHOT
     */
    @Override
    protected void processCondition(final AtomicReference<EntityIterable> scope,
                                    final CustomCondition entityCondition,
                                    final Entity entityInContext,
                                    final StoreTransaction storeTransaction) {
        entityCondition.execute(entityInContext);
    }

    /**
     * Returns the class type that the processor can handle.
     *
     * @return The class type that the processor can handle.
     */
    @Override
    public Class<?> canProcess() {
        return CustomCondition.class;
    }
}
