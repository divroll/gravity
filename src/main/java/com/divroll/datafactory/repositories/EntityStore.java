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
package com.divroll.datafactory.repositories;

import com.divroll.datafactory.builders.DataFactoryEntities;
import com.divroll.datafactory.builders.DataFactoryEntity;
import com.divroll.datafactory.builders.DataFactoryEntityTypes;
import com.divroll.datafactory.builders.DataFactoryProperty;
import com.divroll.datafactory.builders.queries.EntityQuery;
import com.divroll.datafactory.builders.queries.EntityTypeQuery;
import com.divroll.datafactory.exceptions.DataFactoryException;
import io.vavr.control.Option;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import org.jetbrains.annotations.NotNull;

/**
 * The EntityStore interface provides methods to interact with the EntityStore service.
 */
public interface EntityStore extends Remote {

  /**
   * Save an entity.
   *
   * @param entity
   * @return the saved entity
   * @throws DataFactoryException
   * @throws NotBoundException
   * @throws RemoteException
   */
  Option<DataFactoryEntity> saveEntity(@NotNull DataFactoryEntity entity)
      throws DataFactoryException, DataFactoryException, NotBoundException, RemoteException;

  /**
   * Save entities.
   *
   * @param entities
   * @return the saved entities
   * @throws DataFactoryException
   * @throws NotBoundException
   * @throws RemoteException
   */
  Option<DataFactoryEntities> saveEntities(@NotNull DataFactoryEntity[] entities)
      throws DataFactoryException, NotBoundException, RemoteException;

  /**
   * Get an entity.
   *
   * @param query
   * @return the entity
   * @throws DataFactoryException
   * @throws NotBoundException
   * @throws RemoteException
   */
  Option<DataFactoryEntity> getEntity(@NotNull EntityQuery query)
      throws DataFactoryException, NotBoundException, RemoteException;

  /**
   * Get entities.
   *
   * @param query
   * @return the entities
   * @throws DataFactoryException
   * @throws NotBoundException
   * @throws RemoteException
   */
  Option<DataFactoryEntities> getEntities(@NotNull EntityQuery query)
      throws DataFactoryException, NotBoundException, RemoteException;

  /**
   * Remove entities matching the query.
   *
   * @param query
   * @return true if the entity was removed, false otherwise
   * @throws NotBoundException
   * @throws RemoteException
   */
  Boolean removeEntity(@NotNull EntityQuery query)
      throws DataFactoryException, NotBoundException, RemoteException;

  /**
   * Remove entities matching the list of queries.
   *
   * @param queries
   * @return true if the entity was removed, false otherwise
   * @throws NotBoundException
   * @throws RemoteException
   */
  Boolean removeEntities(@NotNull EntityQuery[] queries)
      throws DataFactoryException, NotBoundException, RemoteException;

  /**
   * Save a property.
   *
   * @param property
   * @return true if the property was saved, false otherwise
   * @throws DataFactoryException
   * @throws NotBoundException
   * @throws RemoteException
   */
  Boolean saveProperty(@NotNull DataFactoryProperty property)
      throws DataFactoryException, NotBoundException, RemoteException;

  /**
   * Remove a property.
   *
   * @param property
   * @return true if the property was removed, false otherwise
   * @throws DataFactoryException
   * @throws NotBoundException
   * @throws RemoteException
   */
  Boolean removeProperty(@NotNull DataFactoryProperty property)
      throws DataFactoryException, NotBoundException, RemoteException;

  /**
   * Remove an entity type.
   *
   * @param query
   * @return true if the entity type was removed, false otherwise
   * @throws DataFactoryException
   * @throws NotBoundException
   * @throws RemoteException
   */
  Boolean removeEntityType(@NotNull EntityQuery query)
      throws DataFactoryException, NotBoundException, RemoteException;

  /**
   * Get entity.
   *
   * @param query
   * @return the entity types
   * @throws DataFactoryException
   * @throws NotBoundException
   * @throws RemoteException
   */
  Option<DataFactoryEntityTypes> getEntityTypes(EntityTypeQuery query)
      throws DataFactoryException, NotBoundException, RemoteException;
}
