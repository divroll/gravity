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
package com.divroll.datafactory.actions;

import com.divroll.datafactory.BaseTest;
import com.divroll.datafactory.builders.*;
import com.divroll.datafactory.builders.queries.BlobQueryBuilder;
import com.divroll.datafactory.builders.queries.EntityQuery;
import com.divroll.datafactory.builders.queries.EntityQueryBuilder;
import com.google.common.io.ByteSource;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * This class provides test cases for the BlobRemoveAction class.
 */
public class BlobRemoveActionTest extends BaseTest {
    private static final String ENTITY_TYPE = "Room";
    /**
     * Test case for the method testBlobRemove.
     * The method tests a scenario where an entity's blob is removed.
     * It creates a Entity with a blob, then removes the blob using the entityStore.
     * The method asserts that the blob is removed from the saved Entity.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testBlobRemove() throws Exception {
        String environment = getEnvironment();

        // Create a blob we can test
        Entity entity = createEntityWithBlob(environment);

        String entityId = entity.entityId();
        String entityType = entity.entityType();
        String[] blobNames = entity.blobNames();

        assertNotNull(entityId);
        assertNotNull(entityType);
        assertNotNull(blobNames);
        assertTrue(Arrays.asList(blobNames).contains("photo"));

        Entity singleEntityWithBlob = entityStore.getEntity(new EntityQueryBuilder()
                .environment(environment)
                .entityId(entity.entityId())
                .addBlobQueries(new BlobQueryBuilder()
                        .blobName("photo")
                        .include(true)
                        .build()).build()).get();

        assertNotNull(singleEntityWithBlob);
        assertNotNull(singleEntityWithBlob.entityId());
        assertNotNull(singleEntityWithBlob.entityType());
        assertNotNull(singleEntityWithBlob.blobNames());
        assertTrue(Arrays.asList(singleEntityWithBlob.blobNames()).contains("photo"));
        assertEquals("Mock Image", getBlobString(singleEntityWithBlob, "photo"));

        // Build the query that will be used for both checking the entity with blob and removing it later
        EntityQuery entityQuery = new EntityQueryBuilder()
                .environment(environment)
                .entityType(ENTITY_TYPE)
                .addBlobQueries(new BlobQueryBuilder().blobName("photo")
                        .include(true)
                        .build())
                .build();

        Entities entities = entityStore.getEntities(entityQuery).get();

        assertNotNull(entities);
        assertTrue(entities.count() == 1);
        assertNotNull(entities.entities());

        Entity entityWithBlob = entities.entities().stream().findFirst().get();
        assertNotNull(entityWithBlob);
        assertNotNull(entityWithBlob.entityId());
        assertNotNull(entityWithBlob.entityType());
        assertNotNull(entityWithBlob.blobNames());
        assertTrue(Arrays.asList(entityWithBlob.blobNames()).contains("photo"));
        assertEquals("Mock Image", getBlobString(entityWithBlob, "photo"));

        // Remove the blob
         Boolean wasRemoved = entityStore.removeEntity(entityQuery);
         assertTrue(wasRemoved);

         // Get the entity by blob name to test
         Entities shouldBeEmpty = entityStore.getEntities(entityQuery).get();
         assertNotNull(shouldBeEmpty);
         assertTrue(shouldBeEmpty.count() == 0);
    }

    /**
     * Test case for the method testRemoveBlobWithAction.
     * The method tests a scenario where an entity's blob is replaced with a new blob effectively removing the old blob.
     * It creates a Entity with a blob, then creates a BlobRemoveAction with the blob name "photo".
     * The BlobRemoveAction is added as an action to the Entity.
     * A new blob named "newPhoto" is added to the Entity.
     * The Entity is saved using the entityStore.
     * The method asserts that the old blob "photo" is removed and the new blob "newPhoto" is present in the saved Entity.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testRemoveBlobWithAction() throws Exception {
        String environment = getEnvironment();

        Entity entity = createEntityWithBlob(environment);
        assertNotNull(entity.entityId());

        // Test a scenario where the entity will be replaced with a new blob effectively removing the old blob
        BlobRemoveAction blobRemoveAction = ImmutableBlobRemoveAction.builder()
                .addAllBlobNames(Arrays.asList("photo"))
                .build();

        entity = new EntityBuilder()
                .environment(environment)
                .entityType(ENTITY_TYPE)
                .entityId(entity.entityId())
                .addActions(blobRemoveAction)
                .addBlobs(new BlobBuilder()
                        .blobName("newPhoto")
                        .blobStream(new SimpleRemoteInputStream(ByteSource.wrap("New Mock Image".getBytes()).openStream()))
                        .build())
                .build();
        String[] blobName = entityStore.saveEntity(entity).get().blobNames();

        // Test if the blob was removed
        assertFalse(Arrays.asList(blobName).contains("photo"));
        assertTrue(Arrays.asList(blobName).contains("newPhoto"));
    }

    @Test
    public void testFalseOnNonExistentBlobRemove() throws Exception {
        String environment = getEnvironment();

        Entity entity = createEntityWithBlob(environment);

        EntityQuery entityQuery = new EntityQueryBuilder()
                .environment(environment)
                .entityType(ENTITY_TYPE)
                .addBlobQueries(new BlobQueryBuilder().blobName("nonexistentBlob")
                        .include(true)
                        .build())
                .build();

        // Attempt to remove the non-existing blob
        boolean isRemoved = entityStore.removeEntity(entityQuery);
        assertFalse(isRemoved);
    }

    /**
     * Tests the scenario where multiple non-existing blobs are attempted to be removed at once.
     * The method creates an entity with multiple blobs, then creates a query with multiple non-existing blob names.
     * It then tries to remove the non-existing blobs using the entityStore.removeEntity() method.
     * The method asserts that the removal of non-existent blobs was unsuccessful.
     * It also asserts that the entity with multiple blobs is still intact and was not affected by the removal attempt.
     *
     * @throws IOException       If an I/O error occurs.
     * @throws NotBoundException If the object being called is not bound to this registry.
     */
    @Test
    public void testFalseWhenRemovingMultipleNonExistingBlobsAtOnce() throws IOException, NotBoundException {
        String environment = getEnvironment();

        // Create an entity with multiple blobs
        Entity entity = createEntityWithMultipleBlobs(environment);
        assertNotNull(entity.entityId());

        // Create a query with multiple non-existing blob names
        EntityQuery entityQuery = new EntityQueryBuilder()
                .environment(environment)
                .entityType(ENTITY_TYPE)
                .addBlobQueries(
                        new BlobQueryBuilder().blobName("nonexistentBlob1").include(true).build(),
                        new BlobQueryBuilder().blobName("nonexistentBlob2").include(true).build(),
                        new BlobQueryBuilder().blobName("nonexistentBlob3").include(true).build())
                .build();

        // Try to remove non-existing blobs at once
        boolean isRemoved = entityStore.removeEntity(entityQuery);

        // Assert that the removal of non-existent blobs was unsuccessful
        assertFalse(isRemoved);

        // Build a query to fetch the entity we created earlier
        EntityQuery existingBlobsQuery = new EntityQueryBuilder()
                .environment(environment)
                .entityType(ENTITY_TYPE)
                .addBlobQueries(
                        new BlobQueryBuilder().blobName("photo1").include(true).build(),
                        new BlobQueryBuilder().blobName("photo2").include(true).build(),
                        new BlobQueryBuilder().blobName("photo3").include(true).build())
                .build();

        // Get the entity by blob names to test
        Entities shouldNotBeEmpty = entityStore.getEntities(existingBlobsQuery).get();

        // Assert that the entity with multiple blobs is still intact and was not affected
        assertNotNull(shouldNotBeEmpty);
        assertTrue(shouldNotBeEmpty.count() == 1);
        assertEquals(shouldNotBeEmpty.entities().stream().findFirst().get().entityId(), entity.entityId());
    }

