package com.divroll.datafactory.conditions.processors;

import com.divroll.datafactory.conditions.OppositeLinkCondition;
import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

public class OppositeLinkConditionProcessor extends UnsatisfiedConditionProcessorBase<OppositeLinkCondition> {
    public OppositeLinkConditionProcessor() {
        super(OppositeLinkCondition.class);
    }

    @Override
    protected void processCondition(AtomicReference<EntityIterable> scope, OppositeLinkCondition entityCondition, Entity entityInContext, StoreTransaction txn) {
        OppositeLinkCondition oppositeLinkCondition = (OppositeLinkCondition) entityCondition;
        String linkName = oppositeLinkCondition.linkName();
        String oppositeLinkName = oppositeLinkCondition.oppositeLinkName();
        String oppositeEntityId = oppositeLinkCondition.oppositeEntityId();
        Boolean isSet = oppositeLinkCondition.isSet();
        Entity oppositeEntity = entityInContext.getStore()
                .getCurrentTransaction()
                .getEntity(txn.toEntityId(oppositeEntityId));

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
