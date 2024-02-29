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
package com.divroll.datafactory.conditions.exceptions;

import com.divroll.datafactory.conditions.EntityCondition;
import com.divroll.datafactory.exceptions.DataFactoryException;

/**
 * Exception thrown when a given condition is not satisfied.
 */
public class UnsatisfiedConditionException extends DataFactoryException {
  /**
   * Exception thrown when a given condition is not satisfied.
   *
   * @param condition The unsatisfied condition.
   */
  public UnsatisfiedConditionException(final EntityCondition condition) {
   super("The condition " + condition.getClass().getName() + " was not satisfied");
  }

  /**
   * Exception thrown when a given condition is not satisfied.
   *
   * @param condition The unsatisfied condition.
   * @param e The cause of the exception.
   *
   * @see EntityCondition
   * @see com.divroll.datafactory.conditions.LinkCondition
   * @see com.divroll.datafactory.conditions.OppositeLinkCondition
   * @see com.divroll.datafactory.conditions.PropertyContainsCondition
   * @see com.divroll.datafactory.conditions.PropertyEqualCondition
   * @see com.divroll.datafactory.conditions.PropertyLocalTimeRangeCondition
   * @see com.divroll.datafactory.conditions.PropertyMinMaxCondition
   * @see com.divroll.datafactory.conditions.PropertyNearbyCondition
   * @see com.divroll.datafactory.conditions.PropertyStartsWithCondition
   * @see com.divroll.datafactory.conditions.PropertyUniqueCondition
   * @see com.divroll.datafactory.conditions.CustomCondition
   * @see com.divroll.datafactory.conditions.CustomQueryCondition
   */
  public UnsatisfiedConditionException(final EntityCondition condition, final Throwable e) {
    super("The condition " + condition.getClass().getName() + " was not satisfied", e);
  }
}
