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
import java.io.Serializable;
import java.io.UncheckedIOException;
import jetbrains.exodus.bindings.ComparableBinding;
import jetbrains.exodus.util.LightOutputStream;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a binding for an embedded entity.
 */
public class EmbeddedEntityBinding extends ComparableBinding implements Serializable {
  /**
   * The binding for an embedded entity.
   */
  public static final EmbeddedEntityBinding BINDING = new EmbeddedEntityBinding();
  /**
   * Serializes an object into a byte array.
   *
   * @param obj the object to be serialized
   * @return the serialized object as a byte array
   */
  public static byte[] serialize(final Object obj) {
    try {
      try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
           ObjectOutput out = new ObjectOutputStream(bos)) {
        out.writeObject(obj);
        return bos.toByteArray();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Deserializes a byte array into an object of type T.
   *
   * @param data  the byte array to be deserialized
   * @param clazz the class of the object to be deserialized
   * @param <T>   the type of the object to be deserialized
   * @return the deserialized object of type T, or null if the deserialized object is not of the specified class
   * @throws UncheckedIOException if an IOException occurs during deserialization
   */
  public static <T> T deserialize(final byte[] data, final Class<T> clazz) {
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
   * Reads an object of type Comparable from a ByteArrayInputStream.
   *
   * @param stream the ByteArrayInputStream containing the serialized object
   * @return the deserialized object of type Comparable, or null if the deserialized object is not of type Comparable
   */
  @Override
  public Comparable readObject(@NotNull final ByteArrayInputStream stream) {
    return Try.of(() -> {
      byte[] serialized = ByteStreams.toByteArray(stream);
      return deserialize(serialized, Comparable.class);
    }).getOrNull();
  }

  /**
   * Writes a Comparable object to a LightOutputStream.
   *
   * @param output the LightOutputStream to write the object to
   * @param object the Comparable object to write
   */
  @Override
  public void writeObject(@NotNull final LightOutputStream output, @NotNull final Comparable object) {
    byte[] serialized = serialize(object);
    if (serialized != null) {
      output.write(serialized);
    }
  }
}
