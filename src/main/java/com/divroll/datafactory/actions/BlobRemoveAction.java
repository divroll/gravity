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

import java.io.Serializable;
import java.util.List;
import org.immutables.value.Value;

/**
 * Immutable class representing a BlobRemoveAction.
 * A BlobRemoveAction is an {@linkplain EntityAction} that contains
 * a list of blob names to be removed.
 */
@Value.Immutable
public interface BlobRemoveAction extends EntityAction, Serializable {
  /**
   * Returns a list of blob names.
   *
   * @return A list of blob names.
   */
  List<String> blobNames();
}
