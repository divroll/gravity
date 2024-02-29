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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.immutables.value.Value;

/**
 * Represents a query to retrieve a list of entities.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface Entities extends Serializable {
  /**
   *
   * Returns a list of Entity objects.
   *
   * @return the list of Entity objects
   */
  @Value.Default
  default List<Entity> entities() {
    return new ArrayList<>();
  }
  /**
   * Returns the offset value.
   *
   * @return the offset value, or null if not set
   */
  @Nullable
  Integer offset();
  /**
   * Returns the maximum value for the 'max' attribute in the Entities class.
   *
   * @return the maximum value for the 'max' attribute, or null if not set
   */
  @Nullable
  Integer max();
  /**
   * Retrieves the count value.
   *
   * @return the count, or null if not set
   */
  @Nullable
  Long count();
  /**
   * Sorts the data using the specified sorting criteria.
   *
   * @return the sorting criteria, or null if not set
   */
  @Nullable
  String sort();
  /**
   * Checks whether the data is sorted in ascending order based on the sorting criteria.
   *
   * @return  {@code true} if the data is sorted in ascending order,
   *          {@code false} if the data is not sorted or sorted in descending order, or
   *          {@code null} if the sorting criteria is not set
   */
  @Nullable
  Boolean isAscendingSort();
}
