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

import java.io.Serializable;
import javax.annotation.Nullable;

import com.divroll.datafactory.database.BinaryOp;
import com.divroll.datafactory.database.EqualityOp;
import org.immutables.value.Value;

/**
 * TransactionFilter class represents a filter for transactions.
 */
@Value.Immutable
public interface TransactionFilter extends Serializable {
  /**
   * Retrieves the value of the equalityOp attribute in the TransactionFilter class.
   *
   * @return The value of the equalityOp attribute
   */
  EqualityOp equalityOp();

  /**
   * Retrieves the value of the propertyName attribute.
   * @return The value of the propertyName attribute
   */
  String propertyName();

  /**
   * Returns the value of the {@code propertyValue} attribute.
   *
   * @return The value of the {@code propertyValue} attribute.
   */
  Comparable propertyValue();

  /**
   * Returns the minimum value associated with this transaction filter.
   *
   * @return The minimum value, or null if it is not set
   */
  @Nullable
  Comparable minValue();

  /**
   * Returns the maximum value for the {@link TransactionFilter#maxValue() maxValue} attribute.
   *
   * @return The maximum value (can be {@code null})
   */
  @Nullable
  Comparable maxValue();

  /**
   * Retrieves the value of the operator attribute.
   *
   * @return The value of the operator attribute
   */
  @Nullable
  BinaryOp operator();

  /**
   * Retrieves the next {@code TransactionFilter} in the sequence.
   *
   * @return The next {@code TransactionFilter} in the sequence, or null if there is no next filter.
   */
  @Nullable
  TransactionFilter next();
}
