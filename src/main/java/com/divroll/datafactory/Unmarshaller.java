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

import ch.hsr.geohash.GeoHash;
import com.divroll.datafactory.actions.BlobRemoveAction;
import com.divroll.datafactory.actions.BlobRenameAction;
import com.divroll.datafactory.actions.BlobRenameRegexAction;
import com.divroll.datafactory.actions.CustomAction;
import com.divroll.datafactory.actions.EntityAction;
import com.divroll.datafactory.actions.LinkAction;
import com.divroll.datafactory.actions.LinkNewEntityAction;
import com.divroll.datafactory.actions.LinkRemoveAction;
import com.divroll.datafactory.actions.OppositeLinkAction;
import com.divroll.datafactory.actions.OppositeLinkRemoveAction;
import com.divroll.datafactory.actions.PropertyCopyAction;
import com.divroll.datafactory.actions.PropertyIndexAction;
import com.divroll.datafactory.actions.PropertyRemoveAction;
import com.divroll.datafactory.builders.DataFactoryEntity;
import com.divroll.datafactory.builders.TransactionFilter;
import com.divroll.datafactory.conditions.BlobExistsCondition;
import com.divroll.datafactory.conditions.CustomQueryCondition;
import com.divroll.datafactory.conditions.EntityCondition;
import com.divroll.datafactory.conditions.PropertyContainsCondition;
import com.divroll.datafactory.conditions.PropertyEqualCondition;
import com.divroll.datafactory.conditions.PropertyLocalTimeRangeCondition;
import com.divroll.datafactory.conditions.PropertyMinMaxCondition;
import com.divroll.datafactory.conditions.PropertyNearbyCondition;
import com.divroll.datafactory.conditions.PropertyStartsWithCondition;
import com.divroll.datafactory.conditions.exceptions.InvalidConditionException;
import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessor;
import com.divroll.datafactory.conditions.processors.EntityConditionProcessors;
import com.divroll.datafactory.database.EqualityOp;
import com.divroll.datafactory.exceptions.DataFactoryException;
import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import com.divroll.datafactory.indexers.LuceneIndexer;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.StoreTransaction;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;
import org.jetbrains.annotations.NotNull;

import static com.divroll.datafactory.exceptions.Throwing.rethrow;

/**
 * Unmarshaller for unmarshalling {@linkplain DataFactoryEntity} into {@linkplain Entity}
 * within the context of the scoped entities.
 */
public class Unmarshaller {
  /**
   * The precision of the geohash used for nearby queries.
   */
  private static final int GEO_HASH_PRECISION = 12;
  /**
   * Represents a data factory entity.
   *
   * <p>
   * This class is used by the Unmarshaller class to store information about a data factory entity.
   * It is typically used as a parameter or return type in various methods of the Un
   *marshaller class.
   * </p>
   *
   * <p>
   * Example usage:
   * </p>
   *
   * <pre>
   * DataFactoryEntity dataFactoryEntity = new DataFactoryEntity();
   * </pre>
   *
   * @see Unmarshaller
   */
  private DataFactoryEntity dataFactoryEntity;
  /**
   * Represents a store transaction.
   */
  private StoreTransaction storeTransaction;

  /**
   * Set the data factory entity and store transaction for the unmarshaller.
   *
   * @param entity            The data factory entity to set.
   * @param storeTransaction  The store transaction to set.
   * @return  The updated instance of the Unmarshaller.
   */
  public Unmarshaller with(@NotNull final DataFactoryEntity entity,
                           @NotNull final StoreTransaction storeTransaction) {
    this.dataFactoryEntity = entity;
    this.storeTransaction = storeTransaction;
    return this;
  }

  /**
   * Builds an Entity based on the specified DataFactoryEntity and transaction.
   *
   * @return  The built Entity.
   * @throws  IllegalArgumentException If the DataFactoryEntity is not set.
   */
  public Entity build() {
    if (dataFactoryEntity == null) {
      new IllegalArgumentException("Must set RemoteEntity to unmarshall into Entity");
    }
    Entity newEntity = storeTransaction.newEntity(dataFactoryEntity.entityType());
    AtomicReference reference
            = new AtomicReference<>(storeTransaction.getAll(dataFactoryEntity.entityType()));
    buildContexedEntity(dataFactoryEntity, reference, storeTransaction);
    processActions(dataFactoryEntity, reference, newEntity, storeTransaction);
    throw new IllegalArgumentException("Not yet implemented");
  }

