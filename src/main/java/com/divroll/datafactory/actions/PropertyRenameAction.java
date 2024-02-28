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

import javax.annotation.Nullable;
import org.immutables.value.Value;

/**
 * PropertyRenameAction represents an action to rename a property for an entity.
 *
 * @author <a href="mailto:kerby@divroll.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
@Value.Immutable
public interface PropertyRenameAction extends EntityPropertyAction {
  /**
   * The name of the property to be renamed.
   *
   * @return The name of the property.
   */
  String propertyName();

  /**
   * The new name of the property.
   *
   * @return The new name of the property.
   */
  String newPropertyName();

  /**
   * Indicates the rename operation will overwrite the existing property
   * with the new property name.
   *
   * @return True if the existing property will be overwritten,
   * false otherwise
   */
  @Nullable
  @Value.Default
  default Boolean overwrite() {
    return false;
  }
}
