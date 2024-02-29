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

import com.divroll.datafactory.builders.Entity;
import com.divroll.datafactory.builders.EntityBuilder;
import org.junit.Assert;
import org.junit.Test;

public class DataFactoryClientTest extends ClientBaseTest {
  @Test
  public void testGetInstance() throws Exception {
    Assert.assertNotNull(entityStore);
  }

  @Test
  public void testSimpleSave() throws Exception {
    Assert.assertNotNull(entityStore);

    Entity entity = new EntityBuilder()
            .environment(TestEnvironment.getEnvironment())
            .entityType("Foo")
            .putPropertyMap("foo", "bar")
            .build();

    entity = entityStore.saveEntity(entity).get();
    Assert.assertNotNull(entity);
    Assert.assertNotNull(entity.entityId());
    Assert.assertEquals("bar", entity.propertyMap().get("foo"));
  }
}
