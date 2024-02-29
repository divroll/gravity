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

import com.divroll.datafactory.database.DatabaseManager;
import com.divroll.datafactory.database.DatabaseManagerProvider;
import com.divroll.datafactory.database.SerializableDatabaseManagerProvider;
import com.divroll.datafactory.database.impl.DatabaseManagerImpl;
import com.divroll.datafactory.exceptions.DataFactoryException;
import com.divroll.datafactory.indexers.LuceneIndexerProvider;
import com.divroll.datafactory.indexers.SerializableLuceneIndexerProvider;
import com.divroll.datafactory.repositories.EntityStore;
import com.divroll.datafactory.repositories.impl.EntityStoreImpl;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import jetbrains.exodus.bindings.ComparableBinding;

import java.lang.management.ManagementFactory;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

/**
 * DataFactory registers and exposes the repositories for accessing the underlying Xodus database.
 * Directly through {@linkplain DatabaseManager} or remotely using the {@linkplain EntityStore}
 * provided by both {@linkplain DataFactory} and {@linkplain DataFactoryClient}
 */
public final class DataFactory {
  /**
   * Logger for capturing events, errors, and informational messages within
   * the {@code DataFactory} class. Utilizes the {@link LoggerFactory} to create the
   * logger instance specific to this class.
   */
  private static final Logger LOG = LoggerFactory.getLogger(DataFactory.class);

  /**
   * The `entityStore` variable is an instance of the `EntityStore` interface.
   *
   * It provides methods to interact with the `EntityStore` service for saving,
   * retrieving, and removing entities and properties from the database. The `EntityStore`
   * interface extends the `Remote` interface, allowing it to be used for remote method
   * invocations.
   *
   */
  private EntityStore entityStore;

  /**
   * The DataFactory class is responsible for managing a database.
   */
  private static DataFactory instance;

  /**
   * Represents a registry.
   */
  private static Registry registry;

  /**
   * The `databaseManagerProvider` is a static constant variable that provides an implementation
   * of the `DatabaseManagerProvider` interface for retrieving instances of the `DatabaseManager`.
   * It is initialized with an instance of the `SerializableDatabaseManagerProvider` class.
   */
  private static final DatabaseManagerProvider DATABASE_MANAGER_PROVIDER
          = new SerializableDatabaseManagerProvider();

  /**
   * The LuceneIndexerProvider variable represents an instance of a provider
   * for the LuceneIndexer interface.
   */
  private static final LuceneIndexerProvider LUCENE_INDEXER_PROVIDER
          = new SerializableLuceneIndexerProvider();

  /**
   * The DataFactory class is a Singleton class that represents a factory for creating instances
   * of the DataFactory class. It ensures that only one instance of the DataFactory class
   * can be created.
   */
  private DataFactory() {
    if (instance != null) {
      throw new RuntimeException("Only one instance of DataFactory is allowed");
    }
  }

  /**
   * Returns the instance of the DataFactory class.
   *
   * @return the instance of the DataFactory class
   */
  public static DataFactory getInstance() {
    if (instance == null) {
      instance = new DataFactory();
    }
    return instance;
  }

  /**
   * Registers the DataFactory with the RMI registry.
   *
   * @throws RemoteException     if there is a communication-related error
   * @throws NotBoundException   if the registry could not be found
   */
  public void register() throws RemoteException, NotBoundException {
    String host = System.getProperty(Constants.JAVA_RMI_HOST_ENVIRONMENT, "localhost");
    String port = System.getProperty(Constants.JAVA_RMI_TEST_PORT_ENVIRONMENT,
            Constants.JAVA_RMI_PORT_DEFAULT);
    port = port != null ? port : System.getProperty(Constants.JAVA_RMI_PORT_ENVIRONMENT,
            Constants.JAVA_RMI_PORT_DEFAULT);

    if (registry == null) {
      registry = LocateRegistry.createRegistry(Integer.valueOf(port));
    }

    if (!Arrays.asList(registry.list()).contains(EntityStore.class.getName())) {
      entityStore = new EntityStoreImpl(DATABASE_MANAGER_PROVIDER, LUCENE_INDEXER_PROVIDER);
      registry.rebind(EntityStore.class.getName(), entityStore);
    }

    LOG.info("DataFactory initialized with process id: "
            + ManagementFactory.getRuntimeMXBean().getName());
  }

  /**
   * Releases all resources used by the DataFactory.
   * This method unbinds all registered remote objects from the registry and closes
   * the entityStore. After calling this method, the DataFactory will no longer be able to provide
   * access to these resources. If any remote exception occurs or if any of the resources cannot
   * be unbound or closed, a RuntimeException will be thrown with the appropriate error message.
   * The environments used by the DatabaseManagerImpl will also be closed.
   */
  public void release() {
    try {
      if (registry != null) {
        String[] classNames = registry.list();
        for (int i = 0; i < classNames.length; i++) {
          registry.unbind(classNames[i]);
        }
        if (entityStore != null) {
          UnicastRemoteObject.unexportObject(entityStore, true);
          entityStore = null;
        }
      }
    } catch (RemoteException e) {
      throw new RuntimeException(e);
    } catch (NotBoundException e) {
      throw new RuntimeException(e);
    } finally {
      DatabaseManagerImpl.getInstance().closeEnvironments();
    }
  }

  /**
   * Adds a custom property type to the data factory.
   *
   * @param dir the directory path where the custom binding files are located
   * @param clazz the class representing the custom property type
   * @param binding the binding instance for the custom property type
   * @param <T> the type of the custom property
   * @param <B> the type of the custom binding
   * @return the updated DataFactory instance
   */
  public <T extends Comparable, B extends ComparableBinding> DataFactory addCustomPropertyType(
      final String dir,
      final Class<T> clazz,
      final B binding) {
    DatabaseManagerImpl.getInstance().registerCustomPropertyType(dir, clazz, binding);
    return this;
  }

  /**
   * Retrieves an instance of the EntityStore service. If the service has not been initialized yet,
   * it will be created and registered with the RMI registry.
   *
   * @return the EntityStore service instance
   * @throws DataFactoryException if there is an error in the data factory
   * @throws RemoteException if a remote exception occurs during the RMI registry operations
   * @throws NotBoundException if the RMI registry is not bound
   */
  public EntityStore getEntityStore()
      throws DataFactoryException, RemoteException, NotBoundException {
    //Preconditions.checkNotNull(registry, "RMI registry should not be null");
    if (registry == null) {
      register();
    }
    if (entityStore == null) {
      entityStore =
          new EntityStoreImpl(DATABASE_MANAGER_PROVIDER, LUCENE_INDEXER_PROVIDER);
    }
    if (!Arrays.asList(registry.list()).contains(EntityStore.class.getName())) {
      registry.rebind(EntityStore.class.getName(), entityStore);
    }
    return entityStore;
  }

  /**
   * The main method starts the DataFactory application.
   *
   * @param args the command line arguments
   * @throws Exception if an error occurs during the execution
   */
  public static void main(final String[] args) throws Exception {
    LOG.info("Staring DataFactory");
    DataFactory dataFactory = getInstance();
    dataFactory.waitMethod();
  }

  /**
   * Waits indefinitely until a notify() or notifyAll() method is called on the object.
   * This method should be called inside a synchronized block.
   */
  private synchronized void waitMethod() {
    while (true) {
      try {
        this.wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
