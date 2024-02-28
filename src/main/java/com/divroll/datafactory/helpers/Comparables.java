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
import java.util.List;
import java.util.Map;
import util.ComparableHashMap;
import util.ComparableLinkedList;

/**
 * This class provides static methods for casting generic collections to comparable collections.
 */
public class Comparables {
  /**
   * Casts a List of elements to a ComparableLinkedList of Comparable elements.
   * Elements in the input List may be of any type that extends Comparable.
   *
   * @param comparableList the List to be casted
   * @return a ComparableLinkedList with elements casted from the input List
   */
  public static <V extends Comparable> ComparableLinkedList<Comparable> cast(
      List<V> comparableList) {
    if (comparableList == null) {
      return null;
    }
    if (comparableList.isEmpty()) {
      return new ComparableLinkedList<Comparable>();
    }
    ComparableLinkedList<Comparable> casted = new ComparableLinkedList<Comparable>();
    comparableList.forEach(
        comparable -> {
          if (comparable instanceof EmbeddedEntityIterable) {
            casted.add(((EmbeddedEntityIterable) comparable).asObject());
          } else if (comparable instanceof EmbeddedArrayIterable) {
            casted.add(cast(((EmbeddedArrayIterable) comparable).asObject()));
          } else {
            casted.add(comparable);
          }
        });
    return casted;
  }

  /**
   * This method casts a regular Map to a ComparableHashMap.
   *
   * @param <K> the type of keys maintained by the input map;
   *            must extend Comparable
   * @param <V> the type of mapped values of the input map;
   *            must extend Comparable
   * @param map the map to be casted
   * @return a ComparableHashMap object with the same key-value pairs as the input map,
   *         or null if the input map is null
   */
  public static <K extends Comparable, V extends Comparable> ComparableHashMap<K, V> cast(
      Map<K, V> map) {
    if (map == null) {
      return null;
    }
    if (map.isEmpty()) {
      return new ComparableHashMap<>();
    }
    ComparableHashMap<K, V> casted = new ComparableHashMap<>();
    map.forEach(
        (key, value) -> {
          casted.put(key, value);
        });
    return casted;
  }
}