    /**
     * This method tests the removal of multiple blobs from a Entity at once.
     * It creates an entity with multiple blobs and then removes those blobs using the entityStore.
     * The method asserts that the blobs were successfully removed from the entity.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testRemoveMultipleBlobsAtOnce() throws Exception {
        String environment = getEnvironment();

        // Create an entity with multiple blobs
        Entity entity = createEntityWithMultipleBlobs(environment);

        // Build the query that will be used for both checking the entity with blobs and removing them later
        EntityQuery entityQuery = new EntityQueryBuilder()
                .environment(environment)
                .entityType(ENTITY_TYPE)
                .addBlobQueries(
                        new BlobQueryBuilder().blobName("photo1").include(true).build(),
                        new BlobQueryBuilder().blobName("photo2").include(true).build(),
                        new BlobQueryBuilder().blobName("photo3").include(true).build())
                .build();

        // Remove the blobs
        Boolean wereRemoved = entityStore.removeEntity(entityQuery);
        assertTrue(wereRemoved);

        // Get the entity by blob names to test
        Entities shouldBeEmpty = entityStore.getEntities(entityQuery).get();
        assertNotNull(shouldBeEmpty);
        assertTrue(shouldBeEmpty.count() == 0);
    }

    /**
     * Test case for removing a blob from an empty Entity.
     * It creates an empty Entity and attempts to remove a non-existing blob.
     * The method asserts that the removal is not successful.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testRemoveBlobFromEmptyEntity() throws Exception {
        String environment = getEnvironment();

        Entity emptyEntity = new EntityBuilder()
                .environment(environment)
                .entityType(ENTITY_TYPE)
                .build();

        EntityQuery entityQuery = new EntityQueryBuilder()
                .environment(environment)
                .entityType(ENTITY_TYPE)
                .addBlobQueries(new BlobQueryBuilder().blobName("nonexistentBlob")
                        .include(true)
                        .build())
                .build();

        // Attempt to remove the non-existing blob from the empty entity
        boolean isRemoved = entityStore.removeEntity(entityQuery);
        assertFalse(isRemoved);
    }

    /**
     * Test case to verify that the method returns false when attempting to remove a blob from a non-existing entity.
     *
     * @throws NotBoundException if the object being called is not bound to the registry
     * @throws RemoteException if a remote exception occurs during the test
     */
    @Test
    public void testFalseWhenRemovingBlobFromNonExistingEntity() throws NotBoundException, RemoteException {
        String environment = getEnvironment();

        // Build the query for entity with a blob
        EntityQuery entityQuery = new EntityQueryBuilder()
                .environment(environment)
                .entityType(ENTITY_TYPE)
                .addBlobQueries(new BlobQueryBuilder().blobName("photo")
                        .include(true)
                        .build())
                .build();

        // Try to remove the blob from non-existing entity
        boolean isRemoved = entityStore.removeEntity(entityQuery);

        // Assert
        assertFalse(isRemoved);
    }

