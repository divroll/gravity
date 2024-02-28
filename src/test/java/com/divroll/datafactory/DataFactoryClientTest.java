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
package com.divroll.datafactory;

import com.divroll.datafactory.builders.DataFactoryEntity;
import com.divroll.datafactory.builders.DataFactoryEntityBuilder;
import com.divroll.datafactory.exceptions.DataFactoryException;
import com.divroll.datafactory.repositories.EntityStore;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.awaitility.Awaitility.await;

public class DataFactoryClientTest {
  private static DataFactory dataFactory = DataFactory.getInstance();
  private static DataFactoryClient client;

  @BeforeClass
  public static void beforeTest() throws Exception {
    try {
      System.setProperty(Constants.JAVA_RMI_TEST_PORT_ENVIRONMENT, TestEnvironment.getPort());
      dataFactory.register();
      await().atMost(5, TimeUnit.SECONDS);
      client = DataFactoryClient.getInstance(TestEnvironment.getDomain(), TestEnvironment.getPort());
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
  public void testGetInstance() throws Exception {
    EntityStore entityStore = client.getEntityStore();
    Assert.assertNotNull(entityStore);
  }

  @Test
  public void testSimpleSave() throws Exception {
    EntityStore entityStore = client.getEntityStore();
    Assert.assertNotNull(entityStore);

    DataFactoryEntity entity = new DataFactoryEntityBuilder()
            .environment(TestEnvironment.getEnvironment())
            .entityType("Foo")
            .putPropertyMap("foo", "bar")
            .build();

    DataFactoryEntity dataFactoryEntity = entityStore.saveEntity(entity).get();
    Assert.assertNotNull(dataFactoryEntity);
    Assert.assertNotNull(dataFactoryEntity.entityId());
    Assert.assertEquals("bar", dataFactoryEntity.propertyMap().get("foo"));
  }
}
