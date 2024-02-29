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

import com.google.common.base.Preconditions;
import java.io.Serializable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a geographic point with latitude and longitude coordinates.
 */
public class GeoPoint implements Comparable<GeoPoint>, Serializable {
  /**
   * The minimum valid latitude value.
   */
  private static final double MIN_LATITUDE = -90.0;
  /**
   * The maximum latitude value allowed for a geographic point.
   */
  private static final double MAX_LATITUDE = 90.0;
  /**
   * Minimum longitude value allowed for a geographic point.
   *
   * The value of MIN_LONGITUDE is set to -180.0 degrees.
   * This represents the westernmost longitude that can be assigned to a GeoPoint.
   * Any longitude value less than -180.0 is considered invalid.
   *
   * @see GeoPoint
   */
  private static final double MIN_LONGITUDE = -180.0;
  /**
   * The maximum longitude value allowed in the GeoPoint class.
   */
  private static final double MAX_LONGITUDE = 180.0;
  /**
   * Represents the longitude of a geographic point.
   *
   * This variable stores the longitude of a geographic point in degrees.
   * The range of valid longitude values is -180 to 180 degrees.
   *
   * @see GeoPoint
   */
  private Double longitude;
  /**
   * Represents the latitude value of a GeoPoint.
   * <p>
   * The latitude value indicates the north-south position of the GeoPoint on the Earth's surface.
   * It is a decimal number between -90 and 90 degrees, where positive values indicate
   * northern hemisphere and negative values indicate southern hemisphere.
   * </p>
   * <p>
   * This variable is a Double type to allow for more accurate latitude representation,
   * such as when dealing with decimal degrees or precise coordinates.
   * </p>
   *
   * @see GeoPoint
   */
  private Double latitude;

  /**
   * Creates a new GeoPoint with the given longitude and latitude.
   *
   * @param longitude the longitude of the GeoPoint
   * @param latitude  the latitude of the GeoPoint
   */
  public GeoPoint(final Double longitude, final Double latitude) {
    Preconditions.checkArgument(
            latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE,
            "Latitude must be in the range of [-90, 90] degrees");
    Preconditions.checkArgument(
            longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE,
            "Longitude must be in the range of [-180, 180] degrees");
    this.longitude = longitude;
    this.latitude = latitude;
  }

  /**
   * Compares this GeoPoint with another GeoPoint.
   *
   * @param   geoPoint the GeoPoint to compare with
   * @return  0 if the given GeoPoint is equal to the current GeoPoint,
   *          a value less than 0 if the current GeoPoint is less than the given GeoPoint, and
   *          a value greater than 0 if the current GeoPoint is greater than the given GeoPoint
   */
  @Override public int compareTo(@NotNull final GeoPoint geoPoint) {
    if (this.latitude < geoPoint.latitude) {
      return -1;
    } else if (this.latitude > geoPoint.latitude) {
      return 1;
    } else {
      // At this point, latitudes are equal, so compare by longitude
      if (this.longitude < geoPoint.longitude) {
        return -1;
      } else if (this.longitude > geoPoint.longitude) {
        return 1;
      } else {
        // Both latitude and longitude are equal, so the points are considered equal
        return 0;
      }
    }
  }

  /**
   * Overrides the equals method for the GeoPoint class.
   *
   * @param obj the object to be compared with the current GeoPoint instance
   * @return    true if the given object is a GeoPoint and its latitude and longitude match
   *            the current instance's latitude and longitude, false otherwise
   */
  @Override public boolean equals(final Object obj) {
    // Check if the current instance is the same as the object
    if (this == obj) {
      return true;
    }
    // Check if the object is null or if it's not an instance of GeoPoint
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    // Cast the object to GeoPoint
    GeoPoint geoPoint = (GeoPoint) obj;
    // Compare the latitude and longitude of the current instance and the object
    return Double.compare(geoPoint.latitude, latitude) == 0
        && Double.compare(geoPoint.longitude, longitude) == 0;
  }

  /**
   * Overrides the hashCode method for the GeoPoint class.
   * @return the hash code of the GeoPoint
   */
  @Override public int hashCode() {
    return java.util.Objects.hash(latitude, longitude);
  }

  /**
   * Returns the longitude of this GeoPoint.
   *
   * @return the longitude of this GeoPoint
   */
  public Double getLongitude() {
    return longitude;
  }

  /**
   * Returns the latitude of this GeoPoint.
   *
   * @param longitude the longitude of this GeoPoint
   */
  public void setLongitude(final Double longitude) {
    this.longitude = longitude;
  }

  /**
   * Returns the latitude of this GeoPoint.
   *
   * @return the latitude of this GeoPoint
   */
  public Double getLatitude() {
    return latitude;
  }

  /**
   * Sets the latitude of this GeoPoint.
   *
   * @param latitude the latitude of this GeoPoint
   */
  public void setLatitude(final Double latitude) {
    this.latitude = latitude;
  }
}
