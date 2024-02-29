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
package com.divroll.datafactory.conditions.processors;

import java.util.List;

import com.divroll.datafactory.conditions.processors.core.UnsatisfiedConditionProcessor;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

/**
 * The EntityConditionProcessors class is responsible for storing and retrieving a list of entity
 * processor classes and finding classes in a specified package that implement or extend a given
 * superclass or interface.
 *
 * The PACKAGE_NAME variable represents the package name for the classes related to entity
 * condition processors in the data factory.
 *
 * @see EntityConditionProcessors
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
 * @see com.divroll.datafactory.conditions.EntityCondition
 */
public class EntityConditionProcessors {
    /**
     * A List of Class objects representing the entity processor classes.
     * Entity processors are used for processing entities based on specific conditions.
     * These classes implement the EntityProcessor interface.
     */
    private static final List<Class<?>> ENTITY_PROCESSOR_CLASSES;
    /**
     * The PACKAGE_NAME variable represents the package name for the classes related to entity
     * condition processors in the data factory.
     *
     * @see EntityConditionProcessors
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
     * @see com.divroll.datafactory.conditions.EntityCondition
     */
    private static final String PACKAGE_NAME = "com.divroll.datafactory.conditions.processors";

    static {
        try (ScanResult scanResult = new ClassGraph()
                .whitelistPackages(PACKAGE_NAME)
                .scan()) {
            ENTITY_PROCESSOR_CLASSES = scanResult
                    .getClassesImplementing(UnsatisfiedConditionProcessor.class.getName())
                    .filter(classInfo -> classInfo.getPackageName().equals(PACKAGE_NAME))
                    .loadClasses(true);
        }
    }

    /**
     * Retrieves a list of entity processor classes.
     *
     * @return A list of Class objects representing the entity processor classes.
     */
    public static List<Class<?>> getEntityProcessorClasses() {
        return ENTITY_PROCESSOR_CLASSES;
    }

    /**
     * Finds classes in the specified package that implement or extend the given superclass
     * or interface.
     *
     * @param packageName The name of the package to scan for classes.
     * @param superClass  The superclass or interface that the classes should implement or extend.
     * @return A List of Class objects that implement or extend the given superclass or interface.
     */
    private List<Class<?>> findClasses(final String packageName, final Class<?> superClass) {
        try (ScanResult scanResult = new ClassGraph()
                .whitelistPackages(packageName)
                .scan()) {
            return scanResult
                    .getClassesImplementing(superClass.getName())
                    .loadClasses(true);
        }
    }
}
