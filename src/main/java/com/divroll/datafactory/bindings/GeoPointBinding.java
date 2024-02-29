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

import java.io.ByteArrayInputStream;
import java.time.LocalTime;
import jetbrains.exodus.ArrayByteIterable;
import jetbrains.exodus.ByteIterable;
import jetbrains.exodus.bindings.ComparableBinding;
import jetbrains.exodus.util.LightOutputStream;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a binding for a GeoPoint.
 */
public class GeoPointBinding extends ComparableBinding {

  /**
   * The binding for a GeoPoint.
   */
  public static final GeoPointBinding BINDING = new GeoPointBinding();

  /**
   * Reads a serialized object from the given ByteArrayInputStream.
   *
   * @param stream the ByteArrayInputStream containing the serialized object
   * @return the deserialized object or null if an error occurred
   */
  @Override public Comparable readObject(@NotNull final ByteArrayInputStream stream) {
    return BindingUtils.readObject(stream);
  }

  /**
   * Writes the specified object to the provided output stream.
   *
   * @param output the LightOutputStream to write the object to
   * @param object the object to be written
   */
  @Override public void writeObject(@NotNull final LightOutputStream output,
                                    @NotNull final Comparable object) {
    output.write(BindingUtils.writeObject(object));
  }

  /**
   * Converts a serialized entry into a LocalTime object.
   *
   * @param entry the serialized entry to convert
   * @return the LocalTime object retrieved from the entry
   */
  public static LocalTime entryToLocalTime(@NotNull final ByteIterable entry) {
    return (LocalTime) BINDING.entryToObject(entry);
  }

  /**
   * Converts a LocalTime object into an ArrayByteIterable object.
   *
   * @param object the LocalTime object to convert to ArrayByteIterable
   * @return the ArrayByteIterable object representing the LocalTime object
   */
  public static ArrayByteIterable localTimeToEntry(final LocalTime object) {
    return BINDING.objectToEntry(object);
  }
}
