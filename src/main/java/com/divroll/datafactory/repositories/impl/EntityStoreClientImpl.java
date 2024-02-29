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
package com.divroll.datafactory.repositories.impl;

import com.divroll.datafactory.builders.Entities;
import com.divroll.datafactory.builders.Entity;
import com.divroll.datafactory.builders.EntityTypes;
import com.divroll.datafactory.builders.Property;
import com.divroll.datafactory.builders.queries.EntityQuery;
import com.divroll.datafactory.builders.queries.EntityTypeQuery;
import com.divroll.datafactory.exceptions.DataFactoryException;
import com.divroll.datafactory.repositories.EntityStore;
import io.vavr.control.Option;
import org.jetbrains.annotations.NotNull;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * The EntityStoreClientImpl class is an implementation of the EntityStoreClient interface.
 * It provides methods to interact with the EntityStore through remote procedure calls.
 */
public class EntityStoreClientImpl implements EntityStore {
  /**
   * The port used to establish a connection to the EntityStore service.
   *
   * This variable is used in the EntityStoreClientImpl class to specify the port number
   * when creating a client object to connect to the EntityStore service.
   *
   * The port is an Integer type and is private, meaning it can only be accessed within the
   * EntityStoreClientImpl class.
   *
   * Example usage:
   * EntityStoreClientImpl client = new EntityStoreClientImpl("localhost", port);
   */
  private Integer port;
  /**
   *
   */
  private String host;

  /**
   * The EntityStoreClientImpl class is an implementation of the EntityStoreClient interface.
   * It provides methods to interact with the EntityStore through remote procedure calls.
   *
   * This class has a private constructor to prevent instantiation from outside the class.
   *
   * @see EntityStoreClient
   */
  private EntityStoreClientImpl() {
    // Prevent instantiation
  }

  /**
   * Initializes a new instance of the EntityStoreClientImpl class.
   *
   * @param host The host address of the entity store.
   * @param port The port number of the entity store.
   */
  public EntityStoreClientImpl(final String host, final Integer port) {
    this.host = host;
    this.port = port;
  }

  /**
   * Saves the given Entity.
   *
   * @param entity The Entity to be saved. Cannot be null.
   * @return An Option containing the saved Entity, or None if the save operation
   * failed or the entity is invalid.
   * @throws DataFactoryException if there is an error in the data factory.
   * @throws NotBoundException if the EntityStore is not bound.
   * @throws RemoteException if a communication-related error occurs.
   */
  @Override public Option<Entity> saveEntity(@NotNull final Entity entity)
      throws DataFactoryException, NotBoundException, RemoteException {
    return getEntityStore().saveEntity(entity);
  }

  /**
   * Saves the given array of Entity objects.
   *
   * @param entities an array of Entity objects to be saved
   * @return an Option containing the saved Entities or None if an error occurs
   * @throws DataFactoryException if there is an error in the data factory system
   * @throws NotBoundException if the data factory is not bound
   * @throws RemoteException if a remote communication error occurs
   */
  @Override
  public Option<Entities> saveEntities(@NotNull final Entity[] entities)
      throws DataFactoryException, NotBoundException, RemoteException {
    return getEntityStore().saveEntities(entities);
  }

  /**
   * Retrieves an entity from the data factory system based on the given query.
   *
   * @param query The entity query used to retrieve the entity. Cannot be null.
   * @return An {@code Option<Entity>} object representing the entity if found,
   * otherwise an empty {@code Option}.
   * @throws DataFactoryException If there is an error retrieving the entity from
   * the data factory system.
   * @throws NotBoundException If the entity is not bound in the data factory system.
   * @throws RemoteException If a remote exception occurs during the retrieval of the entity.
   */
  @Override public Option<Entity> getEntity(@NotNull final EntityQuery query)
      throws DataFactoryException, NotBoundException, RemoteException {
    return getEntityStore().getEntity(query);
  }

  /**
   * Retrieves a list of entities based on the provided query.
   *
   * @param query the query object specifying the criteria for the entities to retrieve
   * @return an Option object containing the list of entities that match the query
   * @throws DataFactoryException if there is an error in the data factory
   * @throws NotBoundException if the entity store is not bound
   * @throws RemoteException if a remote exception occurs during the operation
   */
  @Override public Option<Entities> getEntities(@NotNull final EntityQuery query)
      throws DataFactoryException, NotBoundException, RemoteException {
    return getEntityStore().getEntities(query);
  }

