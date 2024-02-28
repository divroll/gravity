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

public class EntityConditionProcessors {
    private static final List<Class<?>> entityProcessorClasses;

    private static final String PACKAGE_NAME = "com.divroll.datafactory.conditions.processors";

    static {
        try (ScanResult scanResult = new ClassGraph()
                .whitelistPackages(PACKAGE_NAME)
                .scan()) {
            entityProcessorClasses = scanResult.getClassesImplementing(UnsatisfiedConditionProcessor.class.getName())
                    .filter(classInfo -> classInfo.getPackageName().equals(PACKAGE_NAME))
                    .loadClasses(true);
        }
    }

    public static List<Class<?>> getEntityProcessorClasses() {
        return entityProcessorClasses;
    }

    private List<Class<?>> findClasses(String packageName, Class<?> superClass) {
        try (ScanResult scanResult = new ClassGraph()
                .whitelistPackages(packageName)
                .scan()) {
            return scanResult.getClassesImplementing(superClass.getName()).loadClasses(true);
        }
    }
}
