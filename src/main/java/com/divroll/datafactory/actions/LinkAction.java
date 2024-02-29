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
package com.divroll.datafactory.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.immutables.value.Value;

/**
 * Represents an action to link an entity to another entity.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface LinkAction extends EntityAction, Serializable {
  /**
   * Returns the name of the link.
   * @return The name of the link.
   */
  String linkName();

  /**
   * Returns the ID of the other entity involved in the link action.
   *
   * @return The ID of the other entity.
   */
  String otherEntityId();

  /**
   * Returns whether the link is set or not.
   *
   * @return True if the link is set, false otherwise.
   */
  Boolean isSet();

  /**
   * Returns whether the link is removed or not.
   *
   * @return True if the link is removed, false otherwise.
   */
  @Value.Default
  default Boolean isRemove() {
    return false;
  }

  /**
   * Returns a list of other entity property actions.
   *
   * @return A list of other entity property actions.
   */
  @Value.Default
  default List<EntityPropertyAction> otherEntityPropertyActions() {
    return new ArrayList<>();
  }
}
