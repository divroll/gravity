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

import com.divroll.datafactory.Constants;
import com.divroll.datafactory.Marshaller;
import com.divroll.datafactory.Unmarshaller;
import com.divroll.datafactory.actions.PropertyRemoveAction;
import com.divroll.datafactory.actions.PropertyRenameAction;
import com.divroll.datafactory.builders.DataFactoryEntities;
import com.divroll.datafactory.builders.DataFactoryEntitiesBuilder;
import com.divroll.datafactory.builders.DataFactoryEntity;
import com.divroll.datafactory.builders.DataFactoryEntityType;
import com.divroll.datafactory.builders.DataFactoryEntityTypeBuilder;
import com.divroll.datafactory.builders.DataFactoryEntityTypes;
import com.divroll.datafactory.builders.DataFactoryEntityTypesBuilder;
import com.divroll.datafactory.builders.DataFactoryProperty;
import com.divroll.datafactory.builders.queries.BlobQuery;
import com.divroll.datafactory.builders.queries.EntityQuery;
import com.divroll.datafactory.builders.queries.EntityTypeQuery;
import com.divroll.datafactory.builders.queries.LinkQuery;
import com.divroll.datafactory.conditions.UnsatisfiedCondition;
import com.divroll.datafactory.database.DatabaseManager;
import com.divroll.datafactory.database.DatabaseManagerProvider;
import com.divroll.datafactory.exceptions.DataFactoryException;
import com.divroll.datafactory.indexers.LuceneIndexer;
import com.divroll.datafactory.indexers.LuceneIndexerProvider;
import com.divroll.datafactory.properties.EmbeddedArrayIterable;
import com.divroll.datafactory.properties.EmbeddedEntityIterable;
import com.divroll.datafactory.repositories.EntityStore;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.common.collect.FluentIterable;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import io.vavr.control.Option;
import io.vavr.control.Try;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.PersistentEntityStoreImpl;
import jetbrains.exodus.entitystore.PersistentStoreTransaction;
import org.jetbrains.annotations.NotNull;
import util.ComparableHashMap;

import static com.divroll.datafactory.Unmarshaller.processActions;
import static com.divroll.datafactory.Unmarshaller.processConditions;
import static com.divroll.datafactory.Unmarshaller.processUnsatisfiedConditions;

/**
 * The EntityStoreImpl class provides an implementation for storing and retrieving entities.
 */
public class EntityStoreImpl extends StoreBaseImpl implements EntityStore {

  /**
   * The logger is used to log information, warning, and error messages during the execution of the application.
   */
  private static final Logger LOG = LoggerFactory.getLogger(EntityStoreImpl.class);

  /**
   * This variable represents a provider for accessing a database manager.
   *
   * The DatabaseManagerProvider class is responsible for providing an instance of a DatabaseManager,
   * which can be used to interact with a database. The DatabaseManagerProvider object encapsulates
   * the logic for creating and initializing the DatabaseManager instance.
   *
   * Usage:
   * The databaseManagerProvider variable can be used to obtain a DatabaseManager instance by calling
   * the appropriate methods provided by the DatabaseManagerProvider class.
   *
   * Example:
   * DatabaseManagerProvider databaseManagerProvider = new DatabaseManagerProvider();
   * // Get a DatabaseManager instance
   * DatabaseManager databaseManager = databaseManagerProvider.getDatabaseManager();
   *
   * Note: It is recommended to use dependency injection or a singleton pattern to ensure
   *       that only one instance of the DatabaseManagerProvider is used throughout the application.
   */
  private DatabaseManagerProvider databaseManagerProvider;

  /**
   * The private variable luceneIndexerProvider is an instance of the LuceneIndexerProvider interface.
   *
   * This variable is used to retrieve a LuceneIndexer instance, which provides functionality for indexing and searching data.
   *
   * To retrieve a LuceneIndexer instance, use the getLuceneIndexer() method provided by the LuceneIndexerProvider interface.
   *
   * Example usage:
   *
   * LuceneIndexerProvider provider = luceneIndexerProvider;
   * LuceneIndexer indexer = provider.getLuceneIndexer();
   */
  private LuceneIndexerProvider luceneIndexerProvider;

