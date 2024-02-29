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
package com.divroll.datafactory.exceptions;

import java.util.function.Consumer;

/**
 * A functional interface that represents a consumer that can throw checked exceptions.
 *
 * @param <T> the type of the input to the consumer
 */
@FunctionalInterface
public interface ThrowingConsumer<T> extends Consumer<T> {
  /**
   * Accepts an element of type T and applies an action to it. If any checked exception is thrown
   * during the execution of the action, it is caught and rethrown as an unchecked exception.
   *
   * @param e the element to accept and apply the action to
   */
  @Override
  default void accept(final T e) {
    try {
      accept0(e);
    } catch (Throwable ex) {
      Throwing.sneakyThrow(ex);
    }
  }
  /**
   * Accepts an input of type T.
   *
   * @param t the input of type T
   * @throws Throwable if an error occurs while accepting the input
   */
  void accept0(T t) throws Throwable;
}
