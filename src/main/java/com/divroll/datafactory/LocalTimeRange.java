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
package com.divroll.datafactory;

import java.io.Serializable;
import java.time.LocalTime;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a range of LocalTime values.
 */
public class LocalTimeRange implements Comparable<LocalTimeRange>, Serializable {

  private LocalTime lower;
  private LocalTime upper;

  public LocalTimeRange(LocalTime lower, LocalTime upper) {
    setLower(lower);
    setUpper(upper);
  }

  /**
   * Compares this LocalTimeRange with another LocalTimeRange.
   *
   * @param   range the LocalTimeRange to compare with
   * @return  -1 if this LocalTimeRange is before the given LocalTimeRange, 1 if this LocalTimeRange is
   * after the given LocalTimeRange, and 0 if the LocalTimeRanges are equal
   */
  @Override public int compareTo(@NotNull LocalTimeRange range) {
    if ((range.lower.isBefore(lower) && range.upper.isBefore(lower)) && (range.lower.isBefore(upper)
        && range.upper.isBefore(upper))) {
      return -1;
    } else if ((range.lower.isAfter(lower) && range.upper.isAfter(lower)) && (range.lower.isAfter(
        upper)
        && range.upper.isAfter(upper))) {
      return 1;
    }
    return 0;
  }

  /**
   * Returns the lower bound of this LocalTimeRange.
   *
   * @return  the lower bound of this LocalTimeRange
   */
  public LocalTime getLower() {
    return lower;
  }

  /**
   * Sets the lower bound of this LocalTimeRange.
   *
   * @param   lower the lower bound of this LocalTimeRange
   */
  public void setLower(LocalTime lower) {
    this.lower = lower;
  }

  /**
   * Returns the upper bound of this LocalTimeRange.
   *
   * @return  the upper bound of this LocalTimeRange
   */
  public LocalTime getUpper() {
    return upper;
  }

  /**
   * Sets the upper bound of this LocalTimeRange.
   *
   * @param   upper the upper bound of this LocalTimeRange
   */
  public void setUpper(LocalTime upper) {
    this.upper = upper;
  }
}