  /**
   * Builds an Entity in the context of the scoped entities based on the namespace of the given
   * DataFactoryEntity.
   *
   * @param entity           The DataFactoryEntity to build the Entity from. Cannot be null.
   * @param referenceToScope The AtomicReference to hold the scoped entities. Cannot be null.
   * @param storeTransaction The StoreTransaction to use for building the Entity. Cannot be null.
   * @return The built Entity.
   * @throws IllegalArgumentException If the specified entity is not found in the scoped entities
   * and the namespace is not null.
   */
  public static Entity buildContexedEntity(
          @NotNull final DataFactoryEntity entity,
          @NotNull final AtomicReference<EntityIterable> referenceToScope,
          @NotNull final StoreTransaction storeTransaction) {
    findEntityInCurrentScope(entity, referenceToScope, storeTransaction);

    final Entity entityInContext = getEntityFromTransaction(entity, storeTransaction);

    throwIfEntityNotFoundInScopedNamespace(entity, entityInContext, referenceToScope.get());

    return entityInContext;
  }

  /**
   * Process the list of entity conditions and update the reference to scope based on
   * the given parameters.
   *
   * @param indexer The LuceneIndexer used for indexing.
   * @param entityType The type of the entity being processed.
   * @param conditions The list of EntityConditions to process.
   * @param referenceToScope The AtomicReference to hold the scoped entities.
   * @param txn The StoreTransaction to use for processing the conditions.
   * @return The updated EntityIterable based on the processed conditions.
   */
  public static EntityIterable processConditions(
          @NotNull final LuceneIndexer indexer,
          @NotNull final String entityType,
          @NotNull final List<EntityCondition> conditions,
          @NotNull final AtomicReference<EntityIterable> referenceToScope,
          @NotNull final StoreTransaction txn) {
    conditions.forEach(entityCondition -> {
      if (entityCondition instanceof PropertyEqualCondition) {
        PropertyEqualCondition propertyEqualCondition = (PropertyEqualCondition) entityCondition;
        String propertyName = propertyEqualCondition.propertyName();
        Comparable propertyValue = propertyEqualCondition.propertyValue();
        EntityIterable entities = txn.find(entityType, propertyName, propertyValue);
        referenceToScope.set(referenceToScope.get().intersect(entities));
      } else if (entityCondition instanceof PropertyLocalTimeRangeCondition) {
        PropertyLocalTimeRangeCondition propertyLocalTimeRangeCondition =
            (PropertyLocalTimeRangeCondition) entityCondition;
        String propertyName = propertyLocalTimeRangeCondition.propertyName();
        LocalTime upper = propertyLocalTimeRangeCondition.upper();
        LocalTime lower = propertyLocalTimeRangeCondition.lower();
        EntityIterable entities =
            referenceToScope.get()
                .intersect(txn.find(entityType, propertyName, new LocalTimeRange(lower, lower),
                    new LocalTimeRange(upper, upper)));
        referenceToScope.set(entities);
      } else if (entityCondition instanceof PropertyMinMaxCondition) {
        PropertyMinMaxCondition propertyMinMaxCondition
                = (PropertyMinMaxCondition) entityCondition;
        String propertyName = propertyMinMaxCondition.propertyName();
        Comparable minValue = propertyMinMaxCondition.minValue();
        Comparable maxValue = propertyMinMaxCondition.maxValue();
        EntityIterable entities =
            referenceToScope.get()
                .intersect(txn.find(entityType, propertyName, minValue, maxValue));
        referenceToScope.set(entities);
      } else if (entityCondition instanceof PropertyNearbyCondition) {
        PropertyNearbyCondition propertyNearbyCondition = (PropertyNearbyCondition) entityCondition;
        String propertyName = propertyNearbyCondition.propertyName();
        Double longitude = propertyNearbyCondition.longitude();
        Double latitude = propertyNearbyCondition.latitude();
        Double distance = propertyNearbyCondition.distance();
        if (propertyNearbyCondition.useGeoHash()) {
          GeoHash geohash
                  = GeoHash.withCharacterPrecision(
                          latitude,
                          longitude,
                          GEO_HASH_PRECISION);
          int precision = com.divroll.datafactory.GeoHash.calculateGeoHashPrecision(distance);
          String hashQuery = geohash.toBase32().substring(0, precision);
          EntityIterable tempEntities =
              referenceToScope.get().intersect(
                      txn.findStartingWith(entityType, propertyName, hashQuery));
          referenceToScope.set(tempEntities);
        } else {
          ((PersistentEntityStore) referenceToScope.get().getTransaction().getStore()).getConfig()
              .setCachingDisabled(true);
          EntityIterable tempEntities =
              referenceToScope.get().intersect(txn.findWithProp(entityType, propertyName));
          GeodeticCalculator geoCalc = new GeodeticCalculator();
          Ellipsoid reference = Ellipsoid.WGS84;
          GlobalPosition pointA = new GlobalPosition(latitude, longitude, 0.0);
          tempEntities.forEach(entity -> {
            GeoPoint geoReference = (GeoPoint) entity.getProperty(propertyName);
            GlobalPosition pointB =
                new GlobalPosition(geoReference.getLatitude(), geoReference.getLongitude(),
                    0.0);
            double instantaneousDistance
                    = geoCalc.calculateGeodeticCurve(
                            reference,
                            pointB,
                            pointA).getEllipsoidalDistance();
            if (distance < instantaneousDistance) {
              referenceToScope.set(referenceToScope.get().minus(txn.getSingletonIterable(entity)));
            }
          });
          ((PersistentEntityStore) referenceToScope.get().getTransaction().getStore()).getConfig()
              .setCachingDisabled(false);
        }
      } else if (entityCondition instanceof PropertyContainsCondition) {
        PropertyContainsCondition propertyContainsCondition =
            (PropertyContainsCondition) entityCondition;
        String propertyName = propertyContainsCondition.propertyName();
        Comparable containsValue = propertyContainsCondition.innerPropertyValue();
        EntityIterable entities =
            referenceToScope.get().intersect(txn.find(entityType, propertyName, containsValue));
        referenceToScope.set(entities);
      } else if (entityCondition instanceof PropertyStartsWithCondition) {
        PropertyStartsWithCondition startsWithCondition =
            (PropertyStartsWithCondition) entityCondition;
        String propertyName = startsWithCondition.propertyName();
        String startsWithValue = startsWithCondition.startsWith();
        EntityIterable entities =
            referenceToScope.get()
                .intersect(txn.findStartingWith(entityType, propertyName, startsWithValue));
        referenceToScope.set(entities);
      } else if (entityCondition instanceof BlobExistsCondition) {
        BlobExistsCondition blobExistsCondition = (BlobExistsCondition) entityCondition;
        String blobName = blobExistsCondition.blobName();
        EntityIterable entities =
            referenceToScope.get().intersect(txn.findWithBlob(entityType, blobName));
        referenceToScope.set(entities);
      } else if (entityCondition instanceof CustomQueryCondition) {
        CustomQueryCondition customQueryCondition = (CustomQueryCondition) entityCondition;
        referenceToScope.set(customQueryCondition.execute(referenceToScope.get()));
      }
    });
    return referenceToScope.get();
  }

