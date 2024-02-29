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
import com.divroll.datafactory.properties.EmbeddedArrayIterable;
import com.divroll.datafactory.properties.EmbeddedEntityIterable;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import util.ComparableLinkedList;
import util.CustomHashMap;

public abstract class StoreBaseImpl<T> extends UnicastRemoteObject {
  /**
   * Logger for capturing events, errors, and informational messages within
   * the {@code StoreBaseImpl} class. Utilizes the {@link LoggerFactory} to create the
   * logger instance specific to this class.
   */
  private static final Logger LOG = LoggerFactory.getLogger(StoreBaseImpl.class);


  /**
   * StoreBaseImpl is an abstract class that provides a base implementation for a store.
   *
   * @throws RemoteException If a remote communication error occurs.
   */
  public StoreBaseImpl() throws RemoteException {
  }

  /**
   * Removes duplicates from a ComparableLinkedList.
   *
   * @param list the ComparableLinkedList from which to remove duplicates
   * @param <T> the type of the elements in the list
   */
  public static <T> void removeDuplicates(final ComparableLinkedList<T> list) {
    int size = list.size();
    int out = 0;

    Set<T> encountered = new HashSet<>();
    for (int in = 0; in < size; in++) {
      final T t = list.get(in);
      final boolean first = encountered.add(t);
      if (first) {
        list.set(out++, t);
      }
    }

    while (out < size) {
      list.remove(--size);
    }
  }

  /**
   * This method removes duplicate elements from the given ArrayList.
   *
   * @param list the ArrayList from which duplicates are to be removed
   * @param <T>  the type of elements in the ArrayList
   */
  public static <T> void removeDuplicates(final ArrayList<T> list) {
    int size = list.size();
    int out = 0;

    final Set<T> encountered = new HashSet<T>();
    for (int in = 0; in < size; in++) {
      final T t = list.get(in);
      final boolean first = encountered.add(t);
      if (first) {
        list.set(out++, t);
      }
    }

    while (out < size) {
      list.remove(--size);
    }
  }

  /**
   * Converts an Entity object into a CustomHashMap containing String keys and Comparable values.
   *
   * @param entity the Entity object to convert
   * @return a CustomHashMap containing the properties of the Entity object
   */
  protected static CustomHashMap<String, Comparable> entityToMap(
      final jetbrains.exodus.entitystore.Entity entity) {
    CustomHashMap<String, Comparable> comparableMap = new CustomHashMap<>();
    for (String property : entity.getPropertyNames()) {
      Comparable value = entity.getProperty(property);
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
    return comparableMap;
  }

  /**
   * Retrieves the ISO date and time string in the UTC timezone.
   *
   * @return The ISO date and time string in the format "yyyy-MM-dd'T'HH:mm'Z'".
   */
  protected String getISODate() {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df =
        new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
    df.setTimeZone(tz);
    return df.format(new Date());
  }

  /**
   * Retrieves the value of the system property representing the data factory directory.
   *
   * @return the data factory directory system property value
   */
  protected String dataFactoryDir() {
    return System.getProperty(Constants.DATAFACTORY_DIRECTORY_ENVIRONMENT);
  }
}
