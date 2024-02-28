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

import javax.annotation.Nonnull;

/**
 * This class provides utility methods for handling checked exceptions in functional interfaces.
 */
public final class Throwing {
  /**
   * Constructs a private Throwing object.
   *
   * <p>Note: This constructor is private and cannot be accessed or called from outside the class.
   */
  private Throwing() {}

  /**
   * This method returns a Consumer that allows rethrowing checked exceptions thrown by a ThrowingConsumer.
   *
   * @param consumer The ThrowingConsumer that throws checked exceptions.
   * @param <T>      The type of the input to the Consumer.
   * @return A Consumer that allows rethrowing checked exceptions.
   */
  @Nonnull
  public static <T> Consumer<T> rethrow(@Nonnull final ThrowingConsumer<T> consumer) {
    return consumer;
  }

  /**
   * Throws a throwable object without requiring a throws declaration.
   * This method is used to circumvent checked exceptions in Java by throwing a checked exception
   * as if it were an unchecked exception.
   *
   * @param ex the throwable object to be thrown
   * @param <E> the type of the throwable object
   * @throws E the throwable object of type E
   */
  @SuppressWarnings("unchecked")
  @Nonnull
  public static <E extends Throwable> void sneakyThrow(@Nonnull Throwable ex) throws E {
    throw (E) ex;
  }
}