  /**
   * Process conditions without modifying the {@linkplain Entity} with the main function throw a
   * {@linkplain UnsatisfiedConditionException} when condition is not met with the context of the
   * {@linkplain Entity}.
   *
   * @param scope           The AtomicReference to hold the EntityIterable being filtered.
   * @param conditions      The list of conditions to process.
   * @param entityInContext The entity to process the conditions on.
   * @param txn             The StoreTransaction to use for processing the conditions.
   * @throws UnsatisfiedConditionException
   */
  public static void processUnsatisfiedConditions(
      @NotNull final AtomicReference<EntityIterable> scope,
      @NotNull final List<EntityCondition> conditions,
      @NotNull final Entity entityInContext,
      @NotNull final StoreTransaction txn)
      throws UnsatisfiedConditionException {
    conditions.forEach(rethrow(entityCondition -> {
      try {
        UnsatisfiedConditionProcessor<?> processor
                = getUnsatisfiedConditionProcessor(entityCondition.getClass());
        if (processor != null) {
          processor.process(scope, entityCondition, entityInContext, txn);
        } else {
          throw new InvalidConditionException(entityCondition);
        }
      } catch (Exception e) {
        throw new UnsatisfiedConditionException(entityCondition, e);
      }
    }));
  }

  /**
   * Returns the UnsatisfiedConditionProcessor that can handle the given condition class.
   *
   * @param conditionClass The class of the condition to handle.
   * @return The UnsatisfiedConditionProcessor that can handle the given condition class,
   * or null if no processor is found.
   */
  private static UnsatisfiedConditionProcessor<?> getUnsatisfiedConditionProcessor(
          final Class<?> conditionClass) {
    for (UnsatisfiedConditionProcessor<?> processor : getUnsatisfiedConditionProcessors()) {
      if (processor.canProcess().isAssignableFrom(conditionClass)) {
        return processor;
      }
    }
    return null;
  }

