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
import com.divroll.datafactory.LocalTimeRange;
import com.divroll.datafactory.TestEnvironment;
import com.divroll.datafactory.builders.Entities;
import com.divroll.datafactory.builders.EntityBuilder;
import com.divroll.datafactory.builders.queries.EntityQueryBuilder;
import com.divroll.datafactory.repositories.EntityStore;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;

public class PropertyLocalTimeRangeConditionTest {
  @Test
  public void testInRangeCondition() throws RemoteException, NotBoundException {
    EntityStore entityStore = DataFactory.getInstance().getEntityStore();
    String environment = TestEnvironment.getEnvironment();
    entityStore.saveEntity(new EntityBuilder()
        .environment(environment)
        .entityType("Room")
        .putPropertyMap("SUNDAY",
            new LocalTimeRange(LocalTime.parse("07:00"), LocalTime.parse("18:00")))
        .build()).get();

    entityStore.saveEntity(new EntityBuilder()
        .environment(environment)
        .entityType("Room")
        .putPropertyMap("SUNDAY",
            new LocalTimeRange(LocalTime.parse("08:00"), LocalTime.parse("22:00")))
        .build()).get();

    entityStore.saveEntity(new EntityBuilder()
        .environment(environment)
        .entityType("Room")
        .putPropertyMap("SUNDAY",
            new LocalTimeRange(LocalTime.parse("01:00"), LocalTime.parse("06:00")))
        .build()).get();

    entityStore.saveEntity(new EntityBuilder()
        .environment(environment)
        .entityType("Room")
        .putPropertyMap("MONDAY",
            new LocalTimeRange(LocalTime.parse("07:00"), LocalTime.parse("23:59")))
        .build()).get();

    Entities entities = entityStore.getEntities(new EntityQueryBuilder()
        .environment(environment)
        .entityType("Room")
        .addConditions(new PropertyLocalTimeRangeConditionBuilder()
            .propertyName("SUNDAY")
            .lower(LocalTime.parse("10:00"))
            .upper(LocalTime.parse("11:00"))
            .build())
        .build()).get();
    Assert.assertNotNull(entities);
    Assert.assertNotNull(entities.entities());
    Assert.assertEquals(2L, entities.count().longValue());

    Entities entities2 = entityStore.getEntities(new EntityQueryBuilder()
        .environment(environment)
        .entityType("Room")
        .addConditions(new PropertyLocalTimeRangeConditionBuilder()
            .propertyName("MONDAY")
            .lower(LocalTime.parse("19:00"))
            .upper(LocalTime.parse("20:00"))
            .build())
        .build()).get();
    Assert.assertNotNull(entities2);
    Assert.assertNotNull(entities2.entities());
    Assert.assertEquals(1L, entities2.count().longValue());

    Entities entities3 = entityStore.getEntities(new EntityQueryBuilder()
        .environment(environment)
        .entityType("Room")
        .addConditions(new PropertyLocalTimeRangeConditionBuilder()
            .propertyName("MONDAY")
            .lower(LocalTime.parse("01:00"))
            .upper(LocalTime.parse("06:00"))
            .build())
        .build()).get();
    Assert.assertNotNull(entities3);
    Assert.assertNotNull(entities3.entities());
    Assert.assertEquals(0L, entities3.count().longValue());

    Entities entities4 = entityStore.getEntities(new EntityQueryBuilder()
        .environment(environment)
        .entityType("Room")
        .addConditions(new PropertyLocalTimeRangeConditionBuilder()
            .propertyName("MONDAY")
            .lower(LocalTime.parse("07:00"))
            .upper(LocalTime.parse("23:59"))
            .build())
        .build()).get();
    Assert.assertNotNull(entities4);
    Assert.assertNotNull(entities4.entities());
    Assert.assertEquals(1L, entities4.count().longValue());
  }
}
