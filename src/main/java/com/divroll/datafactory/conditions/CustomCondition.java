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
package com.divroll.datafactory.conditions;

import com.divroll.datafactory.conditions.exceptions.UnsatisfiedConditionException;
import jetbrains.exodus.entitystore.Entity;

/**
 * CustomCondition is an interface representing a custom condition applied to entities.
 * It extends the EntityCondition interface.
 * Custom conditions can be executed on entities to check if a condition is satisfied.
 * If the condition is not satisfied, an UnsatisfiedConditionException is thrown.
 */
public interface CustomCondition extends EntityCondition {
  /**
   * Executes the provided custom condition on the given entity in context.
   *
   * @param entityInContext The entity on which the condition will be executed.
   * @throws UnsatisfiedConditionException If the condition is not satisfied.
   */
  void execute(Entity entityInContext) throws UnsatisfiedConditionException;
}
