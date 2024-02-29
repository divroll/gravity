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
package com.divroll.datafactory.conditions;

import org.immutables.value.Value;

/**
 * PropertyMinMaxCondition is an interface that represents a condition for querying entities
 * with a specific property that falls within a specified minimum and maximum value range
 *.
 * It extends the EntityCondition interface and provides methods for retrieving the property name,
 * minimum value, and maximum value of the condition.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface PropertyMinMaxCondition extends EntityCondition {
  /**
   * Retrieves the name of the property.
   *
   * @return The name of the property as a string.
   */
  String propertyName();
  /**
   * Returns the minimum value of the PropertyMinMaxCondition.
   *
   * @return the minimum value
   */
  Comparable minValue();
  /**
   * Returns the maximum value.
   *
   * @return the maximum value
   */
  Comparable maxValue();
}
