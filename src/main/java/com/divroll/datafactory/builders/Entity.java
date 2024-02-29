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
package com.divroll.datafactory.builders;

import com.divroll.datafactory.Constants;
import com.divroll.datafactory.actions.EntityAction;
import com.divroll.datafactory.conditions.EntityCondition;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity represents an entity in the data factory system.
 * It contains information such as the entity type, ID, namespace, property map, blobs,
 * links, and more. This is an immutable interface, which means the attributes cannot be modified
 * once set. Use the EntityBuilder to construct instances of this interface.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface Entity extends Serializable {

  /**
   * Checks if the Entity has at least either an Entity Type or ID.
   *
   * @return The Entity object.
   * @throws IllegalStateException if both the Entity Type and ID are null or empty.
   */
  @Value.Check
  default Entity check() {
    Preconditions.checkState(
        !((entityType() == null
                || entityType().isEmpty()) && (entityId() == null
                || entityId().isEmpty())),
            "Should have at least either an Entity Type or ID");
    return this;
  }

  /**
   * Returns the default value for the "environment" attribute.
   * This method retrieves the value of the system property "datafactory.dir".
   *
   * @return The default value for the "environment" attribute.
   */
  @Value.Default
  default String environment() {
    return System.getProperty(Constants.DATAFACTORY_DIRECTORY_ENVIRONMENT);
  }

  /**
   * Retrieves the entity type of a Entity.
   *
   * @return The entity type as a String. Returns null if no entity type is set.
   */
  @Nullable
  String entityType();

  /**
   * Retrieves the namespace of a Entity.
   *
   * @return The namespace as a String. Returns null if no namespace is set.
   */
  @Nullable
  String nameSpace();

  /**
   * Retrieves the ID of the entity.
   *
   * @return The ID of the entity as a String. Returns null if no ID is set.
   */
  @Nullable
  String entityId();

  /**
   * Returns the property map of a Entity.
   *
   * @return The property map as a {@code Map<String, Comparable>} object.
   * Returns a new empty {@code LinkedHashMap} if no properties are set.
   * The returned map is not null.
   */
  @Value.Default
  default Map<String, Comparable> propertyMap() {
    return new LinkedHashMap<>();
  }

  /**
   * Retrieves the list of blobs associated with the Entity.
   *
   * @return The list of Blob objects. Returns an empty list if no blobs are associated
   * with the entity. The returned list is nullable.
   */
  @Nullable
  @Value.Default
  default List<Blob> blobs() {
    return new ArrayList<>();
  }

  /**
   * Retrieves the links associated with the Entity.
   *
   * @return The Multimap of links, where the key is a String and the value is a Entity.
   * Returns an empty Multimap if no links are associated with the entity.
   * The returned Multimap is nullable.
   */
  @Nullable
  @Value.Default
  default Multimap<String, Entity> links() {
    return ArrayListMultimap.create();
  }

  /**
   * Retrieves the array of blob names associated with the Entity.
   *
   * @return The array of blob names as a String array.
   *         The array is nullable and returns null if no blob names are associated with the entity.
   */
  @Nullable
  @Value.Default
  default String[] blobNames() {
    return null;
  }

  /**
   * Retrieves the array of link names associated with the Entity.
   *
   * @return The array of link names as a String array.
   *         The array is nullable and returns null if no link names are associated with the entity.
   */
  @Nullable
  @Value.Default
  default String[] linkNames() {
    return null;
  }

  /**
   * Retrieves the list of actions performed on the Entity.
   *
   * @return The list of EntityAction objects as a List.
   *         The list is nullable and returns a new empty ArrayList if no actions
   *         are performed on the entity.
   */
  @Nullable
  @Value.Default
  default List<EntityAction> actions() {
    return new ArrayList<>();
  }

  /**
   * Retrieves the list of transaction filters applied to the Entity.
   *
   * @return  The list of TransactionFilter objects as a List.
   *          The list is nullable and returns a new empty ArrayList if no filters
   *          are applied.
   */
  @Nullable
  @Value.Default
  default List<TransactionFilter> filters() {
    return new ArrayList<>();
  }

  /**
   * Retrieves the list of conditions applied to the Entity.
   *
   * @return The list of EntityConditions as a List of EntityCondition objects.
   *         This method returns a new empty ArrayList if no conditions are applied.
   *         The returned list is nullable.
   */
  @Nullable
  @Value.Default
  default List<EntityCondition> conditions() {
    return new ArrayList<>();
  }
}
