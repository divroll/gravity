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
package com.divroll.datafactory.builders.queries;

import com.divroll.datafactory.Constants;
import com.divroll.datafactory.builders.DataFactoryEntity;
import com.divroll.datafactory.builders.TransactionFilter;
import com.divroll.datafactory.conditions.EntityCondition;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.immutables.value.Value;

/**
 * Represents a query to retrieve an entity.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface EntityQuery extends Serializable {
  /**
   * The name of the environment to query
   *
   * @return The name of the environment
   */
  @Value.Default
  default String environment() {
    return System.getProperty(Constants.DATAFACTORY_DIRECTORY_ENVIRONMENT);
  }

  /**
   * The name of the namespace to query
   *
   * @return The name of the namespace
   */
  @Nullable
  String nameSpace();

  /**
   * The name of the entity type to query
   *
   * @return The name of the entity type
   */
  @Nullable
  String entityType();

  /**
   * The id of the entity to query
   *
   * @return The id of the entity
   */
  @Nullable
  String entityId();

  /**
   * Indicates the {@code linkNames} to use in the query
   *
   * @return names of the links of {@linkplain DataFactoryEntity} to query
   */
  @Nullable
  @Value.Default
  default List<LinkQuery> linkQueries() {
    return new ArrayList<>();
  }

  /**
   * Indicates the {@code blobNames} to use in the query
   *
   * @return names of the blobs of {@linkplain DataFactoryEntity} to query
   */
  @Nullable
  @Value.Default
  default List<BlobQuery> blobQueries() {
    return new ArrayList<>();
  }

  /**
   * Indicates the query should return only the first entity found
   *
   * @return True if the query should return only the first entity found, false otherwise
   */
  @Nullable
  @Value.Default
  default Boolean first() {
    return false;
  }

  /**
   * Indicates the query should return only the last entity found
   *
   * @return True if the query should return only the last entity found, false otherwise
   */
  @Nullable
  @Value.Default
  default Boolean last() {
    return false;
  }

  /**
   * Retrieves the list of transaction filters specified for the EntityQuery.
   *
   * @return The list of transaction filters specified for the EntityQuery
   *
   * @deprecated This method is deprecated and may be removed in future versions.
   */
  @Deprecated
  @Nullable
  @Value.Default
  default List<TransactionFilter> filters() {
    return new ArrayList<>();
  }

  /**
   * Retrieves the conditions specified for the EntityQuery.
   *
   * @return The list of EntityConditions specified for the EntityQuery
   */
  @Nullable
  @Value.Default
  default List<EntityCondition> conditions() {
    return new ArrayList<>();
  }

  /**
   * Returns the offset value for the query.
   *
   * @return The offset value for the query. Default value is 0 if not specified.
   */
  @Value.Default
  default Integer offset() {
    return 0;
  }

  /**
   * Indicates the query should return a maximum number of entities
   *
   * @return The maximum number of entities to return
   */
  @Value.Default
  default Integer max() {
    return 100;
  }

  /**
   * Indicates the query should return the entities in a specific order
   *
   * @return The order in which the query should return the entities
   */
  @Nullable
  String sort();

  /**
   * Indicates the query should return the entities in ascending order
   *
   * @return True if the query should return the entities in ascending order, false otherwise
   */
  @Nullable
  @Value.Default
  default Boolean sortAscending() {
    return true;
  }
}