  /**
   * Remove entities matching the query.
   *
   * @param query The entity query specifying which entities to remove
   * @return true if the entities were successfully removed, false otherwise
   * @throws DataFactoryException   if there is an error in the data factory
   * @throws NotBoundException     if the entity store is not bound
   * @throws RemoteException      if a communication error occurs during the method invocation
   */
  @Override public Boolean removeEntity(@NotNull final EntityQuery query)
      throws DataFactoryException, NotBoundException, RemoteException {
    return getEntityStore().removeEntity(query);
  }

  /**
   * Remove entities matching the list of queries.
   *
   * @param queries The array of EntityQuery objects representing the queries to remove entities.
   * @return True if the entities were successfully removed, false otherwise.
   * @throws DataFactoryException  If there is an error in the data factory.
   * @throws NotBoundException    If the entity store is not bound.
   * @throws RemoteException     If a remote exception occurs during the removal process.
   */
  @Override public Boolean removeEntities(@NotNull final EntityQuery[] queries)
      throws DataFactoryException, NotBoundException, RemoteException {
    return getEntityStore().removeEntities(queries);
  }

  /**
   * Saves a Property to the entity store.
   *
   * @param property The Property to be saved.
   * @return true if the property was saved successfully, false otherwise.
   * @throws DataFactoryException If there is an error in the data factory.
   * @throws NotBoundException If the entity store is not bound.
   * @throws RemoteException If a remote exception occurs.
   */
  @Override public Boolean saveProperty(@NotNull final Property property)
      throws DataFactoryException, NotBoundException, RemoteException {
    return getEntityStore().saveProperty(property);
  }

  /**
   * Removes a Property from the entity store.
   *
   * @param property The Property to be removed. Cannot be null.
   * @return true if the property was removed successfully, false otherwise.
   * @throws DataFactoryException If there is an error in the data factory.
   * @throws NotBoundException If the entity store is not bound.
   * @throws RemoteException If a remote exception occurs during the removal process.
   */
  @Override public Boolean removeProperty(@NotNull final Property property)
      throws DataFactoryException, NotBoundException, RemoteException {
    return getEntityStore().removeProperty(property);
  }

  /**
   * Removes entities of a specific entity type matching the given query.
   *
   * @param query The EntityQuery used to specify the entities to be removed.
   * @return true if the entities were successfully removed, false otherwise.
   * @throws DataFactoryException if there is an error in the data factory.
   * @throws NotBoundException if the entity store is not bound.
   * @throws RemoteException if there is a remote communication error.
   */
  @Override public Boolean removeEntityType(@NotNull final EntityQuery query)
      throws DataFactoryException, NotBoundException, RemoteException {
    return getEntityStore().removeEntityType(query);
  }

  /**
   * Retrieves the entity types based on the given query.
   *
   * @param query The EntityTypeQuery object specifying the criteria for the entity types
   *             to retrieve. Cannot be null.
   * @return An Option<EntityTypes> representing the retrieved entity types,
   * or None if no entity types match the query.
   * @throws DataFactoryException If there is an error in the data factory.
   * @throws NotBoundException If the entity store is not bound.
   * @throws RemoteException If a remote exception occurs during the retrieval process.
   */
  @Override public Option<EntityTypes> getEntityTypes(final EntityTypeQuery query)
      throws DataFactoryException, NotBoundException, RemoteException {
      return getEntityStore().getEntityTypes(query);
  }

  /**
   * Retrieves the instance of the EntityStore from the remote server.
   * This method establishes a connection with the registry server using the provided
   * host and port.
   *
   * Then, it performs a lookup for the EntityStore implementation registered in the registry.
   *
   * @throws RemoteException if there is an issue with the remote communication.
   * @throws NotBoundException if the EntityStore is not found in the registry.
   *
   * @return the instance of the EntityStore retrieved from the remote server.
   */
  private EntityStore getEntityStore() throws RemoteException, NotBoundException {
    Registry registry = LocateRegistry.getRegistry(host, port);
    EntityStore entityStore =
            (EntityStore) registry.lookup(EntityStore.class.getName());
    return entityStore;
  }
}
