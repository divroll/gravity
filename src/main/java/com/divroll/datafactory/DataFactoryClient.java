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
package com.divroll.datafactory;

import com.divroll.datafactory.repositories.EntityStore;
import com.divroll.datafactory.repositories.impl.EntityStoreClientImpl;

import java.util.Optional;

/**
 * The DataFactoryClient class represents a client for the DataFactory service.
 */
public class DataFactoryClient {
  /**
   * The DataFactoryClient class represents a client for the DataFactory service.
   */
  private static DataFactoryClient instance;
  /**
   * Represents the default host used by the DataFactoryClient class.
   * The default host is "localhost".
   */
  private static final String DEFAULT_HOST = "localhost";
  /**
   * Represents the default port used by the DataFactoryClient.
   */
  private static final String DEFAULT_PORT = "1099";
  /**
   * The `entityStore` variable stores an instance of the `EntityStore` interface.
   * The `EntityStore` interface provides methods to interact with the EntityStore service.
   */
  private EntityStore singleEntityStore;

  /**
   * The DataFactoryClient class represents a client for the EntityStore service.
   * It provides methods to interact with the entity store, such as saving entities,
   * getting entities, removing entities, and more.
   */
  private DataFactoryClient() {
  }

  /**
   * Represents a client for interacting with the EntityStore service.
   *
   * @param entityStore the EntityStore instance to use for interacting with the
   *                    EntityStore service.
   */
  public DataFactoryClient(final EntityStore entityStore) {
    this.singleEntityStore = entityStore;
  }

  /**
   * Returns an instance of the DataFactoryClient class.
   *
   * This method retrieves the host and port from the system properties and creates
   * a new instance of the DataFactoryClient class using the retrieved host and port.
   * If the host and port are not provided as system properties, default values are used instead.
   *
   * @return an instance of the DataFactoryClient class
   */
  public static DataFactoryClient getInstance() {
    String host = System.getProperty(Constants.JAVA_RMI_HOST_ENVIRONMENT);
    if (host == null) {
      host = System.setProperty(Constants.JAVA_RMI_HOST_ENVIRONMENT, DEFAULT_HOST);
    }
    String port =
        System.getProperty(Constants.JAVA_RMI_PORT_ENVIRONMENT, Constants.JAVA_RMI_PORT_DEFAULT);
    return getInstance(host, port);
  }

  /**
   * Returns an instance of DataFactoryClient.
   *
   * @param host the host name to connect to the EntityStore service.
   * @param port the port number to connect to the EntityStore service.
   * @return an instance of DataFactoryClient.
   */
  public static DataFactoryClient getInstance(final String host, final String port) {
    return new DataFactoryClient(new EntityStoreClientImpl(
            Optional.ofNullable(host).orElse(DEFAULT_HOST),
            Integer.valueOf(Optional.ofNullable(port).orElse(DEFAULT_PORT))
    ));
  }

  /**
   * Retrieves the EntityStore instance associated with the DataFactoryClient.
   *
   * This method returns the EntityStore instance that is used to interact with
   * the EntityStore service. You can use this instance to save, retrieve,
   * or remove entities and properties from the store.
   *
   * @return The EntityStore instance associated with the DataFactoryClient.
   */
  public EntityStore getEntityStore() {
    return singleEntityStore;
  }
}
