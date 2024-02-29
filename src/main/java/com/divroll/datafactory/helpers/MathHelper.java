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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.divroll.datafactory.helpers;

/**
 * Provides mathematical helper methods, particularly for operations
 * involving numerical arrays. This class is designed as a utility
 * class, meaning it contains only static methods and should not be
 * instantiated. The primary use case is to support mathematical
 * operations that are frequently required across various parts of an
 * application, such as finding the closest number in an array to a given value.
 */
public final class MathHelper {
  /**
   * Prevents instantiation of this utility class.
   * Utility classes should not have public constructors, as they are not meant to be instantiated.
   * Throwing an exception prevents accidental instantiation from within the class itself.
   */
  private MathHelper() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /**
   * Finds the closest value to a specified target in a sorted array of doubles.
   * This method is optimized for performance in sorted arrays, utilizing a binary search approach.
   * It handles edge cases where the target value is outside the bounds of the array values.
   *
   * @param numberArray A sorted array of double values. It's crucial that the array is sorted
   *                    for the binary search technique to work correctly.
   * @param targetValue The double value for which the closest number is to be found.
   * @return The value from the array that is closest to the specified target value.
   */
  public static double findClosest(final double[] numberArray, final double targetValue) {
    int arrayLength = numberArray.length;
    if (targetValue <= numberArray[0]) {
      return numberArray[0];
    }
    if (targetValue >= numberArray[arrayLength - 1]) {
      return numberArray[arrayLength - 1];
    }

    return findNearest(numberArray, targetValue, arrayLength);
  }

  /**
   * Performs a binary search to find the nearest value to the targetValue within the array.
   * The method is private as it serves as a helper for the public findClosest method,
   * encapsulating the logic specific to the binary search implementation.
   *
   * @param numberArray The array of numbers to search through, assumed to be sorted.
   * @param targetValue The target value to find the closest number to.
   * @param arrayLength The length of the numberArray, passed in to avoid recalculating.
   * @return The value from the array closest to the target value.
   */
  private static double findNearest(final double[] numberArray,
                                    final double targetValue,
                                    final int arrayLength) {
    int left = 0;
    int right = arrayLength;
    int middleIndex = 0;

    while (left < right) {
      middleIndex = calculateMiddleIndex(left, right);
      double middleValue = numberArray[middleIndex];

      if (middleValue == targetValue) {
        return middleValue;
      }

      if (targetValue < middleValue) {
        if (middleIndex > 0 && targetValue > numberArray[middleIndex - 1]) {
          return getClosest(numberArray[middleIndex - 1], middleValue, targetValue);
        }
        right = middleIndex;
      } else {
        if (middleIndex < arrayLength - 1 && targetValue < numberArray[middleIndex + 1]) {
          return getClosest(middleValue, numberArray[middleIndex + 1], targetValue);
        }
        left = middleIndex + 1;
      }
    }

    return numberArray[middleIndex];
  }

  /**
   * Calculates the middle index between two indices.
   * This method simplifies the calculation and centralizes the logic for updating
   * the search bounds.
   *
   * @param leftIndex The left boundary of the current search interval.
   * @param rightIndex The right boundary of the current search interval.
   * @return The middle index between the left and right indices.
   */
  private static int calculateMiddleIndex(final int leftIndex, final int rightIndex) {
    return (leftIndex + rightIndex) / 2;
  }

  /**
   * Determines which of two values is closer to a target value.
   * This method abstracts the logic for determining closeness, reducing duplication
   * and centralizing the comparison logic.
   *
   * @param value1 The first value to compare against the target.
   * @param value2 The second value to compare against the target.
   * @param targetValue The target value for the comparison.
   * @return The value (either value1 or value2) that is closest to the target value.
   */
  private static double getClosest(final double value1,
                                   final double value2,
                                   final double targetValue) {
    if (targetValue - value1 >= value2 - targetValue) {
      return value2;
    } else {
      return value1;
    }
  }
}