  /**
   * Processes a list of actions on an entity.
   *
   * @param dataFactoryEntity The DataFactoryEntity associated with the entity.
   * @param reference        An AtomicReference to the EntityIterable used for reference.
   * @param entityInContext  The entity on which the actions are performed.
   * @param storeTransaction              The StoreTransaction associated with the entity.
   * @return The modified entityInContext after processing the actions.
   */
  public static Entity processActions(@NotNull final DataFactoryEntity dataFactoryEntity,
                                      @NotNull final AtomicReference<EntityIterable> reference,
                                      @NotNull final Entity entityInContext,
                                      @NotNull final StoreTransaction storeTransaction) {
    dataFactoryEntity.actions().forEach(
        rethrow(action -> {
          if (action instanceof LinkAction) {
            processLinkAction(action, entityInContext, storeTransaction);
          } else if (action instanceof OppositeLinkAction) {
            processOppositeLinkAction(action, entityInContext, storeTransaction);
          } else if (action instanceof LinkRemoveAction) {
            processLinkRemoveAction(action, entityInContext, storeTransaction);
          } else if (action instanceof OppositeLinkRemoveAction) {
            processOppositeLinkRemoveAction(action, entityInContext, storeTransaction);
          } else if (action instanceof LinkNewEntityAction) {
            processLinkNewEntityAction(action, entityInContext, storeTransaction);
          } else if (action instanceof BlobRenameAction) {
            processBlobRenameAction(action, entityInContext);
          } else if (action instanceof BlobRenameRegexAction) {
            processBlobRenameRegexAction(action, entityInContext);
          } else if (action instanceof BlobRemoveAction) {
            processBlobRemoveAction(action, entityInContext);
          } else if (action instanceof PropertyCopyAction) {
            processPropertyCopyAction(action, entityInContext, storeTransaction, reference);
          } else if (action instanceof PropertyIndexAction) {
            processPropertyIndexAction(
                    action,
                    entityInContext,
                    storeTransaction,
                    dataFactoryEntity);
          } else if (action instanceof PropertyRemoveAction) {
            processPropertyRemoveAction(action, entityInContext);
          } else if (action instanceof CustomAction) {
            processCustomAction(action, entityInContext);
          } else {
            throw new IllegalArgumentException("Invalid entity action");
          }
        }));
    return entityInContext;
  }

