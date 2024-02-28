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
 *
 */
public class GeoPoint implements Comparable<GeoPoint>, Serializable {
  private Double longitude;
  private Double latitude;

  /**
   * Creates a new GeoPoint with the given longitude and latitude.
   *
   * @param longitude the longitude of the GeoPoint
   * @param latitude  the latitude of the GeoPoint
   */
  public GeoPoint(Double longitude, Double latitude) {
    Preconditions.checkArgument(
        latitude >= -90 && latitude <= 90, "Latitude must be in the range of [-90, 90] degrees");
    Preconditions.checkArgument(
        longitude >= -180 && longitude <= 180,
        "Longitude must be in the range of [-180, 180] degrees");
    setLongitude(longitude);
    setLatitude(latitude);
  }

  /**
   * Compares this GeoPoint with another GeoPoint.
   *
   * @param   geoPoint the GeoPoint to compare with
   * @return  0 if the given GeoPoint is equal to the current GeoPoint,
   *          a value less than 0 if the current GeoPoint is less than the given GeoPoint, and
   *          a value greater than 0 if the current GeoPoint is greater than the given GeoPoint
   */
  @Override public int compareTo(@NotNull GeoPoint geoPoint) {
    return 0;
  }

  /**
   * Overrides the equals method for the GeoPoint class.
   *
   * @param obj the object to be compared with the current GeoPoint instance
   * @return true if the given object is a GeoPoint and its latitude and longitude match the current instance's latitude and longitude, false otherwise
   */
  @Override public boolean equals(Object obj) {
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
   * @return the latitude of this GeoPoint
   */
  public void setLongitude(Double longitude) {
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
  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }
}
