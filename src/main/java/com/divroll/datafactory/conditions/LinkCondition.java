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
 * LinkCondition is an interface that represents a condition for querying links between entities.
 * It extends the EntityCondition interface and provides methods for retrieving the link name and the other entity id associated with the link condition.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface LinkCondition extends EntityCondition {
  /**
   * Retrieves the link name associated with this link condition.
   *
   * @return The link name.
   */
  String linkName();

  /**
   * Returns the other entity id of the LinkCondition.
   *
   * @return The other entity id.
   */
  String otherEntityId();

  /**
   * Checks if the value is set.
   *
   * @return Returns true if the value is set, false otherwise.
   */
  @Nullable
  @Value.Default
  default Boolean isSet() {
    return false;
  }
}