  /**
   * Processes the LinkAction on an entity.
   *
   * @param action             The LinkAction to process.
   * @param entityInContext    The entity on which the action is performed.
   * @param storeTransaction   The StoreTransaction associated with the entity.
   */
  private static void processLinkAction(final EntityAction action,
                                        final Entity entityInContext,
                                        final StoreTransaction storeTransaction) {
    LinkAction linkAction = (LinkAction) action;
    String targetId = linkAction.otherEntityId();
    String linkName = linkAction.linkName();
    Boolean isSet = linkAction.isSet();
    if (targetId != null && linkName != null) {
      EntityId targetEntityId = storeTransaction.toEntityId(targetId);
      Entity targetEntity = storeTransaction.getEntity(targetEntityId);
      if (isSet) {
        entityInContext.getLinks(linkName).forEach(otherEntity -> {
          entityInContext.deleteLink(linkName, otherEntity);
        });
        entityInContext.setLink(linkName, targetEntity);
      } else {
        entityInContext.addLink(linkName, targetEntity);
      }
    }
  }

  /**
   * Processes an OppositeLinkAction for a given entity in the context.
   *
   * @param action the OppositeLinkAction to be processed
   * @param entityInContext the entity in which the action is being performed
   * @param storeTransaction the transaction object for accessing the store
   */
  private static void processOppositeLinkAction(final EntityAction action,
                                                final Entity entityInContext,
                                                final StoreTransaction storeTransaction) {
    OppositeLinkAction oppositeLinkAction = (OppositeLinkAction) action;
    String linkName = oppositeLinkAction.linkName();
    String sourceId = oppositeLinkAction.oppositeEntityId();
    String oppositeLinkName = oppositeLinkAction.oppositeLinkName();
    Boolean isSet = oppositeLinkAction.isSet();
    if (sourceId != null && linkName != null && oppositeLinkName != null) {
      EntityId sourceEntityId = storeTransaction.toEntityId(sourceId);
      Entity sourceEntity = storeTransaction.getEntity(sourceEntityId);
      if (isSet) {
        Entity otherEntity = sourceEntity.getLink(oppositeLinkName);
        if (otherEntity != null) {
          otherEntity.deleteLink(linkName, sourceEntity);
        }
        sourceEntity.deleteLink(oppositeLinkName, otherEntity);
        sourceEntity.setLink(oppositeLinkName, entityInContext);
        entityInContext.setLink(linkName, sourceEntity);
      } else {
        sourceEntity.addLink(oppositeLinkName, entityInContext);
        entityInContext.addLink(linkName, sourceEntity);
      }
    }
  }

  /**
   * Processes a LinkRemoveAction on an entity.
   *
   * @param action          The LinkRemoveAction to process.
   * @param entityInContext The entity on which the action is performed.
   * @param storeTransaction The StoreTransaction associated with the entity.
   */
  private static void processLinkRemoveAction(final EntityAction action,
                                              final Entity entityInContext,
                                              final StoreTransaction storeTransaction) {
    LinkRemoveAction linkRemoveAction = (LinkRemoveAction) action;
    String targetId = linkRemoveAction.otherEntityId();
    String linkName = linkRemoveAction.linkName();
    EntityId targetEntityId = storeTransaction.toEntityId(targetId);
    Entity targetEntity = storeTransaction.getEntity(targetEntityId);
    entityInContext.deleteLink(linkName, targetEntity);
  }

  /**
   * Processes an OppositeLinkRemoveAction.
   *
   * @param action the OppositeLinkRemoveAction to be processed
   * @param entityInContext the entity on which the action is performed
   * @param storeTransaction the transaction to access the store and perform the actions
   */
  private static void processOppositeLinkRemoveAction(final EntityAction action,
                                                      final Entity entityInContext,
                                                      final StoreTransaction storeTransaction) {
    OppositeLinkRemoveAction removeAction = (OppositeLinkRemoveAction) action;
    String linkName = removeAction.linkName();
    String oppositeEntityType = removeAction.oppositeEntityType();
    String oppositeLinkName = removeAction.oppositeLinkName();
    entityInContext.getLinks(linkName)
            .intersect(
                    storeTransaction.findWithLinks(
                            entityInContext.getType(),
                            linkName,
                            oppositeEntityType,
                            oppositeLinkName))
            .forEach(linkedEntity -> {
              linkedEntity.deleteLink(oppositeLinkName, entityInContext);
              entityInContext.deleteLink(linkName, linkedEntity);
            });
  }

