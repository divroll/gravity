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
import com.divroll.datafactory.builders.Entities;
import com.divroll.datafactory.builders.EntitiesBuilder;
import com.divroll.datafactory.builders.Entity;
import com.divroll.datafactory.builders.Property;
import com.divroll.datafactory.builders.EntityType;
import com.divroll.datafactory.builders.EntityTypeBuilder;
import com.divroll.datafactory.builders.EntityTypes;
import com.divroll.datafactory.builders.EntityTypesBuilder;
import com.divroll.datafactory.builders.queries.BlobQuery;
import com.divroll.datafactory.builders.queries.EntityQuery;
import com.divroll.datafactory.builders.queries.EntityTypeQuery;
import com.divroll.datafactory.builders.queries.LinkQuery;
import com.divroll.datafactory.database.DatabaseManager;
import com.divroll.datafactory.database.DatabaseManagerProvider;
import com.divroll.datafactory.database.impl.DatabaseManagerImpl;
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
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;
import jetbrains.exodus.entitystore.PersistentEntityStoreImpl;
import jetbrains.exodus.entitystore.PersistentStoreTransaction;
import org.jetbrains.annotations.NotNull;
import util.ComparableHashMap;

import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static com.divroll.datafactory.Unmarshaller.processActions;
import static com.divroll.datafactory.Unmarshaller.processConditions;
import static com.divroll.datafactory.Unmarshaller.processUnsatisfiedConditions;

/**
 * The EntityStoreImpl class provides an implementation for storing and retrieving entities.
 */
public class EntityStoreImpl extends StoreBaseImpl implements EntityStore {

  /**
   * Logger for capturing events, errors, and informational messages within
   * the {@code EntityStoreImpl} class. Utilizes the {@link LoggerFactory} to create the
   * logger instance specific to this class.
   */
  private static final Logger LOG = LoggerFactory.getLogger(EntityStoreImpl.class);

  /**
   * The {@code databaseManagerProvider} variable serves as a provider for accessing
   * a database manager, specifically designed for use within the {@link EntityStoreImpl} class
   * to manage interactions with the database. Its primary role is to prevent direct references to
   * the {@link DatabaseManagerImpl}, addressing serialization concerns that may arise
   * from such direct linkage.
   *
   * This abstraction facilitates a loose coupling between the {@code EntityStoreImpl} and
   * the {@code DatabaseManagerImpl}, ensuring that changes in the database management
   * implementation have minimal impact on the entity storage logic.
   *
   * To obtain an instance of the DatabaseManager, the {@link #getDatabaseManager()} method should
   * be invoked on the {@code databaseManagerProvider}. This approach promotes a cleaner,
   * more maintainable codebase by encapsulating the mechanism for database manager retrieval.
   */
  private DatabaseManagerProvider databaseManagerProvider;

  /**
   * The private variable 'luceneIndexerProvider' is of type LuceneIndexerProvider.
   *
   * LuceneIndexerProvider is an interface that provides a method to retrieve a LuceneIndexer
   * instance. It has a single method getLuceneIndexer() which returns the LuceneIndexer instance.
   *
   * This variable is used in the class 'EntityStoreImpl' to retrieve the LuceneIndexer instance
   * used for search indexing.
   *
   * To get the LuceneIndexer instance, call the getLuceneIndexer() method on the
   * luceneIndexerProvider variable.
   */
  private LuceneIndexerProvider luceneIndexerProvider;

  /**
   * EntityStoreImpl is a class that provides implementation for storing and retrieving entities.
   *
   * @param databaseManagerProvider The provider for retrieving an instance of DatabaseManager.
   * @param luceneIndexerProvider The provider for retrieving an instance of LuceneIndexer.
   * @throws DataFactoryException If there is an error in the data factory.
   * @throws NotBoundException  If a remote object is not bound to the specified name
   *                            in the registry.
   * @throws RemoteException If a remote communication error occurs.
   */
  public EntityStoreImpl(final DatabaseManagerProvider databaseManagerProvider,
                         final LuceneIndexerProvider luceneIndexerProvider)
      throws DataFactoryException, NotBoundException, RemoteException {
    this.databaseManagerProvider = databaseManagerProvider;
    this.luceneIndexerProvider = luceneIndexerProvider;
  }

  /**
   * Saves a Entity and returns an Option of the saved entity.
   *
   * @param entity The Entity to be saved. Cannot be null.
   * @return  An Option of the saved Entity.
   *          Returns None if the entity could not be saved.
   * @throws DataFactoryException if there is an error in the data factory.
   * @throws NotBoundException    if a remote object is not bound to the specified name in
   *                              the registry.
   * @throws RemoteException      if a remote communication error occurs.
   */
  @Override public Option<Entity> saveEntity(@NotNull final Entity entity)
      throws DataFactoryException, NotBoundException, RemoteException {
    Entities entities = saveEntities(new Entity[] {entity}).get();
    return Option.of(entities.entities().stream().findFirst().get());
  }

