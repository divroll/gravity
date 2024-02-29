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
package com.divroll.datafactory.database.impl;

import com.divroll.datafactory.GeoPoint;
import com.divroll.datafactory.LocalTimeRange;
import com.divroll.datafactory.bindings.EmbeddedEntityBinding;
import com.divroll.datafactory.bindings.GeoPointBinding;
import com.divroll.datafactory.bindings.LocalTimeBinding;
import com.divroll.datafactory.bindings.LocalTimeRangeBinding;
import com.divroll.datafactory.database.DatabaseManager;
import com.divroll.datafactory.exceptions.ThrowingConsumer;
import com.divroll.datafactory.properties.EmbeddedArrayIterable;
import com.divroll.datafactory.properties.EmbeddedEntityIterable;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import jetbrains.exodus.bindings.ComparableBinding;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStoreConfig;
import jetbrains.exodus.entitystore.PersistentEntityStores;
import jetbrains.exodus.entitystore.StoreTransactionalExecutable;
import jetbrains.exodus.env.ContextualEnvironment;
import jetbrains.exodus.env.Environment;
import jetbrains.exodus.env.Environments;
import jetbrains.exodus.env.EnvironmentConfig;
import jetbrains.exodus.env.StoreConfig;
import jetbrains.exodus.env.ContextualStore;
import jetbrains.exodus.lucene.ExodusDirectory;
import jetbrains.exodus.lucene.ExodusDirectoryConfig;
import jetbrains.exodus.vfs.VfsConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deletes the persistent entity store associated with the given directory.
 */
public final class DatabaseManagerImpl implements DatabaseManager {

  /**
   * Logger for capturing events, errors, and informational messages within
   * the {@code DatabaseManagerImpl} class. Utilizes the {@link LoggerFactory} to create the
   * logger instance specific to this class.
   */
  private static final Logger LOG = LoggerFactory.getLogger(DatabaseManagerImpl.class);
  /**
   * The default lock timeout value, in milliseconds, for managing a database.
   */
  private static final int DEFAULT_LOCK_TIMEOUT = 30000;
  /**
   * The default name of a persistent entity store.
   * This constant is used in
   * {@link DatabaseManager#getPersistentEntityStore(String, String, boolean)} method
   * to retrieve or create a persistent entity store with the default name when a specific name
   * is not provided.
   */
  private static final String DEFAULT_ENTITYSTORE_NAME = "persistentEntityStore";

  /**
   * contextualEnvironmentMap is a Map that stores the contextual environments associated with
   * their directory paths.
   *
   * The key in the map is the directory path.
   * The value in the map is an instance of ContextualEnvironment.
   *
   * ContextualEnvironment is an object that represents a contextual environment associated
   * with a directory. It provides methods for managing the environmental properties
   * and configurations.
   *
   * Use the contextualEnvironmentMap to retrieve a specific ContextualEnvironment instance
   * using its directory path. This map is used by the DatabaseManagerImpl class to manage and
   * access the contextual environments.
   *
   * Example usage:
   *
   * Map<String, ContextualEnvironment> contextualEnvironmentMap
   *  = DatabaseManagerImpl.contextualEnvironmentMap;
   * ContextualEnvironment environment = contextualEnvironmentMap.get("/path/to/environment");
   *
   * // Access properties and methods of the ContextualEnvironment instance
   * environment.setConfig(config);
   * environment.start();
   *
   * // Iterate over the contextualEnvironmentMap
   * for (Map.Entry<String, ContextualEnvironment> entry : contextualEnvironmentMap.entrySet()) {
   *     String dir = entry.getKey();
   *     ContextualEnvironment env = entry.getValue();
   *     // Perform operations on the ContextualEnvironment instance
   * }
   */
  private Map<String, ContextualEnvironment> contextualEnvironmentMap;
  /**
   * A map that stores the association between directory paths and ExodusDirectory objects.
   */
  private Map<String, ExodusDirectory> exodusDirectoryMap;
  /**
   * environmentMap is a map that stores the association between directory paths
   * and Environment objects.
   */
  private Map<String, Environment> environmentMap;
  /**
   * A map that stores entity stores associated with directory paths.
   */
  private Map<String, PersistentEntityStore> entityStoreMap;

  /**
   * This is the private static instance variable of the class DatabaseManagerImpl.
   *
   * <p>
   * This variable holds the singleton instance of the DatabaseManagerImpl class.
   * </p>
   *
   * @see DatabaseManagerImpl
   */
  private static DatabaseManagerImpl instance;

  /**
   * DatabaseManagerImpl is a class that implements the DatabaseManager interface.
   * It provides methods for managing a database.
   */
  private DatabaseManagerImpl() {
    if (instance != null) {
      throw new RuntimeException("Only one instance of DatabaseManager is allowed");
    }
    entityStoreMap = new HashMap<>();
    environmentMap = new HashMap<>();
    contextualEnvironmentMap = new HashMap<>();
    exodusDirectoryMap = new HashMap<>();
  }