  /**
   * This method processes the LinkNewEntityAction.
   *
   * @param action The EntityAction to be processed.
   * @param entityInContext The Entity in context.
   * @param storeTransaction The StoreTransaction for performing database operations.
   */
  private static void processLinkNewEntityAction(final EntityAction action,
                                                 final Entity entityInContext,
                                                 final StoreTransaction storeTransaction) {
    LinkNewEntityAction newLinkAction = (LinkNewEntityAction) action;
    String linkName = newLinkAction.linkName();
    Boolean isSet = newLinkAction.isSet();
    DataFactoryEntity newDataFactoryEntity = newLinkAction.newEntity();
    Entity targetEntity = new Unmarshaller().with(newDataFactoryEntity, storeTransaction).build();
    if (isSet) {
      entityInContext.getLinks(linkName).forEach(otherEntity -> {
        entityInContext.deleteLink(linkName, otherEntity);
      });
      entityInContext.setLink(linkName, targetEntity);
    } else {
      entityInContext.addLink(linkName, targetEntity);
    }
  }

  /**
   * Handles BlobRenameAction.
   *
   * @param action         the EntityAction to be processed
   * @param entityInContext the entity in which the action is performed
   */
  private static void processBlobRenameAction(final EntityAction action,
                                              final Entity entityInContext) {
    BlobRenameAction blobRenameAction = (BlobRenameAction) action;
    String blobName = blobRenameAction.blobName();
    String newBlobName = blobRenameAction.newBlobName();
    InputStream oldBlob = entityInContext.getBlob(blobName);
    entityInContext.deleteBlob(blobName);
    entityInContext.setBlob(newBlobName, oldBlob);
  }

  /**
   * Processes the BlobRenameRegexAction by renaming the blobs that match the given regex pattern
   * within the entity's context.
   *
   * @param action        The BlobRenameRegexAction to be processed.
   * @param entityInContext The entity object within which the blobs will be renamed.
   */
  private static void processBlobRenameRegexAction(final EntityAction action,
                                                   final Entity entityInContext) {
    BlobRenameRegexAction blobRenameRegexAction = (BlobRenameRegexAction) action;
    String regexPattern = blobRenameRegexAction.regexPattern();
    String replacement = blobRenameRegexAction.replacement();
    entityInContext.getBlobNames().forEach(blobName -> {
      Pattern pattern = Pattern.compile(regexPattern);
      Matcher matcher = pattern.matcher(blobName);
      if (matcher.matches()) {
        InputStream blobStream = entityInContext.getBlob(blobName);
        entityInContext.deleteBlob(blobName);
        String newBlobName = matcher.replaceAll(replacement);
        entityInContext.setBlob(newBlobName, blobStream);
      }
    });
  }

  /**
   * Handles the BlobRemoveAction.
   *
   * @param action the EntityAction containing the BlobRemoveAction to be processed
   * @param entityInContext the Entity on which the BlobRemoveAction will be performed
   */
  private static void processBlobRemoveAction(final EntityAction action,
                                              final Entity entityInContext) {
    BlobRemoveAction blobRemoveAction = (BlobRemoveAction) action;
    blobRemoveAction.blobNames().forEach(entityInContext::deleteBlob);
  }

  /**
   * Processes PropertyCopyAction.
   *
   * @param action            the EntityAction to process
   * @param entityInContext   the Entity in context
   * @param storeTransaction the transaction for the entity store
   * @param reference         the reference to the EntityIterable object
   */
  private static void processPropertyCopyAction(final EntityAction action,
                                                final Entity entityInContext,
                                                final StoreTransaction storeTransaction,
                                                final AtomicReference<EntityIterable> reference) {
    PropertyCopyAction propertyCopyAction = (PropertyCopyAction) action;
    String copyProperty = propertyCopyAction.propertyName();
    Boolean copyFirst = propertyCopyAction.first();
    if (copyFirst) {
      Entity first = reference.get()
              .intersect(
                      storeTransaction.getAll(entityInContext.getType())
              ).getFirst();
      if (first != null) {
        entityInContext.setProperty(copyProperty, first.getProperty(copyProperty));
      }
    } else {
      Entity last = reference.get()
              .intersect(
                      storeTransaction.getAll(entityInContext.getType()))
              .getLast();
      if (last != null) {
        entityInContext.setProperty(copyProperty, last.getProperty(copyProperty));
      }
    }
  }

