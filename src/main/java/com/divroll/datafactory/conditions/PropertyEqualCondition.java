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
 * PropertyEqualCondition is an interface that represents a condition for querying entities based
 * on a specific property and its value. It extends the EntityCondition interface
 * and provides methods for retrieving the name of the property and the property value.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface PropertyEqualCondition extends EntityCondition {
  /**
   * Retrieves the name of the property.
   *
   * @return The name of the property.
   */
  String propertyName();
  /**
   * Returns the value of the property for the PropertyEqualCondition instance.
   *
   * @return The property value.
   */
  Comparable propertyValue();
}