  /**
   * EntityStoreImpl is a class that provides implementation for storing and retrieving entities in a data factory system.
   *
   * @param databaseManagerProvider The provider for retrieving an instance of DatabaseManager.

   * @param luceneIndexerProvider The provider for retrieving an instance of LuceneIndexer.

   *

   * @throws DataFactoryException If there is an error in the data factory.

   * @throws NotBoundException If a remote object is not bound to the specified name in the registry.

   * @throws RemoteException If a remote communication error occurs.

   */
  public EntityStoreImpl(DatabaseManagerProvider databaseManagerProvider, LuceneIndexerProvider luceneIndexerProvider)
      throws DataFactoryException, NotBoundException, RemoteException {
    this.databaseManagerProvider = databaseManagerProvider;
    this.luceneIndexerProvider = luceneIndexerProvider;
  }

  /**
   * Saves a DataFactoryEntity and returns an Option of the saved entity.
   *
   * @param entity The DataFactoryEntity to be saved. Cannot be null.
   * @return An Option of the saved DataFactoryEntity. Returns None if the entity could not be saved.
   * @throws DataFactoryException   if there is an error in the data factory.
   * @throws NotBoundException      if a remote object is not bound to the specified name in the registry.
   * @throws RemoteException        if a remote communication error occurs.
   */
  @Override public Option<DataFactoryEntity> saveEntity(@NotNull DataFactoryEntity entity)
      throws DataFactoryException, NotBoundException, RemoteException {
    DataFactoryEntities dataFactoryEntities = saveEntities(new DataFactoryEntity[] {entity}).get();
    return Option.of(dataFactoryEntities.entities().stream().findFirst().get());
  }

  /**
   * Saves an array of DataFactoryEntities.
   *
   * @param entities the array of DataFactoryEntities to be saved
   * @return an Option containing a DataFactoryEntities object if the save operation was successful, otherwise an empty Option
   * @throws DataFactoryException if an error occurs during the save operation
   * @throws NotBoundException if the entity store is not bound
   * @throws RemoteException if a remote communication error occurs
   */
  @Override
  public Option<DataFactoryEntities> saveEntities(@NotNull DataFactoryEntity[] entities)
      throws DataFactoryException, NotBoundException, RemoteException {
    Map<String, List<DataFactoryEntity>> envIdOrderedEntities = sort(entities);
    Iterator<String> it = envIdOrderedEntities.keySet().iterator();
    AtomicReference<DataFactoryEntities> finalResult = new AtomicReference<>(null);
    while (it.hasNext()) {
      String dir = it.next();
      List<DataFactoryEntity> dataFactoryEntityList = envIdOrderedEntities.get(dir);
      getDatabaseManager().transactPersistentEntityStore(dir, false, txn -> {

        List<DataFactoryEntity> resultEntities = new ArrayList<>();
        dataFactoryEntityList.forEach(entity -> {

          /**
           * Build a {@linkplain Entity} in context of a referenced scoped entities based on the
           * {@code namespace}
           */

          final AtomicReference<EntityIterable> reference = new AtomicReference<>();
          final Entity entityInContext = Unmarshaller.buildContexedEntity(entity, reference, txn);

          /**
           * Filter the {@linkplain EntityIterable} reference scoped
           */
          reference.set(
              Unmarshaller.filterContext(reference, entity.filters(), entity.entityType(), txn));

          /**
           * Process entity conditions, if there are no {@linkplain UnsatisfiedCondition}
           * process the actions, blobs and properties
           */
          processUnsatisfiedConditions(reference, entity.conditions(), entityInContext, txn);

          /**
           * Process entity actions within the context of the {@linkplain Entity}
           */
          processActions(entity, reference, entityInContext, txn);

          entity.blobs().forEach(remoteBlob -> {
            InputStream blobStream =
                Try.of(() -> RemoteInputStreamClient.wrap(remoteBlob.blobStream())).getOrNull();
            entityInContext.setBlob(remoteBlob.blobName(), blobStream);
          });

          /**
           * Process properties to save or update.
           * Saving null value for a property effectively deletes the property
           */

          Iterator<String> propertyIterator = entity.propertyMap().keySet().iterator();
          while (propertyIterator.hasNext()) {
            String key = propertyIterator.next();
            Comparable value = entity.propertyMap().get(key);
            entityInContext.setProperty(key, value);
          }

          resultEntities.add(new Marshaller()
              .with(entityInContext)
              .build());
        });
        finalResult.set(new DataFactoryEntitiesBuilder()
            .entities(resultEntities)
            .build());
      });
    }
    return Option.of(finalResult.get());
  }