    /**
     * Creates a Entity with multiple blobs.
     *
     * @return The created Entity.
     * @throws IOException If an I/O error occurs.
     * @throws NotBoundException If the object being called is not bound to this registry.
     */
    private Entity createEntityWithMultipleBlobs(String environment) throws IOException, NotBoundException {
        Blob blob1 = new BlobBuilder()
                .blobName("photo1")
                .blobStream(new SimpleRemoteInputStream(ByteSource.wrap("Mock Image 1".getBytes()).openStream()))
                .build();
        Blob blob2 = new BlobBuilder()
                .blobName("photo2")
                .blobStream(new SimpleRemoteInputStream(ByteSource.wrap("Mock Image 2".getBytes()).openStream()))
                .build();
        Blob blob3 = new BlobBuilder()
                .blobName("photo3")
                .blobStream(new SimpleRemoteInputStream(ByteSource.wrap("Mock Image 3".getBytes()).openStream()))
                .build();
        Entity entity = new EntityBuilder()
                .environment(environment)
                .entityType(ENTITY_TYPE)
                .addBlobs(blob1, blob2, blob3)
                .build();
        entity = entityStore.saveEntity(entity).get();
        return entity;
    }

    /**
     * Creates a Entity with a blob.
     *
     * @return The created Entity.
     * @throws IOException If an I/O error occurs.
     * @throws NotBoundException If the object being called is not bound to this registry.
     */
    private Entity createEntityWithBlob(String environment) throws IOException, NotBoundException {
        Blob blob = new BlobBuilder()
                .blobName("photo")
                .blobStream(new SimpleRemoteInputStream(ByteSource.wrap("Mock Image".getBytes()).openStream()))
                .build();
        Entity entity = new EntityBuilder()
                .environment(environment)
                .entityType(ENTITY_TYPE)
                .addBlobs(blob)
                .build();
        entity = entityStore.saveEntity(entity).get();
        return entity;
    }

    /**
     * Retrieves the content of a blob with the specified name from the provided Entity.
     *
     * @param entityWithBlob The Entity that contains the blob.
     * @param blobName The name of the blob.
     * @return The content of the blob as a string, or null if the blob is not found.
     * @throws Exception if an error occurs while retrieving the blob content.
     */
    private String getBlobString(Entity entityWithBlob, String blobName) throws Exception {
        String blobString = null;
        for (Blob b : entityWithBlob.blobs()) {
            if (b.blobName().equals(blobName)) {
                RemoteInputStream remoteInputStream = b.blobStream();
                blobString =
                        IOUtils.toString(RemoteInputStreamClient.wrap(remoteInputStream),
                                Charset.defaultCharset());
                break;
            }
        }
        return blobString;
    }
}