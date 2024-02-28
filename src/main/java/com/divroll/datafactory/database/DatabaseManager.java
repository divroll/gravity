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
package com.divroll.datafactory.database;

import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.StoreTransactionalExecutable;
import jetbrains.exodus.env.ContextualEnvironment;
import jetbrains.exodus.env.Environment;
import jetbrains.exodus.lucene.ExodusDirectory;

/**
 * DatabaseManager is an interface that provides methods for managing a database.
 */
public interface DatabaseManager {

  /**
   * Retrieves the environment associated with the specified directory.
   *
   * @param dir the directory path
   * @param isContextual flag indicating whether the environment is contextual
   * @return the environment associated with the specified directory
   */
  Environment getEnvironment(String dir, boolean isContextual);

  /**
   * Retrieves a PersistentEntityStore instance.
   *
   * @param dir         the directory path of the entity store
   * @param isReadOnly  specifies if the entity store should be read-only
   * @return the PersistentEntityStore instance
   */
  PersistentEntityStore getPersistentEntityStore(String dir, boolean isReadOnly);
  /**
   * Retrieves or creates a persistent entity store.
   *
   * @param dir The directory where the persistent entity store is located.
   * @param storeName The name of the persistent entity store. If null, the default name will be used.
   * @param isReadOnly True if the entity store should be opened in read-only mode, false otherwise.
   * @return The persistent entity store.
   */
  PersistentEntityStore getPersistentEntityStore(String dir, String storeName, boolean isReadOnly);
  /**
   * Retrieves the ExodusDirectory associated with the given directory path.
   *
   * @param dir the directory path
   * @return the ExodusDirectory object
   */
  ExodusDirectory getExodusDirectory(String dir);

  /**
   * Executes a transaction on a persistent entity store.
   *
   * @param dir The directory of the entity store.
   * @param isReadOnly Indicates whether the transaction is read-only or not.
   * @param tx The transaction to be executed.
   */
  void transactPersistentEntityStore(String dir, boolean isReadOnly,
      StoreTransactionalExecutable tx);

  /**
   * Stores data in the given store using the provided transactional executable.
   *
   * @param dir The directory where the store is located.
   * @param store The name of the store.
   * @param tx The transactional executable to be executed.
   */
  void transactionContextualStore(String dir, String store, StoreTransactionalExecutable tx);

  /**
   * Closes the environment associated with the given directory.
   *
   * @param dir the directory of the environment to be closed
   */
  void closeEnvironment(String dir);

  /**
   * Closes all Xodus environments.
   */
  void closeEnvironments();
}
