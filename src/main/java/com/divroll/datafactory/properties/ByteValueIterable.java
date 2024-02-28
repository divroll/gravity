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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import jetbrains.exodus.ArrayByteIterable;
import jetbrains.exodus.ByteIterable;
import jetbrains.exodus.ByteIterator;
import jetbrains.exodus.util.ByteIterableUtil;
import org.jetbrains.annotations.NotNull;

/**
 * The ByteValueIterable class represents an iterable of bytes derived from a ByteValue object.
 */
public class ByteValueIterable implements Serializable, ByteIterable {
  /**
   * Represents the offset value within a ByteValueIterable object.
   * The offset indicates the starting position within an array of bytes.
   */
  private final long offset;
  /**
   * Represents the length of a byte array.
   */
  private final int length;
  /**
   * Represents a byte value.
   */
  private ByteValue byteValue;
  /**
   * Represents a byte array.
   */
  private byte[] bytes;

  /**
   * This class represents an iterable of bytes derived from a ByteValue object.
   */
  public ByteValueIterable(ByteValue byteValue) {
    byte[] bytes = toByteArray(byteValue);
    this.bytes = bytes;
    this.byteValue = byteValue;
    this.offset = 0L;
    this.length = bytes.length;
  }

  /**
   * Returns an iterator over the bytes in this ByteValueIterable object.
   *
   * @return an iterator over the bytes
   */
  @Override
  public ByteIterator iterator() {
    return new ArrayByteIterable(bytes).iterator();
  }

  /**
   * Retrieves the byte array stored in the ByteValueIterable object.
   *
   * @return The byte array stored in the ByteValueIterable object.
   */
  @Override
  public byte[] getBytesUnsafe() {
    return bytes;
  }

  /**
   * Retrieves the length of the byte array.
   *
   * @return The length of the byte array.
   */
  @Override
  public int getLength() {
    return length;
  }

  /**
   * Creates a new sub-iterable of the current iterable, starting at the given offset and
   * extending for the specified length.
   *
   * @param offset The start position of the sub-iterable.
   * @param length The length of the sub-iterable.
   * @return The sub-iterable.
   * @throws UnsupportedOperationException if the operation is not supported.
   */
  @NotNull
  @Override
  public ByteIterable subIterable(int offset, int length) {
    throw new UnsupportedOperationException("subIterable");
  }

  /**
   * Compares this ByteIterable with another ByteIterable for order. The comparison is based on the values
   * of the bytes in the ByteIterables.
   *
   * @param o the ByteIterable to be compared
   * @return a negative integer, zero, or a positive integer as this ByteIterable is less than, equal to,
   *         or greater than the specified ByteIterable.
   */
  @Override
  public int compareTo(@NotNull ByteIterable o) {
    return ByteIterableUtil.compare(this, o);
  }

  /**
   * Converts an object to a byte array.
   *
   * @param object the object to be converted
   * @return the byte array representing the object
   */
  protected byte[] toByteArray(Object object) {
    try {
      try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
           ObjectOutput out = new ObjectOutputStream(bos)) {
        out.writeObject(object);
        return bos.toByteArray();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