  /**
   * Returns the singleton instance of the DatabaseManagerImpl class.
   *
   * @return The singleton instance of the DatabaseManagerImpl class.
   */
  public static DatabaseManagerImpl getInstance() {
    if (instance == null) {
      instance = new DatabaseManagerImpl();
    }
    return instance;
  }

  /**
   * Retrieves the environment for the given directory.
   *
   * @param dir The directory path for which the environment needs to be retrieved.
   * @param isContextual A boolean flag indicating whether the environment is contextual or not.
   * @return The environment corresponding to the given directory.
   */
  @Override
  public Environment getEnvironment(final String dir, final boolean isContextual) {
    Environment environment = environmentMap.get(dir);
    if (environment == null) {
      EnvironmentConfig config = new EnvironmentConfig();
      config.setLogCacheShared(false);
      config.setEnvCloseForcedly(true);
      config.setManagementEnabled(false);
      config.setLogLockTimeout(DEFAULT_LOCK_TIMEOUT);
      environment = deleteLockingProcessAndGetEnvironment(dir, config, isContextual);
      environmentMap.put(dir, environment);
    }
    return environment;
  }

  /**
   * Retrieves a persistent entity store based on the given directory and read-only flag.
   *
   * @param dir The directory where the persistent entity store is located.
   * @param isReadOnly A flag indicating whether the persistent entity store should be read-only.
   *
   * @return The persistent entity store instance.
   */
  @Override
  public PersistentEntityStore getPersistentEntityStore(final String dir,
                                                        final boolean isReadOnly) {
    return getPersistentEntityStore(dir, DEFAULT_ENTITYSTORE_NAME, isReadOnly);
  }

  /**
   * Retrieves or creates a PersistentEntityStore given a directory, store name, and read-only flag.
   *
   * @param dir        The directory where the PersistentEntityStore is located.
   * @param storeName  The name of the store. If null, a default store name is used.
   * @param isReadOnly A flag indicating whether the store should be opened in read-only mode.
   * @return The retrieved or created PersistentEntityStore.
   */
  @Override public PersistentEntityStore getPersistentEntityStore(final String dir,
                                                                  final String storeName,
                                                                  final boolean isReadOnly) {
    PersistentEntityStore entityStore = entityStoreMap.get(dir);
    if (entityStore == null) {
      Environment environment = getEnvironment(dir, false);
      final PersistentEntityStoreConfig config = new PersistentEntityStoreConfig()
          .setRefactoringHeavyLinks(true)
          .setDebugSearchForIncomingLinksOnDelete(true)
          .setManagementEnabled(false);
      entityStore =
          PersistentEntityStores.newInstance(config, environment,
              storeName != null ? storeName : DEFAULT_ENTITYSTORE_NAME);
      entityStore.executeInTransaction(
          txn -> {
            ((PersistentEntityStore) txn.getStore()).registerCustomPropertyType(
                txn, EmbeddedEntityIterable.class, EmbeddedEntityBinding.BINDING);
            ((PersistentEntityStore) txn.getStore()).registerCustomPropertyType(
                txn, EmbeddedArrayIterable.class, EmbeddedEntityBinding.BINDING);
            ((PersistentEntityStore) txn.getStore()).registerCustomPropertyType(
                txn, LocalTime.class, LocalTimeBinding.BINDING);
            ((PersistentEntityStore) txn.getStore()).registerCustomPropertyType(
                txn, GeoPoint.class, GeoPointBinding.BINDING);
            ((PersistentEntityStore) txn.getStore()).registerCustomPropertyType(
                txn, LocalTimeRange.class, LocalTimeRangeBinding.BINDING);
            entityStoreMap.put(dir,
                ((PersistentEntityStore) txn.getStore()));
          });
    }
    return entityStore;
  }

  /**
   * Retrieves the ExodusDirectory for the specified directory.
   *
   * @param dir the directory for which to retrieve the ExodusDirectory
   * @return the ExodusDirectory associated with the specified directory
   */
  @Override public ExodusDirectory getExodusDirectory(final String dir) {
    ContextualEnvironment env = (ContextualEnvironment) getEnvironment(dir, true);
    ExodusDirectory exodusDirectory = exodusDirectoryMap.get(dir);
    if (exodusDirectory == null) {
      exodusDirectory = new ExodusDirectory(env, VfsConfig.DEFAULT,
              StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING, new ExodusDirectoryConfig());
      exodusDirectoryMap.put(dir, exodusDirectory);
    }
    return exodusDirectoryMap.get(dir);
  }

