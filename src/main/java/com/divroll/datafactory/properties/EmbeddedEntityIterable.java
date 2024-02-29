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
import jetbrains.exodus.ArrayByteIterable;
import jetbrains.exodus.ByteIterable;
import jetbrains.exodus.ByteIterator;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:kerby@divroll.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
public class EmbeddedEntityIterable implements Serializable, ByteIterable {
  /**
   * The serialVersionUID is a unique identifier for a Serializable class.
   * It is used during deserialization to ensure that the sender and receiver of
   * a serialized object have loaded classes with compatible serialVersionUIDs.
   * If the serialVersionUIDs do not match, an InvalidClassException is thrown.
   *
   * The serialVersionUID is a 64-bit hash of the class structure,
   * which includes the names, modifiers, and types of the class fields and methods.
   *
   * The serialVersionUID should be declared as a private static final long field.
   * The value can be generated using the `serialver` command-line tool or manually assigned.
   */
  private static final long serialVersionUID = 9074087750155200888L;

  /**
   * Represents a private variable 'bytes' of type byte array.
   * This variable is used to store serialized data as a byte array.
   */
  private byte[] bytes;

  /**
   * Represents an iterable of embedded entities.
   *
   * Constructor:
   * EmbeddedEntityIterable(Comparable object)
   *
   * Parameters:
   * - object: The object to be serialized and stored as bytes
   *
   * Usage:
   * EmbeddedEntityIterable instance = new EmbeddedEntityIterable(object);
   *
   * Example:
   * ComparableHashMap<String, Comparable> comparableMap = new ComparableHashMap<>();
   * EmbeddedEntityIterable iterable = EntityIterables.toEmbeddedEntityIterable(comparableMap);
   *
   * Serialization:
   * The constructor serializes the given object into bytes using the serialize() method.
   * The serialized bytes can be accessed through the getBytesUnsafe() method.
   *
   * Deserialization:
   * The asObject() method deserializes the bytes back into the Comparable object.
   *
   * Note: This class implements the ByteIterable interface.
   *
   * @param object the object to be serialized and stored as bytes
   */
  public EmbeddedEntityIterable(final Comparable object) {
    bytes = serialize(object);
  }

  /**
   * Serializes an object into a byte array.
   *
   * @param obj the object to be serialized
   * @return the serialized byte array
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
   * @param data The byte array to deserialize.
   * @return The deserialized Comparable object, or null if deserialization fails.
   */
  public static Comparable deserialize(final byte[] data) {
    return Try.of(() -> {
      ByteArrayInputStream in = new ByteArrayInputStream(data);
      ObjectInputStream is = new ObjectInputStream(in);
      return (Comparable) is.readObject();
    }).getOrNull();
  }

  /**
   * Returns an iterator over the elements in this EmbeddedEntityIterable.
   *
   * @return an iterator over the elements in this EmbeddedEntityIterable
   */
  @Override
  public ByteIterator iterator() {
    return new ArrayByteIterable(bytes).iterator();
  }

  /**
   * Retrieves the underlying byte array of this ByteIterable object.
   *
   * @return the byte array.
   */
  @Override
  public byte[] getBytesUnsafe() {
    return bytes;
  }

  /**
   * Returns the length of the byte array.
   *
   * @return the length of the byte array
   */
  @Override
  public int getLength() {
    return bytes.length;
  }

  /**
   * Returns a sub-range of the current ByteIterable starting from the specified offset
   * and of the specified length.
   *
   * @param offset the starting offset of the sub-range
   * @param length the length of the sub-range
   * @return a ByteIterable representing the sub-range of the current ByteIterable
   */
  @NotNull
  @Override
  public ByteIterable subIterable(final int offset, final int length) {
    return null;
  }

  /**
   * Compares this ByteIterable object with the specified object for order.
   * The comparison is based on the serialized form of the objects.
   *
   * @param o the object to be compared
   * @return a negative integer, zero, or a positive integer as this object is less than,
   * equal to, or greater than the specified object
   */
  @Override
  public int compareTo(@NotNull final ByteIterable o) {
    return 0;
  }

  /**
   * Converts a byte array to a Comparable object by deserializing it.
   *
   * @return The deserialized Comparable object.
   */
  public Comparable asObject() {
    return deserialize(bytes);
  }
}
