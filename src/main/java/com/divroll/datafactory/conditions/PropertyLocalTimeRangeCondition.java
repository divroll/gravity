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

import java.time.LocalTime;
import org.immutables.value.Value;

/**
 * PropertyLocalTimeRangeCondition is an interface that represents a condition for querying entities based on a local time range.
 * It extends the EntityCondition interface and provides methods for retrieving the name of the property, as well as the upper and lower bounds of the time range condition.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface PropertyLocalTimeRangeCondition extends EntityCondition {
  /**
   * Gets the name of the property.
   *
   * @return The name of the property.
   */
  String propertyName();
  /**
   * Returns the upper bound of the time range for a property value.
   *
   * @return The upper bound of the time range.
   */
  LocalTime upper();
  /**
   * Returns the lower bound of the time range condition.
   *
   * @return The lower bound of the time range condition.
   */
  LocalTime lower();
}
