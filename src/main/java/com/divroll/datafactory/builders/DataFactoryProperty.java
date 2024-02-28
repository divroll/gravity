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

import com.divroll.datafactory.Constants;
import com.divroll.datafactory.actions.EntityPropertyAction;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;
import org.immutables.value.Value;

/**
 * DataFactoryProperty is an interface that represents a property for the DataFactory class.
 * It extends the Serializable interface.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface DataFactoryProperty extends Serializable {
  /**
   * Retrieves the environment for the {@link DataFactoryProperty} instance.
   *
   * @return The environment value.
   */
  @Value.Default
  default String environment() {
    return System.getProperty(Constants.DATAFACTORY_DIRECTORY_ENVIRONMENT);
  }

  /**
   * Returns the entityType of the DataFactoryProperty instance.
   *
   * @return The entityType value.
   */
  String entityType();

  /**
   * Retrieves the name space for the DataFactoryProperty instance.
   *
   * @return The name space value, or null if not set.
   */
  @Nullable
  String nameSpace();

  /**
   * Retrieves the property actions for the DataFactoryProperty instance.
   *
   * @return The list of EntityPropertyAction objects.
   */
  List<EntityPropertyAction> propertyActions();
}