  /**
   * Retrieves an entity from the DataFactory system based on the provided entity query.
   *
   * @param query The entity query used to retrieve the entity.
   * @return An Option object containing the retrieved DataFactoryEntity if it exists, otherwise an empty Option object.
   * @throws DataFactoryException If there was an error retrieving the entity.
   * @throws NotBoundException if the entity is not bound.
   * @throws RemoteException if a communication error occurred during the remote call.
   */
  @Override public Option<DataFactoryEntity> getEntity(@NotNull EntityQuery query)
      throws DataFactoryException, NotBoundException, RemoteException {
    Option<DataFactoryEntities> optional = getEntities(query);
    if (optional.isDefined()) {
      return Option.of(optional.get().entities().stream().findFirst().get());
    } else {
      return Option.none();
    }
  }

  /**
   * Retrieves entities from the data factory based on the provided query parameters.
   *
   * @param query The query object containing the parameters for retrieving entities.
   * @return An Option wrapping a DataFactoryEntities object that contains the retrieved entities.
   * @throws DataFactoryException if an error occurs while retrieving the entities.
   * @throws NotBoundException if the data factory is not bound.
   * @throws RemoteException if a network error occurs during the remote communication.
   */
  @Override
  public Option<DataFactoryEntities> getEntities(@NotNull EntityQuery query)
      throws DataFactoryException, NotBoundException, RemoteException {

    String dir = query.environment();
    AtomicReference<String> entityType = new AtomicReference<>(query.entityType());
    String nameSpace = query.nameSpace();

    List<DataFactoryEntity> remoteEntities = new ArrayList<>();

    AtomicReference<Long> count = new AtomicReference<>(0L);

    getDatabaseManager().transactPersistentEntityStore(dir, true, txn -> {

      AtomicReference<EntityIterable> result = new AtomicReference<>();
      if (nameSpace != null && !nameSpace.isEmpty() && entityType.get() != null) {
        result.set(txn.getAll(entityType.get())
            .intersect(txn.find(entityType.get(), Constants.NAMESPACE_PROPERTY, nameSpace)));
      } else if (entityType.get() != null) {
        result.set(txn.getAll(entityType.get()));
      }

      if (query.entityId() == null && entityType.get() == null) {
        throw new IllegalArgumentException("Either entity ID or entity type must be present");
      } else if (query.entityId() == null) {
        processConditions(getSearchIndexer(), entityType.get(), query.conditions(), result, txn);
      }

      if (query.entityId() != null) {
        // Query by id encompasses name spacing
        EntityId idOfEntity = txn.toEntityId(query.entityId());
        final Entity entity = txn.getEntity(idOfEntity);
        entityType.set(entity.getType());
        remoteEntities.add(new Marshaller().with(entity)
            .with(FluentIterable.from(query.blobQueries()).toArray(BlobQuery.class))
            .with(FluentIterable.from(query.linkQueries()).toArray(LinkQuery.class))
            .build());
        count.set(1L);
      } else {
        result.set(
            Unmarshaller.filterContext(result, query.filters(), entityType.get(), txn));
        count.set(result.get().size());
        result.set(result.get().skip(query.offset()).take(query.max()));

        for (Entity entity : result.get()) {
          final Map<String, Comparable> comparableMap = new LinkedHashMap<>();
          for (String property : entity.getPropertyNames()) {
            Comparable value = entity.getProperty(property);
            if (value != null) {
              if (value != null) {
                if (value instanceof EmbeddedEntityIterable) {
                  comparableMap.put(property, ((EmbeddedEntityIterable) value).asObject());
                } else if (value instanceof EmbeddedArrayIterable) {
                  comparableMap.put(
                      property, (Comparable) ((EmbeddedArrayIterable) value).asObject());
                } else {
                  comparableMap.put(property, value);
                }
              }
            }
          }
          remoteEntities.add(new Marshaller().with(entity)
              .with(FluentIterable.from(query.blobQueries()).toArray(BlobQuery.class))
              .with(FluentIterable.from(query.linkQueries()).toArray(LinkQuery.class))
              .build());
        }
      }
    });

    return Option.of(new DataFactoryEntitiesBuilder()
        .entities(remoteEntities)
        .offset(query.offset())
        .max(query.max())
        .count(count.get())
        .build());
  }

  /**
   *
   */
  @Override public Boolean removeEntity(@NotNull EntityQuery query)
      throws DataFactoryException, NotBoundException, RemoteException {
    return removeEntities(new EntityQuery[] {query});
  }

