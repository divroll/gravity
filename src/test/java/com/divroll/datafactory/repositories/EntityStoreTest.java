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
package com.divroll.datafactory.repositories;

import com.divroll.datafactory.DataFactory;
import com.divroll.datafactory.TestEnvironment;
import com.divroll.datafactory.builders.Blob;
import com.divroll.datafactory.builders.BlobBuilder;
import com.divroll.datafactory.builders.Entity;
import com.divroll.datafactory.builders.EntityBuilder;
import com.divroll.datafactory.builders.queries.BlobQueryBuilder;
import com.divroll.datafactory.builders.queries.EntityQuery;
import com.divroll.datafactory.builders.queries.EntityQueryBuilder;
import com.google.common.io.ByteSource;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EntityStoreTest {
  EntityStore entityStore;
  @Before
  public void setUp() throws NotBoundException, RemoteException {
    this.entityStore = DataFactory.getInstance().getEntityStore();
  }

  @Test
  public void saveEntity_ShouldPersistEntitySuccessfully() throws Exception {
    String environment = TestEnvironment.getEnvironment();
    Entity createdEntity = createAndSaveEntity(environment, "Foo", "foo", "bar");

    assertNotNull("Entity should not be null after save", createdEntity);
    assertNotNull("Entity ID should not be null after save", createdEntity.entityId());
  }

  @Test
  public void saveEntityWithBlob_ShouldPersistEntitySuccessfully() throws Exception {
    String environment = TestEnvironment.getEnvironment();
    Entity createdEntity = createAndSaveEntityWithBlob(environment);

    assertNotNull("Entity should not be null after save", createdEntity);
    assertNotNull("Entity ID should not be null after save", createdEntity.entityId());

    createdEntity = retrieveEntityWithBlob(environment, createdEntity.entityId(), "message");

    // Retrieve the entity with the blob
    String[] blobNames = createdEntity.blobNames();
    List<Blob> blobs = createdEntity.blobs();

    assertEquals(1, blobNames.length);
    assertEquals("message", blobNames[0]);
    assertEquals(1, blobs.size());

    // Retrieve the blob
    Blob blob = blobs.stream().findFirst().get();
    String blobString =
            IOUtils.toString(RemoteInputStreamClient.wrap(blob.blobStream()), Charset
                    .defaultCharset());
    assertEquals("Hello Word", blobString);
  }

  @Test
  public void getEntity_ShouldRetrieveSavedEntityCorrectly() throws Exception {
    // TestEnvironment generate new UUID for each call
    String environment = TestEnvironment.getEnvironment();
    Entity createdEntity = createAndSaveEntity(environment,"Foo", "foo", "bar");
    Entity retrievedEntity = retrieveEntity(environment, createdEntity.entityId());

    assertNotNull("Retrieved entity should not be null", retrievedEntity);
    assertEquals("Entity ID should match the created entity", createdEntity.entityId(), retrievedEntity.entityId());
    assertEquals("Entity property 'foo' should match", "bar", retrievedEntity.propertyMap().get("foo"));
  }

  private Entity createAndSaveEntity(String environment, String entityType, String key, String value) throws Exception {
    EntityBuilder builder = new EntityBuilder()
            .environment(environment)
            .entityType(entityType)
            .putPropertyMap(key, value);
    return entityStore.saveEntity(builder.build()).get();
  }

  private Entity createAndSaveEntityWithBlob(String environment) throws Exception {
    RemoteInputStream blobStream
            = new SimpleRemoteInputStream(ByteSource.wrap("Hello Word".getBytes()).openStream());
    Blob blob = new BlobBuilder()
            .blobName("message")
            .blobStream(blobStream)
            .build();
    Entity entity = entityStore.saveEntity(new EntityBuilder()
            .environment(environment)
            .entityType("Foo")
            .putPropertyMap("foo", "bar")
            .addBlobs(blob)
            .build()).get();
    return entity;
  }

  private Entity retrieveEntity(String environment, String entityId) throws Exception {
    EntityQuery query = new EntityQueryBuilder()
            .environment(environment)
            .entityId(entityId)
            .build();
    return entityStore.getEntity(query).get();
  }

  private Entity retrieveEntityWithBlob(String environment, String entityId, String blobName) throws Exception {
    EntityQuery entityWithBlobQuery = new EntityQueryBuilder()
            .environment(environment)
            .entityId(entityId)
            .addBlobQueries(new BlobQueryBuilder()
                    .blobName(blobName)
                    .include(true)
                    .build())
            .build();
    return entityStore.getEntity(entityWithBlobQuery).get();
  }
}
