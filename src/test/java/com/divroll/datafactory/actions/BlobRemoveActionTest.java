/*
 * Divroll, Platform for Hosting Static Sites
 * Copyright 2024, Divroll, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.divroll.datafactory.actions;

import com.divroll.datafactory.BaseTest;
import com.divroll.datafactory.TestEnvironment;
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
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * This class provides test cases for the BlobRemoveAction class.
 */
public class BlobRemoveActionTest extends BaseTest {
    private static final String ENTITY_TYPE = "Room";
    private static final String ENVIRONMENT = TestEnvironment.getEnvironment();

    /**
     * Test case for the method testBlobRemove.
     * The method tests a scenario where an entity's blob is removed.
     * It creates a DataFactoryEntity with a blob, then removes the blob using the entityStore.
     * The method asserts that the blob is removed from the saved DataFactoryEntity.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testBlobRemove() throws Exception {
        // Create a blob we can test
        DataFactoryEntity dataFactoryEntity = createEntityWithBlob();

        String entityId = dataFactoryEntity.entityId();
        String entityType = dataFactoryEntity.entityType();
        String[] blobNames = dataFactoryEntity.blobNames();

        assertNotNull(entityId);
        assertNotNull(entityType);
        assertNotNull(blobNames);
        assertTrue(Arrays.asList(blobNames).contains("photo"));

        DataFactoryEntity singleEntityWithBlob = entityStore.getEntity(new EntityQueryBuilder()
                .environment(ENVIRONMENT)
                .entityId(dataFactoryEntity.entityId())
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
                .environment(ENVIRONMENT)
                .entityType(ENTITY_TYPE)
                .addBlobQueries(new BlobQueryBuilder().blobName("photo")
                        .include(true)
                        .build())
                .build();

        DataFactoryEntities entities = entityStore.getEntities(entityQuery).get();

        assertNotNull(entities);
        assertTrue(entities.count() == 1);
        assertNotNull(entities.entities());

        DataFactoryEntity entityWithBlob = entities.entities().stream().findFirst().get();
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
         DataFactoryEntities shouldBeEmpty = entityStore.getEntities(entityQuery).get();
         assertNotNull(shouldBeEmpty);
         assertTrue(shouldBeEmpty.count() == 0);
    }

    /**
     * Test case for the method testRemoveBlobWithAction.
     * The method tests a scenario where an entity's blob is replaced with a new blob effectively removing the old blob.
     * It creates a DataFactoryEntity with a blob, then creates a BlobRemoveAction with the blob name "photo".
     * The BlobRemoveAction is added as an action to the DataFactoryEntity.
     * A new blob named "newPhoto" is added to the DataFactoryEntity.
     * The DataFactoryEntity is saved using the entityStore.
     * The method asserts that the old blob "photo" is removed and the new blob "newPhoto" is present in the saved DataFactoryEntity.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testRemoveBlobWithAction() throws Exception {
        DataFactoryEntity dataFactoryEntity = createEntityWithBlob();
        assertNotNull(dataFactoryEntity.entityId());

        // Test a scenario where the entity will be replaced with a new blob effectively removing the old blob
        BlobRemoveAction blobRemoveAction = ImmutableBlobRemoveAction.builder()
                .addAllBlobNames(Arrays.asList("photo"))
                .build();

        dataFactoryEntity = new DataFactoryEntityBuilder()
                .environment(ENVIRONMENT)
                .entityType(ENTITY_TYPE)
                .entityId(dataFactoryEntity.entityId())
                .addActions(blobRemoveAction)
                .addBlobs(new DataFactoryBlobBuilder()
                        .blobName("newPhoto")
                        .blobStream(new SimpleRemoteInputStream(ByteSource.wrap("New Mock Image".getBytes()).openStream()))
                        .build())
                .build();
        String[] blobName = entityStore.saveEntity(dataFactoryEntity).get().blobNames();

        // Test if the blob was removed
        assertFalse(Arrays.asList(blobName).contains("photo"));
        assertTrue(Arrays.asList(blobName).contains("newPhoto"));
    }

    /**
     * Creates a DataFactoryEntity with a blob.
     *
     * @return The created DataFactoryEntity.
     * @throws IOException If an I/O error occurs.
     * @throws NotBoundException If the object being called is not bound to this registry.
     */
    private DataFactoryEntity createEntityWithBlob() throws IOException, NotBoundException {
        DataFactoryBlob blob = new DataFactoryBlobBuilder()
                .blobName("photo")
                .blobStream(new SimpleRemoteInputStream(ByteSource.wrap("Mock Image".getBytes()).openStream()))
                .build();
        DataFactoryEntity dataFactoryEntity = new DataFactoryEntityBuilder()
                .environment(ENVIRONMENT)
                .entityType(ENTITY_TYPE)
                .addBlobs(blob)
                .build();
        dataFactoryEntity = entityStore.saveEntity(dataFactoryEntity).get();
        return dataFactoryEntity;
    }

    /**
     * Retrieves the content of a blob with the specified name from the provided DataFactoryEntity.
     *
     * @param entityWithBlob The DataFactoryEntity that contains the blob.
     * @param blobName The name of the blob.
     * @return The content of the blob as a string, or null if the blob is not found.
     * @throws Exception if an error occurs while retrieving the blob content.
     */
    private String getBlobString(DataFactoryEntity entityWithBlob, String blobName) throws Exception {
        String blobString = null;
        for (DataFactoryBlob b : entityWithBlob.blobs()) {
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