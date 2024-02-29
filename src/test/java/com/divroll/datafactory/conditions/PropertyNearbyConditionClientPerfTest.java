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

import com.divroll.datafactory.*;
import com.divroll.datafactory.builders.Entities;
import com.divroll.datafactory.builders.Entity;
import com.divroll.datafactory.builders.EntityBuilder;
import com.divroll.datafactory.builders.queries.EntityQuery;
import com.divroll.datafactory.builders.queries.EntityQueryBuilder;
import com.divroll.datafactory.repositories.EntityStore;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:kerby@divroll.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
public class PropertyNearbyConditionClientPerfTest {

  private static final Logger LOG = LoggerFactory.getLogger(PropertyNearbyConditionClientPerfTest.class);
  private static DataFactory dataFactory = DataFactory.getInstance();

  @BeforeClass
  public static void beforeAll() throws Exception {
    try {
      System.setProperty(Constants.JAVA_RMI_TEST_PORT_ENVIRONMENT, TestEnvironment.getPort());
      dataFactory.register();
      await().atMost(5, TimeUnit.SECONDS);
    } catch (RemoteException e) {
      throw new RuntimeException(e);
    } catch (NotBoundException e) {
      throw new RuntimeException(e);
    }
  }

  @AfterClass
  public static void afterAll() {
    dataFactory.release();
  }

  @Test
  public void testPropertyNearbyCondition() throws Exception {
    EntityStore entityStore = DataFactoryClient.getInstance(TestEnvironment.getDomain(), TestEnvironment.getPort()).getEntityStore();
    assertNotNull(entityStore);
    String environment = TestEnvironment.getEnvironment();
    long start = System.currentTimeMillis();

    for (int i = 0; i < 999; i++) {
      entityStore.saveEntity(new EntityBuilder()
          .environment(environment)
          .entityType("Room")
          .putPropertyMap("geoLocation", new GeoPoint(120.954228, 14.301893))
          .build()).get();
    }

    entityStore.saveEntity(new EntityBuilder()
        .environment(environment)
        .entityType("Room")
        .putPropertyMap("geoLocation", new GeoPoint(120.976187, 14.581310))
        .build()).get();

    Entities dataFactoryEntities =
        entityStore.getEntities(new EntityQueryBuilder()
            .environment(environment)
            .entityType("Room")
            .max(10000)
            .build()).get();

    Assert.assertEquals(1000L, dataFactoryEntities.count().longValue());

    long time = System.currentTimeMillis() - start;
    LOG.info("Time to save complete (ms): " + time);
    start = System.currentTimeMillis();
    List<Entity> matched = new ArrayList<>();
    List<Long> times = new ArrayList<>();

    boolean hasMore = true;
    int offset = 0;
    int max = 100;
    int loopCount = 0;
    while (true) {
      EntityQuery entityQuery = new EntityQueryBuilder()
          .environment(environment)
          .entityType("Room")
          .addConditions(new PropertyNearbyConditionBuilder()
              .propertyName("geoLocation")
              .longitude(120.976187)
              .latitude(14.581310)
              .distance(100.0)
              .build())
          .offset(offset)
          .max(max)
          .build();
      Entities entities = entityStore.getEntities(entityQuery).get();
      matched.addAll(entities.entities());
      time = System.currentTimeMillis() - start;
      times.add(time);
      loopCount++;
      offset = loopCount * max;
      hasMore = ((offset * max) < entities.count());
      if (!hasMore) {
        break;
      }
    }
    times.forEach(aLong -> {
      LOG.info("Time to query complete (ms): " + aLong);
    });
    assertEquals(1L, loopCount);
    assertEquals(1L, matched.size());
  }


  @Test
  public void testNearbyConditionWithGeoHash() throws Exception {
    String environment = TestEnvironment.getEnvironment();
    EntityStore entityStore = DataFactoryClient.getInstance(TestEnvironment.getDomain(), TestEnvironment.getPort()).getEntityStore();

    Entity firstLocation = new EntityBuilder()
        .environment(environment)
        .entityType("Room")
        .putPropertyMap("address", "Room 123, 456 Street, 789 Avenue")
        .putPropertyMap("geoLocation", new GeoHash(120.976171, 14.580919).toString())
        .build();
    firstLocation = entityStore.saveEntity(firstLocation).get();
    assertNotNull(firstLocation.entityId());
    for(int i=0;i<10000;i++){
      Entity secondLocation = new EntityBuilder()
          .environment(environment)
          .entityType("Room")
          .putPropertyMap("address", "Room 456, 789 Street, 012 Avenue")
          .putPropertyMap("geoLocation", new GeoHash(121.016723, 14.511879).toString())
          .build();
      secondLocation = entityStore.saveEntity(secondLocation).get();
      //assertNotNull(secondLocation.entityId());
    }
    Entity thirdLocation = new EntityBuilder()
        .environment(environment)
        .entityType("Room")
        .putPropertyMap("address", "Room 456, 789 Street, 012 Avenue")
        .putPropertyMap("geoLocation", new GeoHash(120.976619, 14.581578).toString())
        .build();
    thirdLocation = entityStore.saveEntity(thirdLocation).get();
    assertNotNull(thirdLocation);
    long start = System.currentTimeMillis();
    EntityQuery entityQuery = new EntityQueryBuilder()
        .environment(environment)
        .entityType("Room")
        .addConditions(new PropertyNearbyConditionBuilder()
            .propertyName("geoLocation")
            .longitude(120.976619)
            .latitude(14.581578)
            .distance(100.00)
            .useGeoHash(true)
            .build())
        .build();
    Entities entities = entityStore.getEntities(entityQuery).get();
    long time = System.currentTimeMillis() - start;
    LOG.info("Time to complete query (ms): " + time);
    assertNotNull(entities);
    assertEquals(2, entities.entities().size());
  }
}
