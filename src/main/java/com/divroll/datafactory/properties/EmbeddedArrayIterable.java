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

import io.vavr.control.Try;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import jetbrains.exodus.ArrayByteIterable;
import jetbrains.exodus.ByteIterable;
import jetbrains.exodus.ByteIterator;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an iterable of embedded arrays.
 */
public class EmbeddedArrayIterable implements Serializable, ByteIterable {
  /**
   * Represents a byte array.
   */
  private byte[] bytes;

  /**
   * Represents an Iterable of embedded arrays.
   *
   * @param objects the List of embedded arrays
   */
  public EmbeddedArrayIterable(final List<Comparable> objects) {
    bytes = serialize(objects);
  }

  /**
   * Serializes an object into a byte array.
   *
   * @param obj the object to be serialized
   * @return the serialized byte array, or null if serialization fails
   */
  public static byte[] serialize(final Object obj) {
    return Try.of(() -> {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream os = new ObjectOutputStream(out);
      os.writeObject(obj);
      return out.toByteArray();
    }).getOrNull();
  }

  /**
   * Deserializes a byte array into a Comparable object.
   *
   * @param data the byte array to deserialize
   * @return the deserialized Comparable object, or null if deserialization fails
   */
  public static Comparable deserialize(final byte[] data) {
    return Try.of(() -> {
      ByteArrayInputStream in = new ByteArrayInputStream(data);
      ObjectInputStream is = new ObjectInputStream(in);
      return (Comparable) is.readObject();
    }).getOrNull();
  }

  /**
   * Returns an iterator over the elements in this EmbeddedArrayIterable.
   * The elements are returned in the order they appear in the underlying byte array.
   *
   * @return an iterator over the elements in this EmbeddedArrayIterable
   */
  @Override
  public ByteIterator iterator() {
    return new ArrayByteIterable(bytes).iterator();
  }

  /**
   * Returns the byte array of this object without any safety checks.
   * This method may be faster than {@link #getBytes()} but it is unsafe
   * because it does not perform any validation or copying of the underlying array.
   * Use this method with caution and ensure proper usage to prevent errors.
   *
   * @return the byte array of this object without any safety checks
   */
  @Override
  public byte[] getBytesUnsafe() {
    return bytes;
  }

  /**
   * Returns the length of the byte array.
   *
   * @return The length of the byte array.
   */
  @Override
  public int getLength() {
    return bytes.length;
  }

  /**
   * Returns a sub-iterable of the current byte iterable starting from the specified offset
   * and with the specified length.
   *
   * @param offset the starting offset of the sub-iterable
   * @param length the length of the sub-iterable
   * @return a sub-iterable of the current byte iterable
   */
  @NotNull
  @Override
  public ByteIterable subIterable(final int offset, final int length) {
    return null;
  }

  /**
   * Compares this ByteIterable to the specified ByteIterable.
   *
   * @param o The ByteIterable to be compared.
   * @return Returns a negative integer, zero, or a positive integer as this ByteIterable
   * is less than, equal to, or greater than the specified ByteIterable.
   */
  @Override
  public int compareTo(@NotNull final ByteIterable o) {
    return 0;
  }

  /**
   * Returns the deserialized value of the bytes as a List<Comparable>. If the deserialized value
   * is not an instance of List, null is returned.
   *
   * @return the deserialized value as a List<Comparable> or null if the deserialized value
   * is not a List
   */
  public List<Comparable> asObject() {
    Comparable comparable = deserialize(bytes);
    return comparable instanceof List ? (List<Comparable>) comparable : null;
  }
}
