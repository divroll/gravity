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
package com.divroll.datafactory.properties;

import java.io.Serializable;
import java.nio.ByteBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a byte array value.
 */
public class ByteValue implements Serializable, Comparable<byte[]> {
  /**
   * Returns the byte array value.
   *
   * @return the byte array value
   */
  private byte[] value;

  /**
   * Retrieves the value of the byte array.
   *
   * @return The byte array value.
   */
  public byte[] getValue() {
    return value;
  }

  /**
   * Sets the value of the byte array.
   *
   * @param value the byte array value to set
   */
  public void setValue(byte[] value) {
    this.value = value;
  }

  /**
   * Compares this byte array with another byte array for order. The comparison is based on the values
   * of the bytes in the arrays.
   *
   * @param o the byte array to be compared
   *
   * @return a negative integer, zero, or a positive integer as this byte array is less than, equal to,
   *         or greater than the specified byte array.
   */
  @Override
  public int compareTo(@NotNull byte[] o) {
    return ByteBuffer.wrap(value).compareTo(ByteBuffer.wrap(o));
  }
}
