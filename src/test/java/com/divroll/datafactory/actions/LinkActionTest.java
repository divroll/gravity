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
package com.divroll.datafactory.actions;

import com.divroll.datafactory.DataFactory;
import com.divroll.datafactory.TestEnvironment;
import com.divroll.datafactory.builders.Entity;
import com.divroll.datafactory.builders.EntityBuilder;
import com.divroll.datafactory.repositories.EntityStore;
import jetbrains.exodus.entitystore.EntityRemovedInDatabaseException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
public class LinkActionTest {
    private static final String ENTITY_TYPE = "Foo";
    protected static EntityStore entityStore;

    @Before
    public void setup() throws Exception {
        this.entityStore = DataFactory.getInstance().getEntityStore();
    }

    @Test(expected = EntityRemovedInDatabaseException.class)
    public void testEntityLinkAction_WhenInvalidOtherEntityId_ShouldThrowException()
            throws Exception {
        String environment = TestEnvironment.getEnvironment();
        LinkAction linkAction = new LinkActionBuilder()
                .linkName("baz")
                .otherEntityId("0-1000")
                .isSet(true)
                .build();

        Entity entity = new EntityBuilder()
                .environment(environment)
                .entityType(ENTITY_TYPE)
                .putPropertyMap("foo", "bar")
                .addActions(linkAction)
                .build();
        entityStore.saveEntity(entity).get();
    }
}
