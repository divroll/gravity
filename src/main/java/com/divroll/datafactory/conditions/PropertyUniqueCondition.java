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
 * PropertyUniqueCondition is an interface representing a condition that checks if a property
 * in an entity has a unique value. It extends the EntityCondition interface and provides
 * the name of the property and its corresponding value to be checked.
 *
 * @see EntityCondition
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface PropertyUniqueCondition extends EntityCondition {
  /**
   * Returns the name of the property associated with this condition.
   *
   * @return The name of the property.
   */
  String propertyName();
  /**
   * Retrieves the value of the property.
   *
   * @return the value of the property
   */
  Comparable propertyValue();
}
