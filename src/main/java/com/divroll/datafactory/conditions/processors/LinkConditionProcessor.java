package com.divroll.datafactory.conditions.processors;

import com.divroll.datafactory.conditions.LinkCondition;
import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessorBase;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

public class LinkConditionProcessor extends UnsatisfiedConditionProcessorBase<LinkCondition> {
    public LinkConditionProcessor() {
        super(LinkCondition.class);
    }

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

