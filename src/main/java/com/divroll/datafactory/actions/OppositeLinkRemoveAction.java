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
 * The OppositeLinkRemoveAction interface represents an action of removing an
 * opposite link in an entity.
 */
@Value.Immutable
public interface OppositeLinkRemoveAction extends EntityAction, Serializable {
  /**
   * Returns the name of the link.
   * @return The name of the link.
   */
  String linkName();

  /**
   * Returns the name of the opposite link.
   * @return The name of the opposite link.
   */
  String oppositeLinkName();

  /**
   * Returns the opposite entity type of the OppositeLinkRemoveAction.
   *
   * @return The opposite entity type.
   */
  String oppositeEntityType();

  /**
   * Returns a default list of EntityPropertyAction objects.
   *
   * @return The default list of EntityPropertyAction objects.
   */
  @Value.Default
  default List<EntityPropertyAction> otherEntityPropertyActions() {
    return new ArrayList<>();
  }
}
