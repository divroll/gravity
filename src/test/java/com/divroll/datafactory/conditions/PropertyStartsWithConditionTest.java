/*
 * Divroll, Platform for Hosting Static Sites
 * Copyright 2024, Divroll, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
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
import com.divroll.datafactory.builders.Entity;
import com.divroll.datafactory.builders.EntityBuilder;
import com.divroll.datafactory.builders.queries.EntityQuery;
import com.divroll.datafactory.builders.queries.EntityQueryBuilder;
import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import com.divroll.datafactory.repositories.EntityStore;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PropertyStartsWithConditionTest {
    private static final String ENTITY_TYPE = "Foo";
    protected static EntityStore entityStore;

    @Before
    public void setup() throws Exception {
        this.entityStore = DataFactory.getInstance().getEntityStore();
    }

    @Test
    public void saveEntityWithStartsWithCondition_WhenConditionMet_ShouldUpdateEntity()
            throws Exception {
        String environment = TestEnvironment.getEnvironment();
        Entity createdEntity = createEntityWithProperty(environment);

        Entity entityToUpdate = new EntityBuilder()
                .environment(environment)
                .entityId(createdEntity.entityId())
                .putPropertyMap("hasFooBar", true)
                .addConditions(new PropertyStartsWithConditionBuilder()
                        .propertyName("foo")
                        .startsWith("fooBar")
                        .build())
                .build();

        Entity updatedEntity = entityStore.saveEntity(entityToUpdate).get();
        assertNotNull("Entity should not be null after save", updatedEntity);
        assertEquals("Entity ID should be the same after update", createdEntity.entityId(), updatedEntity.entityId());

        EntityQuery entityQuery = new EntityQueryBuilder()
                .environment(environment)
                .entityId(updatedEntity.entityId())
                .build();

        // Get the finalEntity and validate it outside the lambda expression
        Entity finalEntity = entityStore.getEntity(entityQuery).get();
        assertEquals("fooBar", finalEntity.propertyMap().get("foo"));
        assertEquals(true, finalEntity.propertyMap().get("hasFooBar"));
    }

    @Test(expected = UnsatisfiedConditionException.class)
    public void testSaveEntityWithStartsWithShouldFail() throws Exception {
        String environment = TestEnvironment.getEnvironment();
        Entity createdEntity = createEntityWithProperty(environment);

        Entity entityToUpdate = new EntityBuilder()
                .environment(environment)
                .entityId(createdEntity.entityId())
                .putPropertyMap("hasFooBaz", true)
                .addConditions(new PropertyStartsWithConditionBuilder()
                        .propertyName("foo")
                        .startsWith("fooBaz")
                        .build())
                .build();

        entityStore.saveEntity(entityToUpdate).get();
    }

    private Entity createEntityWithProperty(String environment) throws Exception {
        Entity entity = new EntityBuilder()
                .environment(environment)
                .entityType(ENTITY_TYPE)
                .putPropertyMap("foo", "fooBar")
                .build();
        entity = entityStore.saveEntity(entity).get();
        return entity;
    }
}
