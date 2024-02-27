package com.divroll.datafactory.conditions.processors.core;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.concurrent.atomic.AtomicReference;

import com.divroll.datafactory.conditions.EntityCondition;

public interface UnsatisfiedConditionProcessor<EntityCondition> {
    void process(AtomicReference<EntityIterable> scope, com.divroll.datafactory.conditions.EntityCondition entityCondition, Entity entityInContext, StoreTransaction txn);
    Class<?> canProcess();
}

