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

import java.io.Serializable;
import javax.annotation.Nullable;

import com.divroll.datafactory.builders.Entity;
import org.immutables.value.Value;

/**
 * Represents a query to retrieve a link.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface LinkQuery extends Serializable {
  /**
   * Retrieves the link name of a {@link LinkQuery} instance.
   *
   * @return The link name
   */
  String linkName();

  /**
   * Indicates to include the
   * {@linkplain Entity} body in the query response.
   *
   * @return  True if the linked entity body will be included in the query response,
   *          false otherwise
   */
  @Nullable
  @Value.Default
  default Boolean include() {
    return false;
  }

  /**
   * Retrieves the target entity ID of the LinkQuery.
   *
   * @return The target entity ID, or null if not set
   */
  @Nullable
  String targetEntityId();
}