  /**
   * Handles PropertyIndexAction.
   *
   * @param action                  The EntityAction object.
   * @param entityInContext         The Entity object in context.
   * @param storeTransaction        The StoreTransaction object.
   * @param dataFactoryEntity       The DataFactoryEntity object.
   */
  private static void processPropertyIndexAction(final EntityAction action,
                                                 final Entity entityInContext,
                                                 final StoreTransaction storeTransaction,
                                                 final DataFactoryEntity dataFactoryEntity) {
    PropertyIndexAction propertyIndexAction = (PropertyIndexAction) action;
    String propertyName = propertyIndexAction.propertyName();
    String entityInContextType = entityInContext.getType();
    Comparable propertyValue = dataFactoryEntity.propertyMap().get(propertyName);
    EntityIterable result
            = storeTransaction.find(entityInContextType, propertyName, propertyValue);
    if (!result.isEmpty()) {
      throw new IllegalArgumentException("Unique property violation");
    }
  }

  /**
   * Handles the PropertyRemoveAction by removing a property from the given entity.
   *
   * @param action the PropertyRemoveAction to be processed
   * @param entityInContext the entity from which the property is to be removed
   */
  private static void processPropertyRemoveAction(final EntityAction action,
                                                  final Entity entityInContext) {
    PropertyRemoveAction propertyRemoveAction = (PropertyRemoveAction) action;
    String propertyName = propertyRemoveAction.propertyName();
    entityInContext.deleteProperty(propertyName);
  }

  /**
   * Performs custom action on the given entity.
   *
   * @param action The custom action to be performed.
   * @param entityInContext The entity on which the custom action needs to be performed.
   */
  private static void processCustomAction(final EntityAction action,
                                          final Entity entityInContext) {
    CustomAction customAction = (CustomAction) action;
    customAction.execute(entityInContext);
  }

  /**
   * Filters the given EntityIterable based on the provided TransactionFilters.
   *
   * @param reference         AtomicReference to hold the EntityIterable being filtered.
   * @param filters           List of TransactionFilters to apply.
   * @param entityType        Type of the entity being filtered.
   * @param storeTransaction  StoreTransaction to use for filtering.
   * @return The filtered EntityIterable.
   */
  public static EntityIterable filterContext(final AtomicReference<EntityIterable> reference,
                                             final List<TransactionFilter> filters,
                                             final String entityType,
                                             final StoreTransaction storeTransaction) {
    filters.forEach(transactionFilter -> {
      String propertyName = transactionFilter.propertyName();
      Comparable propertyValue = transactionFilter.propertyValue();

      if (EqualityOp.EQUAL.equals(transactionFilter.equalityOp())) {
        reference.set(
            reference.get()
                .intersect(storeTransaction.find(entityType, propertyName, propertyValue)));
      } else if (EqualityOp.NOT_EQUAL.equals(
          transactionFilter.equalityOp())) {
        reference.set(reference.get()
            .intersect(storeTransaction.getAll(entityType)
                .minus(storeTransaction.find(entityType, propertyName, propertyValue))));
      } else if (EqualityOp.STARTS_WITH.equals(
          transactionFilter.equalityOp())) {
        reference.set(reference.get()
            .intersect(storeTransaction.findStartingWith(entityType, propertyName,
                String.valueOf(propertyValue))));
      } else if (EqualityOp.NOT_STARTS_WITH.equals(
          transactionFilter.equalityOp())) {
        reference.set(reference.get()
            .intersect(storeTransaction.getAll(entityType)
                .minus(storeTransaction.findStartingWith(entityType, propertyName,
                    String.valueOf(propertyValue)))));
      } else if (EqualityOp.IN_RANGE.equals(
          transactionFilter.equalityOp())) {
        throw new IllegalArgumentException(
            "Not yet implemented: "
                + EqualityOp.IN_RANGE.toString());
      } else if (EqualityOp.CONTAINS.equals(
          transactionFilter.equalityOp())) {
        throw new IllegalArgumentException(
            "Not yet implemented: "
                + EqualityOp.CONTAINS.toString());
      }
    });
    return reference.get();
  }

