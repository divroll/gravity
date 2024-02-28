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

import com.divroll.datafactory.conditions.LinkCondition;
import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The LinkConditionProcessor class is a processor that handles the LinkCondition.
 * It extends the UnsatisfiedConditionProcessorBase class.
 *
 * @see UnsatisfiedConditionProcessorBase
 * @see LinkCondition
 */
public class LinkConditionProcessor extends UnsatisfiedConditionProcessorBase<LinkCondition> {
    /**
     * The LinkConditionProcessor class is a processor that handles the LinkCondition.
     * It extends the UnsatisfiedConditionProcessorBase class.
     *
     * @see UnsatisfiedConditionProcessorBase
     * @see LinkCondition
     */
    public LinkConditionProcessor() {
        super(LinkCondition.class);
    }
    /**
     * Processes a given condition for a specified entity within a transaction.
     *
     * @param scope            The atomic reference to hold the entity iterable.
     * @param entityCondition  The condition to be processed.
     * @param entityInContext  The entity in context.
     * @param txn              The store transaction.
     * @throws UnsatisfiedConditionException If the condition is not satisfied.
     */
    @Override
    protected void processCondition(AtomicReference<EntityIterable> scope, LinkCondition entityCondition, Entity entityInContext, StoreTransaction txn)
            throws UnsatisfiedConditionException {
        String linkName = entityCondition.linkName();
        String otherEntityId = entityCondition.otherEntityId();
        Boolean isSet = entityCondition.isSet();
        if (isSet) {
            Entity otherEntity = entityInContext.getLink(linkName);
            if (otherEntity == null) {
                throw new UnsatisfiedConditionException(entityCondition);
            } else if (otherEntity != null && !otherEntity.getId()
                    .toString()
                    .equals(otherEntityId)) {
                throw new UnsatisfiedConditionException(entityCondition);
            }
        } else {
            if (entityInContext.getLinks(linkName).isEmpty()) {
                throw new UnsatisfiedConditionException(entityCondition);
            }
        }
    }
}

