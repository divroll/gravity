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
import org.immutables.value.Value;

/**
 * Represents a blob query.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface BlobQuery extends Serializable {
  /**
   * Returns the value of the blobName attribute.
   *
   * @return The value of the blobName attribute
   */
  String blobName();

  /**
   * Indicates to include the blob {@linkplain java.io.InputStream} body in the query response.
   *
   * @return True if the blob body will be included in the query response, false otherwise
   */
  @Value.Default
  default Boolean include() {
    return true;
  }
}
