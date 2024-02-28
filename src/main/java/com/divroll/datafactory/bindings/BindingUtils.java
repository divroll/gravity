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
package com.divroll.datafactory.bindings;

import com.google.common.io.ByteStreams;
import io.vavr.control.Try;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for binding objects to byte arrays and vice versa.
 */
public final class BindingUtils {
  private BindingUtils() {
  }

  /**
   * Reads a serialized object from the given ByteArrayInputStream.
   *
   * @param stream the ByteArrayInputStream containing the serialized object
   * @return the deserialized object or null if an error occurred
   */
  public static Comparable readObject(@NotNull final ByteArrayInputStream stream) {
    return Try.of(() -> {
      byte[] serialized = ByteStreams.toByteArray(stream);
      return deserialize(serialized, Comparable.class);
    }).getOrNull();
  }

  /**
   * Writes a given object to a byte array using serialization.
   *
   * @param object the object to be serialized
   * @return the serialized object as a byte array
   */
  public static byte[] writeObject(@NotNull final Comparable object) {
    byte[] serialized = serialize(object);
    return serialized;
  }

  /**
   * Deserializes a byte array into an object of the specified class.
   *
   * @param data  the byte array containing the serialized object
   * @param clazz the class of the object to be deserialized
   * @param <T>   the type of the object to be deserialized
   * @return the deserialized object of the specified class, or null if deserialization fails
   */
  public static <T> T deserialize(@NotNull final byte[] data, final Class<T> clazz) {
    try {
      ByteArrayInputStream in = new ByteArrayInputStream(data);
      ObjectInputStream is = new ObjectInputStream(in);
      Object readObject = is.readObject();
      return clazz.isInstance(readObject)
          ? (T) readObject : null;
    } catch (IOException e) {
     throw new UncheckedIOException(e);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Serializes an object into a byte array using the serialization mechanism.
   *
   * @param obj the object to be serialized
   * @return the serialized object as a byte array
   */
  public static byte[] serialize(@NotNull final Object obj) {
    return Try.of(() -> {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutput out = new ObjectOutputStream(bos);
      out.writeObject(obj);
      return bos.toByteArray();
    }).getOrNull();
  }
}
