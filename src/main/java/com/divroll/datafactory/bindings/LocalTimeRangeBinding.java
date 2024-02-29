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

import com.divroll.datafactory.LocalTimeRange;
import java.io.ByteArrayInputStream;
import jetbrains.exodus.ArrayByteIterable;
import jetbrains.exodus.ByteIterable;
import jetbrains.exodus.bindings.ComparableBinding;
import jetbrains.exodus.util.LightOutputStream;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a binding for a LocalTimeRange.
 */
public class LocalTimeRangeBinding extends ComparableBinding {

  /**
   * The binding for a LocalTimeRange.
   */
  public static final LocalTimeRangeBinding BINDING = new LocalTimeRangeBinding();

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
   * Writes a given object to a LightOutputStream using serialization.
   *
   * @param output the LightOutputStream to write the serialized object to
   * @param object the object to be serialized
   */
  @Override public void writeObject(@NotNull final LightOutputStream output,
                                    @NotNull final Comparable object) {
    output.write(BindingUtils.writeObject(object));
  }

  /**
   * Converts a ByteIterable entry object to a LocalTimeRange object.
   *
   * @param entry the ByteIterable entry to convert
   * @return the converted LocalTimeRange object
   */
  public static LocalTimeRange entryToLocalTimeRange(@NotNull final ByteIterable entry) {
    return (LocalTimeRange) BINDING.entryToObject(entry);
  }

  /**
   * Converts a LocalTimeRange object to an ArrayByteIterable entry object.
   *
   * @param object the LocalTimeRange object to convert
   * @return the converted ArrayByteIterable entry object
   */
  public static ArrayByteIterable localTimeRangeToEntry(final LocalTimeRange object) {
    return BINDING.objectToEntry(object);
  }
}
