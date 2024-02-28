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
package com.divroll.datafactory.conditions;

import java.io.Serializable;
import javax.annotation.Nullable;
import org.immutables.value.Value;

/**
 * EntityCondition is an interface that represents a condition for querying entities.
 * It extends the Serializable interface and provides methods for retrieving and setting the binary operator for the condition.
 */
public interface EntityCondition extends Serializable {
  enum BINARY_OP {
    INTERSECT, MINUS, UNION, CONCAT
  }
  /**
   * Returns the binary operator for the condition. If not set, the default value is BINARY_OP.INTERSECT.
   *
   * @return the binary operator for the condition
   */
  @Nullable
  @Value.Default
  default BINARY_OP binaryOperator() {
    return BINARY_OP.INTERSECT;
  }
}
