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
package util;

import java.util.LinkedList;
import org.jetbrains.annotations.NotNull;

/**
 * This class is an extension of the LinkedList class and implements the
 * Comparable interface.It can be used to store a list of comparable objects
 * and provides the ability to compare the elements in the list.
 *
 * @param <Comparable> the type of elements in this list
 */
public class ComparableLinkedList<Comparable> extends LinkedList<Comparable>
    implements java.lang.Comparable {
  /**
   * Compares this object with the specified object for order.
   * Returns a negative integer, zero, or a positive integer as this
   * object is less than, equal to, or greater than the
   * specified object.
   *
   * @param o the object to be compared.
   * @return a negative integer, zero, or a positive integer as this
   * object is less than, equal to, or greater than the specified object.
   */
  @Override
  public int compareTo(@NotNull final Object o) {
    return 0;
  }
}