  /**
   * Removes entities matching the given queries from the persistent store.
   *
   * @param queries The array of EntityQuery objects representing the queries to filter the entities to remove.
   * @return true if the entities were successfully removed, false otherwise.
   * @throws DataFactoryException if an error occurs during the removal process.
   * @throws NotBoundException if the persistent store is not bound.
   * @throws RemoteException if there is a remote communication error.
   */
  @Override public Boolean removeEntities(@NotNull EntityQuery[] queries)
      throws DataFactoryException, NotBoundException, RemoteException {
    final boolean[] success = {false};
    final boolean[] hasRemovedEntities = {false};
    Map<String, List<EntityQuery>> dirOrderedQueries = sort(queries);
    Iterator<String> it = dirOrderedQueries.keySet().iterator();
    while (it.hasNext()) {
      String dir = it.next();
      List<EntityQuery> queryList = dirOrderedQueries.get(dir);
      getDatabaseManager().transactPersistentEntityStore(dir, false, txn -> {
        queryList.forEach(query -> {

          // Build the list of entities to be removed
          String entityType = query.entityType();
          String nameSpace = query.nameSpace();
          EntityIterable result = null;
          if (nameSpace != null && !nameSpace.isEmpty()) {
            result =
                txn.findWithProp(entityType, Constants.NAMESPACE_PROPERTY)
                    .intersect(txn.find(entityType, Constants.NAMESPACE_PROPERTY, nameSpace));
          } else {
            result =
                txn.getAll(entityType)
                    .minus(txn.findWithProp(entityType, Constants.NAMESPACE_PROPERTY));
          }

          for(BlobQuery blobQuery : query.blobQueries()) {
            result = result.intersect(txn.findWithBlob(entityType, blobQuery.blobName()));
          }

          // Remove the entities
          final boolean[] hasError = {false};
          for (Entity entity : result) {
            entity.getLinkNames().forEach(linkName -> {
              Entity linked = entity.getLink(linkName);
              entity.deleteLink(linkName, linked);
            });

            // TODO: This is a performance issue
            // Review if this is even needed!
            final List<String> allLinkNames = ((PersistentEntityStoreImpl) txn.getStore())
                .getAllLinkNames(
                    (PersistentStoreTransaction) txn.getStore().getCurrentTransaction());
            for (final String entityType1 : txn.getEntityTypes()) {
              for (final String linkName : allLinkNames) {
                for (final Entity referrer : txn.findLinks(entityType1, entity, linkName)) {
                  referrer.deleteLink(linkName, entity);
                }
              }
            }

            for(String blobName : entity.getBlobNames()) {
              entity.deleteBlob(blobName);
            }

            if (!entity.delete()) {
              hasError[0] = true;
            } else {
                hasRemovedEntities[0] = true;
            }
          }
          success[0] = (!hasError[0] && hasRemovedEntities[0]);
        });
      });
    }

    return success[0];
  }

  /**
   * Saves a DataFactoryProperty.
   *
   * @param property The DataFactoryProperty to be saved.
   * @return true if the property was successfully saved, false otherwise.
   * @throws DataFactoryException If there is an error in the data factory.
   * @throws NotBoundException If the data factory is not bound.
   * @throws RemoteException If a remote connection error occurs.
   */
  @Override public Boolean saveProperty(@NotNull DataFactoryProperty property)
      throws DataFactoryException, NotBoundException, RemoteException {
    String dir = property.environment();
    String entityType = property.entityType();
    String nameSpace = property.nameSpace();
    AtomicReference<Boolean> updated = new AtomicReference<>(false);
    getDatabaseManager().transactPersistentEntityStore(dir, false, txn -> {
      final AtomicReference<EntityIterable> reference = new AtomicReference<>();
      if (nameSpace != null) {
        reference.set(
            txn.getAll(entityType)
                .intersect(
                    txn.find(entityType, Constants.NAMESPACE_PROPERTY, nameSpace)));
      } else {
        reference.set(txn.getAll(entityType));
      }
      property.propertyActions().forEach(entityPropertyAction -> {
        if (entityPropertyAction instanceof PropertyRenameAction) {
          reference.set(reference.get().intersect(txn.getAll(entityType)));
          EntityIterable entities = reference.get();
          String propertyName = ((PropertyRenameAction) entityPropertyAction).propertyName();
          String newPropertyName = ((PropertyRenameAction) entityPropertyAction).newPropertyName();
          Boolean overwrite = ((PropertyRenameAction) entityPropertyAction).overwrite();
          entities.forEach(entity -> {
            if (entity.getProperty(newPropertyName) != null && !overwrite) {
              throw new IllegalArgumentException(
                  "Conflicting property " + newPropertyName + " exists");
            }
            Comparable propertyValue = entity.getProperty(propertyName);
            entity.deleteProperty(propertyName);
            entity.setProperty(propertyName, propertyValue);
          });
        }
      });
      updated.set(true);
    });
    return updated.get();
  }

