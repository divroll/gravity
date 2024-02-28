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
 * OppositeLinkCondition is an interface that represents a condition for querying entities based on opposite link attributes.
 * It extends the EntityCondition interface and provides methods for retrieving the link name, opposite link name, opposite entity ID, and whether the isSet attribute is set.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface OppositeLinkCondition extends EntityCondition {
  /**
   * Returns the link name.
   *
   * @return The link name.
   */
  String linkName();

  /**
   * Returns the opposite link name.
   *
   * @return the opposite link name
   */
  String oppositeLinkName();

  /**
   * Retrieves the opposite entity ID from the {@link OppositeLinkCondition} instance.
   *
   * @return The opposite entity ID as a string.
   */
  String oppositeEntityId();

  /**
   * Returns whether the value of the isSet attribute is set.
   *
   * @return the value of the isSet attribute, or null if it is not set
   */
  @Nullable
  @Value.Default
  default Boolean isSet() {
    return false;
  }
}