  /**
   * Executes a transaction on a persistent entity store.
   *
   * @param dir The directory where the persistent entity store is located.
   * @param isReadOnly A flag indicating whether the persistent entity store should be read-only.
   * @param storeTransaction The transaction to be executed.
   */
  @Override
  public void transactPersistentEntityStore(final String dir,
                                            final boolean isReadOnly,
                                            final StoreTransactionalExecutable storeTransaction) {
    final PersistentEntityStore entityStore =
        getPersistentEntityStore(dir, isReadOnly);
    entityStore.executeInTransaction(storeTransaction);
  }

  /**
   * Stores data in the given store using the provided transactional executable.
   *
   * @param dir The directory where the store is located.
   * @param store The name of the store.
   * @param tx The transactional executable to be executed.
   */
  @Override public void transactionContextualStore(final String dir,
                                                   final String store,
                                                   final StoreTransactionalExecutable tx) {
    final ContextualEnvironment environment
            = (ContextualEnvironment) getEnvironment(dir, true);
    ContextualStore contextualStore = environment.openStore(store, StoreConfig.WITHOUT_DUPLICATES);
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * Closes the environment associated with the given directory.
   *
   * @param dir the directory of the environment to be closed
   */
  @Override public void closeEnvironment(final String dir) {
    PersistentEntityStore entityStore = entityStoreMap.get(dir);
    entityStore.getEnvironment().close();
    entityStore.close();
  }

  /**
   * Closes all Xodus environments.
   */
  @Override public void closeEnvironments() {
    entityStoreMap.forEach((dir, entityStore) -> {
      entityStore.getEnvironment().close();
      entityStore.close();
    });
  }

  /**
   * Deletes the locking process and retrieves the associated environment.
   *
   * @param dir the directory path
   * @param config the environment configuration
   * @param isContextual flag indicating whether the environment is contextual
   * @return the retrieved Environment instance
   */
  private Environment deleteLockingProcessAndGetEnvironment(final String dir,
                                                            final EnvironmentConfig config,
                                                            final boolean isContextual) {
    Environment env = null;
    try {
      env = isContextual
              ? Environments.newContextualInstance(dir, config)
              : Environments.newInstance(dir, config);
    } catch (Exception e) {
      if (e.getMessage().contains("Can't acquire environment lock")) {
        try {
          String content = new Scanner(new File(dir)).useDelimiter("\\Z").next();
          String processId = parseProcessId(content);
          boolean isWindows = System.getProperty("os.name")
              .toLowerCase().startsWith("windows");
          String cmd = isWindows ? "taskkill /F /PID " + processId : "kill -9 " + processId;
          Runtime.getRuntime().exec(cmd);
          // Try again
          env = isContextual
                  ? Environments.newContextualInstance(dir, config)
                  : Environments.newInstance(dir, config);
        } catch (FileNotFoundException ex) {
          LOG.error("Lock file not found");
        } catch (IOException ex) {
          LOG.error("Lock file access error");
        }
      }
    }
    return env;
  }

  /**
   * Registers a custom property type for the specified directory.
   *
   * @param dir     the directory for which to register the custom property type
   * @param clazz   the class of property values extending Comparable
   * @param binding the ComparableBinding for the custom property type
   * @param <T>     the class of the property type
   * @param <B>     the binding type
   */
  public <T extends Comparable, B extends ComparableBinding> void registerCustomPropertyType(
      final String dir,
      final Class<T> clazz,
      final B binding) {
    getPersistentEntityStore(dir, false).executeInTransaction(txn -> {
      ((PersistentEntityStore) txn.getStore()).registerCustomPropertyType(txn, clazz, binding);
    });
  }

  /**
   * Parses the process ID from the given text.
   *
   * @param text The input text to be parsed.
   * @return The extracted process ID, or null if no match is found.
   */
  public static String parseProcessId(final String text) {
    Pattern pattern = Pattern.compile("^Private property of Exodus:\\s(\\d+)");
    Matcher matcher = pattern.matcher(text);
    if (matcher.matches()) {
      return matcher.group(1);
    }
    return null;
  }

  /**
   * Wraps a throwing consumer with exception handling.
   *
   * @param <T>    the type of the input to the consumer
   * @param <E>    the type of the exception that can be thrown
   * @param consumer   the throwing consumer to wrap
   * @param clazz  the class representing the expected exception type
   * @return a consumer that handles exceptions thrown by the input consumer
   */
  public static <T, E extends Exception> Consumer<T> consumerWrapper(
          final ThrowingConsumer<T> consumer,
          final Class<E> clazz) {
    return i -> {
      try {
        consumer.accept(i);
      } catch (Exception ex) {
        ex.printStackTrace();
        try {
          E exCast = clazz.cast(ex);
          System.err.println(
              "Exception occurred : " + exCast.getMessage());
        } catch (ClassCastException ccEx) {
          //throw ex;
        }
      }
    };
  }
}
