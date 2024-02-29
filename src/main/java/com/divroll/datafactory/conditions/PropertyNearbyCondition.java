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

import javax.annotation.Nullable;
import org.immutables.value.Value;

/**
 * The PropertyNearbyCondition class represents a condition for querying nearby properties.
 * It extends the EntityCondition interface and provides methods for retrieving the property name,
 * latitude, longitude, distance, and usesGeoHash attribute.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface PropertyNearbyCondition extends EntityCondition {
  /**
   * Returns the propertyName.
   *
   * @return The propertyName.
   */
  String propertyName();
  /**
   * Retrieves the longitude value from the PropertyNearbyCondition instance.
   *
   * @return The longitude value as a Double.
   */
  Double longitude();
  /**
   * Returns the latitude value.
   *
   * @return The latitude value as a Double.
   */
  Double latitude();
  /**
   * Calculates the distance.
   *
   * @return The distance as a Double.
   */
  Double distance();
  /**
   * Returns the value of the useGeoHash attribute.
   *
   * @return the useGeoHash attribute value or null if not set
   */
  @Nullable
  @Value.Default
  default Boolean useGeoHash() {
    return false;
  }
}
