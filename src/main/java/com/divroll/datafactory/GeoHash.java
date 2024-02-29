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

import com.divroll.datafactory.helpers.MathHelper;
import com.google.common.base.Preconditions;

/**
 * Represents a GeoHash.
 */
public class GeoHash {
  /**
   * The minimum longitude value.
   */
  public static final int LONGITUDE_MIN = -180;
  /**
   * The maximum longitude value in degrees.
   * The longitude value cannot exceed this value.
   */
  public static final int LONGITUDE_MAX = 180;
  /**
   * The minimum value for latitude.
   */
  public static final int LATITUDE_MIN = -90;
  /**
   * The maximum value for latitude.
   */
  public static final int LATITUDE_MAX = 90;
  /**
   * Represents the precision value used in the application.
   * This value determines the level of accuracy or specificity in calculations or measurements.
   */
  public static final int PRECISION = 12;
  /**
   * The GeoHash as a String.
   */
  private String geoHash;

  /**
   * This is a utility class and cannot be instantiated.
   */
  private GeoHash() {
  }

  /**
   * Creates a new GeoHash with the given longitude and latitude.
   *
   * @param longitude the longitude
   * @param latitude the latitude
   */
  public GeoHash(final Double longitude, final Double latitude) {
    Preconditions.checkArgument(
            latitude >= LATITUDE_MIN && latitude <= LATITUDE_MAX,
            "Latitude must be in the range of [" + LATITUDE_MIN + ", "
                    + LATITUDE_MAX + "] degrees");

    Preconditions.checkArgument(
            longitude >= LONGITUDE_MIN && longitude <= LONGITUDE_MAX,
            "Longitude must be in the range of [" + LONGITUDE_MIN + ", "
                    + LONGITUDE_MAX + "] degrees");

    this.geoHash = ch.hsr.geohash.GeoHash
            .withCharacterPrecision(
                    latitude,
                    longitude,
                    PRECISION).toBase32();
  }

  /**
   * Creates a GeoHash string representation of the given longitude and latitude.
   *
   * @param longitude the longitude
   * @param latitude the latitude
   * @return the GeoHash string representation
   */
  public static String create(final Double longitude, final Double latitude) {
    return new GeoHash(longitude, latitude).toString();
  }

  /**
   * Returns the GeoHash as a String.
   */
  @Override public String toString() {
    return geoHash;
  }

  /**
   * A 2D array representing the ranges for each index.
   * The first index represents the row, and the second index represents the column.
   * Each row contains two elements, indicating the minimum and maximum values for that range.
   * The ranges are used for calculating the GeoHash precision level based on
   * reference distance values.
   */
  private static final double[][] RANGES = {
          {0.037, 0.018},
          {0.149, 0.149},
          {1.19, 0.6},
          {4.47, 4.78},
          {38.2, 19.1},
          {152.8, 152.8},
          {1200, 610},
          {4900, 4900},
          {39000, 19500},
          {156000, 156000},
          {1251000, 625000},
          {5004000, 5004000},
  };
  /**
   * Maximum value index in a row.
   * <p>
   * This variable represents the index of the*/
  private static final int MAX_VALUE_INDEX_IN_ROW = 0;
  /**
   * The minimum value index in a row.
   * Represents the index of the lowest value in a row of a dataset.
   * This index is used to identify the position of the minimum value in various operations
   * or calculations. It is an integer constant and cannot be modified.
   *
   * Example usage:
   *
   * int minValueIndex = MIN_VALUE_INDEX_IN_ROW;
   */
  private static final int MIN_VALUE_INDEX_IN_ROW = 1;
  /**
   * The size of the array.
   */
  private static final int ARRAY_SIZE = 24;

  /**
   * Calculates the GeoHash precision level for a given reference distance value in kilometers.
   * The method is designed to determine the most suitable GeoHash precision level based on
   * predefined distance ranges that correspond to each precision level. If there's no exact
   * match for the reference distance within the ranges, the method finds the closest match.
   *
   * @param reference The reference distance value in kilometers.
   * @return The GeoHash precision level that corresponds to the reference distance.
   *         If there's no exact match within the predefined ranges, the method will return
   *         the precision level that has the closest match.
   *         Returns -1 if no suitable precision level is discovered.
   */
  public static int calculateGeoHashPrecision(final Double reference) {
    for (int i = 0; i < RANGES.length; i++) {
      if (reference >= RANGES[i][MIN_VALUE_INDEX_IN_ROW]
              && reference <= RANGES[i][MAX_VALUE_INDEX_IN_ROW]) {
        return i;
      }
    }
    // Finding nearest match
    double[] arr = new double[ARRAY_SIZE];
    int arrIndex = 0;
    for (double[] range : RANGES) {
      for (double v : range) {
        arr[arrIndex++] = v;
      }
    }
    double nearest = MathHelper.findClosest(arr, reference);
    for (int row = 0; row < RANGES.length; row++) {
      for (double v : RANGES[row]) {
        if (v == nearest) {
          return row;
        }
      }
    }
    return -1;
  }
}
