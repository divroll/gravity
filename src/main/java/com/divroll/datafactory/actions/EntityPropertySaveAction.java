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
package com.divroll.datafactory.actions;

import org.immutables.value.Value;

/**
 * EntityPropertySaveAction is an interface that represents an action
 * on an entity property. It extends the Serializable interface.
 */
@Value.Immutable
public interface EntityPropertySaveAction extends EntityPropertyAction {
  /**
   * Retrieves the name of the entity property.
   *
   * @return The name of the entity property.
   */
  String propertyName();

  /**
   * Retrieves the value of the entity property.
   *
   * @return The value of the entity property.
   */
  Comparable propertyValue();

  /**
   * Retrieves the overwrite flag of the entity property.
   *
   * @return The overwrite flag of the entity property.
   */
  @Value.Default
  default Boolean overwrite() {
    return true;
  }
}
