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
package com.divroll.datafactory.builders;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.immutables.value.Value;

/**
 * EntityTypes is an interface that represents a collection of Entity Types in a
 * Data Factory. It provides methods to retrieve the list of entity types and the count of
 * entities.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface EntityTypes {
  /**
   * Returns the list of entity types.
   *
   * @return the list of entity types as a List<EntityType>.
   */
  @Value.Default
  default List<EntityType> entityTypes() {
    return new ArrayList<>();
  }

  /**
   * Retrieves the count of entities.
   *
   * @return the count of entities as a Long, or null if not available
   */
  @Nullable
  @Value.Default
  default Long entityCount() {
    return -1L;
  }
}