  /**
   * Removes the specified property from the DataFactory.
   *
   * @param property The property to be removed.
   * @return {@code true} if the property is successfully removed, {@code false} otherwise.
   * @throws DataFactoryException if there is an error in the data factory.
   * @throws NotBoundException if the DataFactory is not bound.
   * @throws RemoteException if there is a remote communication error.
   */
  @Override public Boolean removeProperty(@NotNull DataFactoryProperty property)
      throws DataFactoryException, NotBoundException, RemoteException {
    String dir = property.environment();
    String entityType = property.entityType();
    String nameSpace = property.nameSpace();
    AtomicReference<Boolean> removed = new AtomicReference<>(false);
    getDatabaseManager().transactPersistentEntityStore(dir, false, txn -> {
      final AtomicReference<EntityIterable> reference = new AtomicReference<>();
      if (nameSpace != null) {
        reference.set(
            txn.getAll(entityType)
                .intersect(
                    txn.find(entityType, Constants.NAMESPACE_PROPERTY, nameSpace)));
      } else {
        reference.set(txn.getAll(entityType));
      }
      property.propertyActions().forEach(entityPropertyAction -> {
        if (entityPropertyAction instanceof PropertyRemoveAction) {
          reference.set(reference.get().intersect(txn.getAll(entityType)));
          String propertyName = ((PropertyRemoveAction) entityPropertyAction).propertyName();
          reference.set(reference.get().intersect(txn.findWithProp(entityType, propertyName)));
          reference.get().forEach(entity -> {
            entity.deleteProperty(propertyName);
          });
        } else {
          throw new IllegalArgumentException("Invalid property action");
        }
      });
      removed.set(true);
    });
    return removed.get();
  }

  /**
   * Removes entities of a specific type based on the provided query.
   *
   * @param query The query representing the entity type and optional namespace to remove.
   * @return True if the entities were successfully removed, otherwise false.
   * @throws RemoteException If a remote exception occurs.
   * @throws NotBoundException If the entity store is not bound.
   */
  @Override
  public Boolean removeEntityType(@NotNull EntityQuery query)
      throws RemoteException, NotBoundException {
    final boolean[] success = {false};

    String dir = query.environment();
    String entityType = query.entityType();
    String nameSpace = query.nameSpace();

    getDatabaseManager().transactPersistentEntityStore(dir, false, txn -> {
      final AtomicReference<EntityIterable> reference = new AtomicReference<>();
      if (nameSpace != null) {
        reference.set(
            txn.getAll(entityType)
                .intersect(
                    txn.find(entityType, Constants.NAMESPACE_PROPERTY, nameSpace)));
      } else {
        reference.set(txn.getAll(entityType));
      }
      EntityIterable result = reference.get();
      for (Entity entity : result) {
        entity.getLinkNames().forEach(linkName -> {
          Entity linked = entity.getLink(linkName);
          entity.deleteLink(linkName, linked);
        });
        // TODO: This is a performance issue
        final List<String> allLinkNames = ((PersistentEntityStoreImpl) txn.getStore())
            .getAllLinkNames((PersistentStoreTransaction) txn.getStore().getCurrentTransaction());
        for (final String entityType1 : txn.getEntityTypes()) {
          for (final String linkName : allLinkNames) {
            for (final Entity referrer : txn.findLinks(entityType1, entity, linkName)) {
              referrer.deleteLink(linkName, entity);
            }
          }
        }
      }
    });
    return success[0];
  }