  /**
   * Gets the list of UnsatisfiedConditionProcessors.
   *
   * @return The list of UnsatisfiedConditionProcessors.
   */
  private static List<UnsatisfiedConditionProcessor<?>> getUnsatisfiedConditionProcessors() {
    List<UnsatisfiedConditionProcessor<?>> entityConditionProcessors = new ArrayList<>();
    try {
      for (Class<?> processorClass : EntityConditionProcessors.getEntityProcessorClasses()) {
        UnsatisfiedConditionProcessor<?> processor = instantiateProcessor(processorClass);
        entityConditionProcessors.add(processor);
      }
    } catch (Exception e) {
      throw new DataFactoryException("Error getting entity condition processors", e);
    }
    return entityConditionProcessors;
  }

  /**
   * Instantiates a processor of type UnsatisfiedConditionProcessor.
   *
   * @param processorClass The class object of the processor.
   * @return An instance of the UnsatisfiedConditionProcessor.
   * @throws ReflectiveOperationException If the instantiation fails.
   */
  private static UnsatisfiedConditionProcessor<?> instantiateProcessor(
          final Class<?> processorClass
  ) throws ReflectiveOperationException {
    Constructor<?> constructor = processorClass.getDeclaredConstructor();
    return (UnsatisfiedConditionProcessor<?>) constructor.newInstance();
  }

  /**
   * Finds the entity in the current scope based on the given DataFactoryEntity.
   *
   * @param entity  The DataFactoryEntity to find in the current scope.
   * @param scope   The AtomicReference to hold the scoped entities.
   * @param txn     The StoreTransaction to use for finding the entity.
   */
  private static void findEntityInCurrentScope(@NotNull final DataFactoryEntity entity,
                                               @NotNull final AtomicReference<EntityIterable> scope,
                                               @NotNull final StoreTransaction txn) {
    if (entity.nameSpace() != null && entity.entityType() != null) {
      scope.set(
              txn.getAll(entity.entityType())
                      .intersect(
                              txn.find(entity.entityType(), Constants.NAMESPACE_PROPERTY,
                                      entity.nameSpace())));
    } else if (entity.entityType() != null) {
      scope.set(txn.getAll(entity.entityType()));
    }
  }

  /**
   * Gets the Entity from the given DataFactoryEntity and StoreTransaction.
   *
   * @param entity The DataFactoryEntity to get the Entity from.
   * @param txn    The StoreTransaction to use for getting the Entity.
   * @return The Entity from the given DataFactoryEntity and StoreTransaction.
   */
  private static Entity getEntityFromTransaction(@NotNull final DataFactoryEntity entity,
                                                 @NotNull final StoreTransaction txn) {
    return entity.entityId() != null
            ? txn.getEntity(txn.toEntityId(entity.entityId()))
            : txn.newEntity(entity.entityType());
  }

  /**
   * Throws an IllegalArgumentException if the entity is not found in the scoped namespace.
   *
   * @param entity           The DataFactoryEntity to check.
   * @param entityInContext  The Entity to check.
   * @param entityIterable   The EntityIterable to check.
   */
  private static void throwIfEntityNotFoundInScopedNamespace(final DataFactoryEntity entity,
                                                             final Entity entityInContext,
                                                             final EntityIterable entityIterable) {
    if (entityIterable != null
            && entityIterable.indexOf(entityInContext) == -1
            && entity.nameSpace() != null) {
      throw new IllegalArgumentException(
              "Entity " + entity.entityId() + " not found in namespace " + entity.nameSpace());
    }
  }
}
