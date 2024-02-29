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
import com.divroll.datafactory.builders.Blob;
import com.divroll.datafactory.builders.BlobBuilder;
import com.divroll.datafactory.builders.Entity;
import com.divroll.datafactory.builders.EntityBuilder;
import com.divroll.datafactory.repositories.EntityStore;
import com.google.common.io.ByteSource;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class BlobRenameActionTest {
    private static final String ENTITY_TYPE = "User";
    protected static EntityStore entityStore;
    @Before
    public void setup() throws NotBoundException, RemoteException {
        this.entityStore = DataFactory.getInstance().getEntityStore();
    }

    @Test
    public void saveEntityWithBlob_WhenRenamedShouldNotExists()
            throws NotBoundException, IOException {
        String environment = TestEnvironment.getEnvironment();
        Entity createdEntity = createEntityWithBlob(environment);

        BlobRenameAction blobRenameAction = new BlobRenameActionBuilder()
                .blobName("photo")
                .newBlobName("profilePhoto")
                .build();

        Entity entityToUpdate = new EntityBuilder()
                .environment(environment)
                .entityId(createdEntity.entityId())
                .addActions(blobRenameAction)
                .build();

        Entity updatedEntity = entityStore.saveEntity(entityToUpdate).get();
        String[] blobNames = updatedEntity.blobNames();
        assertNotNull("Entity should not be null after save", updatedEntity);
        assertFalse("Blob should not exist after rename",
                Arrays.asList(blobNames).contains("photo"));
        assertTrue("Blob should exist after rename",
                Arrays.asList(blobNames).contains("profilePhoto"));
    }

    private Entity createEntityWithBlob(String environment)
            throws IOException, NotBoundException {
        InputStream mockImageStream = ByteSource.wrap("Mock Image".getBytes()).openStream();
        Blob blob = new BlobBuilder()
                .blobName("photo")
                .blobStream(new SimpleRemoteInputStream(mockImageStream))
                .build();
        Entity entity = new EntityBuilder()
                .environment(environment)
                .entityType(ENTITY_TYPE)
                .addBlobs(blob)
                .build();
        entity = entityStore.saveEntity(entity).get();
        return entity;
    }
}