  /**
   * Saves an array of Entities.
   *
   * @param entities the array of Entities to be saved
   * @return  an Option containing a Entities object
   *          if the save operation was successful, otherwise an empty Option
   * @throws DataFactoryException if an error occurs during the save operation
   * @throws RemoteException if a remote communication error occurs
   */
  @Override
  public Option<Entities> saveEntities(@NotNull final Entity[] entities)
      throws DataFactoryException, RemoteException {
    Map<String, List<Entity>> envIdOrderedEntities = sort(entities);
    Iterator<String> it = envIdOrderedEntities.keySet().iterator();
    AtomicReference<Entities> finalResult = new AtomicReference<>(null);
    while (it.hasNext()) {
      String dir = it.next();
      List<Entity> entityList = envIdOrderedEntities.get(dir);
      getDatabaseManager().transactPersistentEntityStore(dir, false, txn -> {

        List<Entity> resultEntities = new ArrayList<>();
        entityList.forEach(entity -> {

          // Build an entity in context of a referenced scoped entities based on the
          // namespace
          final AtomicReference<EntityIterable> reference = new AtomicReference<>();
          final jetbrains.exodus.entitystore.Entity entityInContext
                  = Unmarshaller.buildContexedEntity(entity, reference, txn);

          // Filter the scoped entities based on the namespace
          reference.set(
              Unmarshaller.filterContext(reference, entity.filters(), entity.entityType(), txn));

          // Process unsatisfied conditions
          // If there are unsatisfied conditions, the entity will not be saved
          // And the actions, blobs and properties will not be processed
          processUnsatisfiedConditions(reference, entity.conditions(), entityInContext, txn);

          // Process actions within the context of the entity
          processActions(entity, reference, entityInContext, txn);

          entity.blobs().forEach(remoteBlob -> {
            InputStream blobStream =
                Try.of(() -> RemoteInputStreamClient.wrap(remoteBlob.blobStream())).getOrNull();
            entityInContext.setBlob(remoteBlob.blobName(), blobStream);
          });

          // Process properties,
          // Note: Saving null values for properties effectively deletes them
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
        finalResult.set(new EntitiesBuilder()
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
   * @return  An Option object containing the retrieved Entity if it exists,
   *          otherwise an empty Option object.
   * @throws DataFactoryException If there was an error retrieving the entity.
   * @throws NotBoundException if the entity is not bound.
   * @throws RemoteException if a communication error occurred during the remote call.
   */
  @Override public Option<Entity> getEntity(@NotNull final EntityQuery query)
      throws DataFactoryException, NotBoundException, RemoteException {
    Option<Entities> optional = getEntities(query);
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
   * @return An Option wrapping a Entities object that contains the retrieved entities.
   * @throws DataFactoryException if an error occurs while retrieving the entities.
   * @throws NotBoundException if the data factory is not bound.
   * @throws RemoteException if a network error occurs during the remote communication.
   */
  @Override
  public Option<Entities> getEntities(@NotNull final EntityQuery query)
      throws DataFactoryException, NotBoundException, RemoteException {

    String dir = query.environment();
    AtomicReference<String> entityType = new AtomicReference<>(query.entityType());
    String nameSpace = query.nameSpace();

    List<Entity> remoteEntities = new ArrayList<>();

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
        final jetbrains.exodus.entitystore.Entity entity = txn.getEntity(idOfEntity);
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

        for (jetbrains.exodus.entitystore.Entity entity : result.get()) {
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

    return Option.of(new EntitiesBuilder()
        .entities(remoteEntities)
        .offset(query.offset())
        .max(query.max())
        .count(count.get())
        .build());
  }

  /**
   *
   */
  @Override public Boolean removeEntity(@NotNull final EntityQuery query)
      throws DataFactoryException, NotBoundException, RemoteException {
    return removeEntities(new EntityQuery[] {query});
  }

  /**
   * Removes entities matching the given queries from the persistent store.
   *
   * @param queries The array of EntityQuery objects representing the queries to filter
   *                the entities to remove.
   * @return true if the entities were successfully removed, false otherwise.
   * @throws DataFactoryException if an error occurs during the removal process.
   * @throws NotBoundException if the persistent store is not bound.
   * @throws RemoteException if there is a remote communication error.
   */
  @Override public Boolean removeEntities(@NotNull final EntityQuery[] queries)
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

          for (BlobQuery blobQuery : query.blobQueries()) {
            result = result.intersect(txn.findWithBlob(entityType, blobQuery.blobName()));
          }

          // Remove the entities
          final boolean[] hasError = {false};
          for (jetbrains.exodus.entitystore.Entity entity : result) {
            entity.getLinkNames().forEach(linkName -> {
              jetbrains.exodus.entitystore.Entity linked = entity.getLink(linkName);
              entity.deleteLink(linkName, linked);
            });

            deleteEntityLinks(entity, txn);

            for (String blobName : entity.getBlobNames()) {
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
   * Saves a Property.
   *
   * @param property The Property to be saved.
   * @return true if the property was successfully saved, false otherwise.
   * @throws DataFactoryException If there is an error in the data factory.
   * @throws NotBoundException If the data factory is not bound.
   * @throws RemoteException If a remote connection error occurs.
   */
  @Override public Boolean saveProperty(@NotNull final Property property)
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
  @Override public Boolean removeProperty(@NotNull final Property property)
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
  public Boolean removeEntityType(@NotNull final EntityQuery query)
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
      for (jetbrains.exodus.entitystore.Entity entity : result) {
        entity.getLinkNames().forEach(linkName -> {
          jetbrains.exodus.entitystore.Entity linked = entity.getLink(linkName);
          entity.deleteLink(linkName, linked);
        });
        deleteEntityLinks(entity, txn);
      }
    });
    return success[0];
  }

  /**
   *
   */
  @Override public Option<EntityTypes> getEntityTypes(final EntityTypeQuery query)
      throws DataFactoryException, NotBoundException, RemoteException {
    AtomicReference<EntityTypes> atomicReference = new AtomicReference<>();
    getDatabaseManager().transactPersistentEntityStore(query.environment(), true, txn -> {
      List<String> types = txn.getEntityTypes();
      Long count = null;
      if (query.count()) {
        EntityIterable entities = txn.getAll(query.entityType());
        count = entities.size();
      }
      List<EntityType> entityTypeList = new ArrayList<>();
      if (types != null) {
        for (String entityType : types) {
          entityTypeList.add(new EntityTypeBuilder()
              .entityTypeName(entityType)
              .build());
        }
      }
      EntityTypes entityTypes = new EntityTypesBuilder()
          .entityTypes(entityTypeList)
          .entityCount(count)
          .build();
      atomicReference.set(entityTypes);
    });
    return Option.of(atomicReference.get());
  }

  /**
   * Deletes all links associated with a given entity.
   *
   * @param entity The entity for which to delete the links. Cannot be null.
   * @param storeTransaction The transaction object used to interact with the persistent store.
   *                         Cannot be null.
   */
  private void deleteEntityLinks(final jetbrains.exodus.entitystore.Entity entity,
                                 final StoreTransaction storeTransaction) {
    PersistentStoreTransaction currentTransaction
            = (PersistentStoreTransaction) storeTransaction.getStore().getCurrentTransaction();
    final List<String> allLinkNames = getAllLinks(storeTransaction, currentTransaction);
    List<String> entityTypes = getEntityTypes(storeTransaction);
    for (String entityType : entityTypes) {
        deleteLinks(entity, storeTransaction, allLinkNames, entityType);
    }
  }

  /**
   * Retrieves the list of entity types from the given store transaction.
   *
   * @param storeTransaction The transaction object used to interact with the persistent store.
   *                         Cannot be null.
   * @return A list of strings representing the entity types in the store transaction.
   */
  private List<String> getEntityTypes(final StoreTransaction storeTransaction) {
    return new ArrayList<>(storeTransaction.getEntityTypes());
  }

  /**
   * Retrieves all the link names from the persistent store.
   *
   * @param storeTransaction      The transaction object used to interact with the persistent store.
   *                              Cannot be null.
   * @param currentTransaction    The current transaction object used for the store.
   *                              Cannot be null.
   * @return A list of strings representing all the link names in the persistent store.
   */
  private List<String> getAllLinks(final StoreTransaction storeTransaction,
                                   final PersistentStoreTransaction currentTransaction) {
    return ((PersistentEntityStoreImpl) storeTransaction.getStore())
            .getAllLinkNames(currentTransaction);
  }

  private void deleteLinks(final jetbrains.exodus.entitystore.Entity entity,
                           final StoreTransaction storeTransaction,
                           final List<String> allLinkNames,
                           final String entityType) {
    allLinkNames.forEach(linkName -> {
      for (final jetbrains.exodus.entitystore.Entity referrer
              : storeTransaction.findLinks(entityType, entity, linkName)) {
        referrer.deleteLink(linkName, entity);
      }
    });
  }

  /**
   * Sort an array of {@linkplain Entity} by Application ID.
   *
   * @param entities the array of Entity objects to be sorted
   * @return a Map object containing the sorted entities mapped to their respective directories
   */
  private static Map<String, List<Entity>> sort(final Entity[] entities) {
    Map<String, List<Entity>> dirOrderedEntities = new HashMap<>();
    Arrays.asList(entities).forEach(remoteEntity -> {
      String dir = remoteEntity.environment();
      List<Entity> entityUpdates = dirOrderedEntities.get(dir);
      if (entityUpdates == null) {
        entityUpdates = new ArrayList<>();
      }
      entityUpdates.add(remoteEntity);
      dirOrderedEntities.put(dir, entityUpdates);
    });
    return dirOrderedEntities;
  }

  /**
   * Sorts an array of {@code EntityQuery} objects by directory.
   *
   * @param entities The array of {@code EntityQuery} objects to be sorted. Cannot be null.
   * @return A {@code Map} object containing the sorted entities mapped
   * to their respective directories.
   */
  private static Map<String, List<EntityQuery>> sort(final EntityQuery[] entities) {
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
  private Comparable asObject(final EmbeddedEntityIterable entityIterable) {
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
