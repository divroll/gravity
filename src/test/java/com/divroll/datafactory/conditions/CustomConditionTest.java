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
package com.divroll.datafactory.conditions;

import com.divroll.datafactory.DataFactory;
import com.divroll.datafactory.TestEnvironment;
import com.divroll.datafactory.actions.IncrementLikesAction;
import com.divroll.datafactory.builders.Entity;
import com.divroll.datafactory.builders.EntityBuilder;
import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import com.divroll.datafactory.repositories.EntityStore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:kerby@divroll.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
public class CustomConditionTest {

  @Test(expected = UnsatisfiedConditionException.class)
  public void testCustomConditionShouldFail() throws Exception {
    String environment = TestEnvironment.getEnvironment();
    EntityStore entityStore = DataFactory.getInstance().getEntityStore();
    Entity entity = entityStore.saveEntity(new EntityBuilder()
        .environment(environment)
        .entityType("Room")
        .putPropertyMap("address", "Room 123, 456 Street, 789 Avenue")
        .build()).get();

    // Update entity if HasBeenLikedCondition is satisfied
    Entity updated = entityStore.saveEntity(new EntityBuilder()
        .environment(environment)
        .entityId(entity.entityId())
        .addConditions(new HasBeenLikedCondition())
        .addActions(new IncrementLikesAction(100))
        .build()).get();
  }

  @Test
  public void testCustomCondition() throws Exception {
    String environment = TestEnvironment.getEnvironment();
    EntityStore entityStore = DataFactory.getInstance().getEntityStore();
    Entity entity = entityStore.saveEntity(new EntityBuilder()
        .environment(environment)
        .entityType("Room")
        .putPropertyMap("address", "Room 123, 456 Street, 789 Avenue")
        .build()).get();

    entityStore.saveEntity(new EntityBuilder()
        .environment(environment)
        .entityId(entity.entityId())
        .putPropertyMap("likes", 1)
        .build()).get();

    // Update entity if HasBeenLikedCondition is satisfied
    Entity shouldBeUpdated = entityStore.saveEntity(new EntityBuilder()
        .environment(environment)
        .entityId(entity.entityId())
        .addConditions(new HasBeenLikedCondition())
        .addActions(new IncrementLikesAction(100))
        .build()).get();

    assertNotNull(shouldBeUpdated);
    assertEquals(101, shouldBeUpdated.propertyMap().get("likes"));
  }
}
