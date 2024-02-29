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
import org.immutables.value.Value;
import com.divroll.datafactory.builders.Entity;
/**
 * Represents a query to retrieve a {@linkplain Entity} property.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface PropertyQuery extends Serializable {
  /**
   * Retrieve the environment from the PropertyQuery instance.
   *
   * @return The environment value of the PropertyQuery.
   */
  String environment();

  /**
   * Retrieves the entityType value of the PropertyQuery.
   *
   * @return The entityType value of the PropertyQuery.
   */
  String entityType();

  /**
   * Retrieves the value of the nameSpace attribute.
   *
   * @return The value of the nameSpace attribute, which can be null.
   */
  @Nullable
  String nameSpace();

  /**
   * Retrieves the value of the propertyName attribute.
   *
   * @return The value of the propertyName attribute.
   */
  String propertyName();
}
