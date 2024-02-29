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

import com.divroll.datafactory.conditions.OppositeLinkCondition;
import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The OppositeLinkConditionProcessor class processes conditions of type OppositeLinkCondition.
 * It extends the UnsatisfiedConditionProcessorBase class and implements
 * the UnsatisfiedConditionProcessor interface.
 */
public class OppositeLinkConditionProcessor
        extends UnsatisfiedConditionProcessorBase<OppositeLinkCondition> {
    /**
     * OppositeLinkConditionProcessor is a class that processes conditions
     * of type OppositeLinkCondition. It extends the UnsatisfiedConditionProcessorBase class and
     * implements the UnsatisfiedConditionProcessor interface.
     */
    public OppositeLinkConditionProcessor() {
        super(OppositeLinkCondition.class);
    }
    /**
     * Processes the given OppositeLinkCondition to check if the condition is satisfied.
     * If the condition is not satisfied, it throws an UnsatisfiedConditionException.
     *
     * @param scope             The atomic reference to hold the entity iterable.
     * @param entityCondition   The OppositeLinkCondition to process.
     * @param entityInContext   The entity in context.
     * @param storeTransaction               The store transaction.
     * @throws UnsatisfiedConditionException if the condition is not satisfied.
     */
    @Override
    protected void processCondition(final AtomicReference<EntityIterable> scope,
                                    final OppositeLinkCondition entityCondition,
                                    final Entity entityInContext,
                                    final StoreTransaction storeTransaction) {
        OppositeLinkCondition oppositeLinkCondition = (OppositeLinkCondition) entityCondition;
        String linkName = oppositeLinkCondition.linkName();
        String oppositeLinkName = oppositeLinkCondition.oppositeLinkName();
        String oppositeEntityId = oppositeLinkCondition.oppositeEntityId();
        Boolean isSet = oppositeLinkCondition.isSet();
        Entity oppositeEntity = entityInContext.getStore()
                .getCurrentTransaction()
                .getEntity(storeTransaction.toEntityId(oppositeEntityId));

        if (entityInContext.getLinks(linkName).isEmpty()) {
            throw new UnsatisfiedConditionException(entityCondition);
        }

        if (oppositeEntity == null) {
            throw new UnsatisfiedConditionException(entityCondition);
        }

        if (oppositeEntity.getLinks(oppositeLinkName).isEmpty()) {
            throw new UnsatisfiedConditionException(entityCondition);
        } else if (oppositeEntity.getLinks(linkName).size() > 1 && isSet) {
            throw new UnsatisfiedConditionException(entityCondition);
        }
    }
}