  /**
   *
   */
  @Override public Option<DataFactoryEntityTypes> getEntityTypes(EntityTypeQuery query)
      throws DataFactoryException, NotBoundException, RemoteException {
    AtomicReference<DataFactoryEntityTypes> atomicReference = new AtomicReference<>();
    getDatabaseManager().transactPersistentEntityStore(query.environment(), true, txn -> {
      List<String> entityTypes = txn.getEntityTypes();
      Long count = null;
      if(query.count()) {
        EntityIterable entities = txn.getAll(query.entityType());
        count = entities.size();
      }
      List<DataFactoryEntityType> dataFactoryEntityTypeList = new ArrayList<>();
      if (entityTypes != null) {
        for (String entityType : entityTypes) {
          dataFactoryEntityTypeList.add(new DataFactoryEntityTypeBuilder()
              .entityTypeName(entityType)
              .build());
        }
      }
      DataFactoryEntityTypes dataFactoryEntityTypes = new DataFactoryEntityTypesBuilder()
          .entityTypes(dataFactoryEntityTypeList)
          .entityCount(count)
          .build();
      atomicReference.set(dataFactoryEntityTypes);
    });
    return Option.of(atomicReference.get());
  }

  /**
   * Sort an array of {@linkplain EntityUpdate} by Environment path.
   *
   * @param entityUpdates
   * @return
   */
  //private static Map<String, List<EntityUpdate>> sort(EntityUpdate[] entityUpdates) {
  //  Map<String, List<EntityUpdate>> envOrderedUpdates = new HashMap<>();
  //  Arrays.asList(entityUpdates).forEach(entityUpdate -> {
  //    String dir = entityUpdate.entity().environment();
  //    List<EntityUpdate> remoteEntityUpdates = envOrderedUpdates.get(dir);
  //    if (remoteEntityUpdates == null) {
  //      remoteEntityUpdates = new ArrayList<>();
  //    }
  //    remoteEntityUpdates.add(entityUpdate);
  //  });
  //  return envOrderedUpdates;
  //}

  /**
   * Sort an array of {@linkplain DataFactoryEntity} by Application ID.
   *
   * @param entities the array of DataFactoryEntity objects to be sorted
   * @return a Map object containing the sorted entities mapped to their respective directories
   */
  private static Map<String, List<DataFactoryEntity>> sort(DataFactoryEntity[] entities) {
    Map<String, List<DataFactoryEntity>> dirOrderedEntities = new HashMap<>();
    Arrays.asList(entities).forEach(remoteEntity -> {
      String dir = remoteEntity.environment();
      List<DataFactoryEntity> dataFactoryEntityUpdates = dirOrderedEntities.get(dir);
      if (dataFactoryEntityUpdates == null) {
        dataFactoryEntityUpdates = new ArrayList<>();
      }
      dataFactoryEntityUpdates.add(remoteEntity);
      dirOrderedEntities.put(dir, dataFactoryEntityUpdates);
    });
    return dirOrderedEntities;
  }

  /**
   *
   */
  private static Map<String, List<EntityQuery>> sort(EntityQuery[] entities) {
    Map<String, List<EntityQuery>> dirOrderedQueries = new HashMap<>();
    Arrays.asList(entities).forEach(remoteEntity -> {
      String dir = remoteEntity.environment();
      List<EntityQuery> entityQueries = dirOrderedQueries.get(dir);
      if (entityQueries == null) {
        entityQueries = new ArrayList<>();
        dirOrderedQueries.put(dir, entityQueries); // Put new ArrayList into the map
      }
      entityQueries.add(remoteEntity);
    });
    return dirOrderedQueries;
  }

  /**
   * Converts an instance of EmbeddedEntityIterable to Comparable.
   *
   * @param entityIterable The EmbeddedEntityIterable instance to convert.
   * @return A Comparable instance representing the converted entityIterable,
   *         or the original entityIterable if it cannot be converted.
   */
  private Comparable asObject(EmbeddedEntityIterable entityIterable) {
    Comparable comparable = entityIterable.asObject();
    if (comparable instanceof ComparableHashMap) {
      ComparableHashMap comparableHashMap = (ComparableHashMap) comparable;
      ComparableHashMap replacements = new ComparableHashMap();
      comparableHashMap.forEach((key, value) -> {
        if (value instanceof EmbeddedEntityIterable) {
          replacements.put(key, asObject((EmbeddedEntityIterable) value));
        }
      });
      comparableHashMap.putAll(replacements);
      return comparableHashMap;
    }
    return comparable;
  }

  /**
   * Retrieves the DatabaseManager instance from the provider.
   *
   * @return The DatabaseManager instance.
   */
  private DatabaseManager getDatabaseManager() {
    return databaseManagerProvider.getDatabaseManager();
  }

  /**
   * Retrieves the LuceneIndexer instance used for search indexing.
   *
   * @return The LuceneIndexer instance.
   */
  private LuceneIndexer getSearchIndexer() {
    return luceneIndexerProvider.getLuceneIndexer();
  }
}
