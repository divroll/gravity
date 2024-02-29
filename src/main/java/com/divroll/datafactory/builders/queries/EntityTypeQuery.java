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
package com.divroll.datafactory.builders.queries;

import com.divroll.datafactory.Constants;
import java.io.Serializable;
import javax.annotation.Nullable;
import org.immutables.value.Value;

/**
 * Represents a query to retrieve an entity type.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface EntityTypeQuery extends Serializable {
  /**
   * Returns the value of the environment attribute.
   * If not set, this attribute will have a default value as returned by the initializer
   * of environment.
   *
   * @return The value of the environment attribute
   */
  @Value.Default
  default String environment() {
    return System.getProperty(Constants.DATAFACTORY_DIRECTORY_ENVIRONMENT);
  }

  /**
   * Returns the value of the entity type attribute.
   *
   * @return The entity type, or null if not set.
   */
  @Nullable
  String entityType();

  /**
   * Returns the value of the count attribute.
   * If not set, this attribute will have a default value as returned by the initializer of count.
   *
   * @return The value of the count attribute, or null if not set.
   */
  @Nullable
  @Value.Default
  default Boolean count() {
    return true;
  }
}
