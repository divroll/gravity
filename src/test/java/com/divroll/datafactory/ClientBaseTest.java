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

import com.divroll.datafactory.repositories.EntityStore;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public abstract class ClientBaseTest {
    protected static EntityStore entityStore;
    protected static DataFactory dataFactory;

    @BeforeClass
    public static void beforeAll() throws Exception {
        try {
            String host = TestEnvironment.getDomain();
            String port = TestEnvironment.getPort();
            startServer(host, port);
            entityStore = DataFactoryClient.getInstance(host, port).getEntityStore();
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void afterAll() {
        dataFactory.release();
    }

    public static void startServer(String host, String port)
            throws NotBoundException, RemoteException {
        // Emulate setting the environment variables
        System.setProperty(Constants.JAVA_RMI_TEST_PORT_ENVIRONMENT, port);
        dataFactory = DataFactory.getInstance();

        // Effectively "start" the RMI server
        dataFactory.register();
    }

    public static String getEnvironment() {
        return TestEnvironment.getEnvironment();
    }
}
