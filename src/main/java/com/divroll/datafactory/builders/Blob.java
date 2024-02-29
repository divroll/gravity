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

import com.healthmarketscience.rmiio.RemoteInputStream;
import java.io.Serializable;
import javax.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface Blob extends Serializable {
  /**
   * Retrieves the name of the blob.
   *
   * @return The name of the blob.
   */
  String blobName();

  /**
   * Retrieves the size of the blob.
   *
   * @return The size of the blob, or null if the size is unknown.
   */
  @Nullable
  Long blobSize();

  /**
   * Retrieves the remote input stream for the blob.
   *
   * @return The remote input stream for the blob.
   */
  RemoteInputStream blobStream();

  /**
   * Indicates that this blob can be {@code set} to multiple
   * {@linkplain jetbrains.exodus.entitystore.Entity}. If used for a delete operation this
   * property indicates whether to delete the blob from multiple
   * {@linkplain jetbrains.exodus.entitystore.Entity} matching the query.
   *
   * @return True if the blob can be set to multiple
   * {@linkplain jetbrains.exodus.entitystore.Entity}, false otherwise
   */
  @Nullable
  @Value.Default
  default Boolean allowMultiple() {
    return false;
  }

  /**
   * Retrieves the count of a Blob.
   *
   * @return The count of the Blob, or null if the count is unknown.
   */
  @Nullable
  Long count();
}
