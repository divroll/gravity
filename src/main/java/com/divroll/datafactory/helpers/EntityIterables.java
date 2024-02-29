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
package com.divroll.datafactory.helpers;

import com.divroll.datafactory.properties.EmbeddedArrayIterable;
import com.divroll.datafactory.properties.EmbeddedEntityIterable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import util.ComparableHashMap;
import util.ComparableLinkedList;

/**
 * Utility class for working with different types of entity iterables.
 */
@SuppressWarnings("unchecked")
public final class EntityIterables {
  /**
   * This is a utility class and cannot be instantiated.
   */
  private EntityIterables() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  /**
   * Casts the given EmbeddedEntityIterable to a Map<String, Comparable>.
   *
   * @param embeddedEntityIterable the EmbeddedEntityIterable to be casted
   * @return a Map<String, Comparable> representation of the EmbeddedEntityIterable,
   *         or null if the input is null
   */
  public static Map<String, Comparable> cast(final EmbeddedEntityIterable embeddedEntityIterable) {
    if (embeddedEntityIterable == null) {
      return null;
    }
    Map<String, Comparable> comparableMap = new HashMap<>();
    return comparableMap;
  }

  /**
   * Converts a ComparableLinkedList to an EmbeddedArrayIterable.
   *
   * @param comparableList the ComparableLinkedList to convert
   * @return the EmbeddedArrayIterable created from the comparableList
   */
  @SuppressWarnings("unchecked")
  public static EmbeddedArrayIterable toEmbeddedArrayIterable(
      final ComparableLinkedList<Comparable> comparableList) {
    final EmbeddedArrayIterable[] entityIterable = {null};
    List<Comparable> comparables = new ComparableLinkedList<>();
    comparableList.forEach(comparable -> {
      Comparable value = comparable;
      if (comparable instanceof ComparableHashMap) {
        value = toEmbeddedEntityIterable((ComparableHashMap<String, Comparable>) comparable);
        comparables.add(value);
      } else if (comparable instanceof ComparableLinkedList) {
        value = toEmbeddedArrayIterable((ComparableLinkedList<Comparable>) comparable);
        comparables.add(value);
      } else if (comparable instanceof String
          || comparable instanceof Number
          || comparable instanceof Boolean) {
        comparables.add(value);
      }
    });
    if (!comparables.isEmpty()) {
      entityIterable[0] = new EmbeddedArrayIterable(comparables);
    }
    return entityIterable[0];
  }

  /**
   * Converts a ComparableHashMap to an EmbeddedEntityIterable.
   *
   * @param comparableMap the ComparableHashMap to convert
   * @return the converted EmbeddedEntityIterable
   */
  @SuppressWarnings("unchecked")
  public static EmbeddedEntityIterable toEmbeddedEntityIterable(
      final ComparableHashMap<String, Comparable> comparableMap) {
    final EmbeddedEntityIterable[] entityIterable = {null};
    ComparableHashMap<String, Comparable> valueMap = new ComparableHashMap<>();
    comparableMap.forEach((propertyName, comparable) -> {
      Comparable value = comparable;
      if (comparable instanceof ComparableHashMap) {
        value = toEmbeddedEntityIterable((ComparableHashMap<String, Comparable>) comparable);
      } else if (comparable instanceof ComparableLinkedList) {
        value = toEmbeddedArrayIterable((ComparableLinkedList<Comparable>) comparable);
      }
      valueMap.put(propertyName, value);
    });
    if (!valueMap.isEmpty()) {
      entityIterable[0] = new EmbeddedEntityIterable(Comparables.cast(comparableMap));
    }
    return entityIterable[0];
  }

  /**
   * Builds an EntityIterable from a list of Entity objects.
   *
   * @param entities the list of Entity objects
   * @return the EntityIterable
   * @throws IllegalArgumentException if the method is not yet implemented
   */
  public static EntityIterable build(final List<Entity> entities) {
    throw new IllegalArgumentException("Not yet implemented");
  }
}
