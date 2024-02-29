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
package com.divroll.datafactory.database;

import com.divroll.datafactory.database.impl.DatabaseManagerImpl;

import java.io.Serializable;

/**
 * The SerializableDatabaseManagerProvider class implements the DatabaseManagerProvider interface
 * and provides an implementation for the getDatabaseManager method by returning an instance of
 * the DatabaseManagerImpl class.
 *
 * This class also implements the Serializable interface to allow objects of this class to be
 * serialized and deserialized.
 */
public class SerializableDatabaseManagerProvider
        implements DatabaseManagerProvider, Serializable {
    /**
     * Retrieves the DatabaseManager instance.
     *
     * @return the DatabaseManager instance
     */
    @Override
    public DatabaseManager getDatabaseManager() {
        return DatabaseManagerImpl.getInstance();
    }
}
