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
package com.divroll.datafactory.indexers;

import com.divroll.datafactory.indexers.impl.LuceneIndexerImpl;

import java.io.Serializable;

/**
 * The SerializableLuceneIndexerProvider class is an implementation of the LuceneIndexerProvider interface.
 * It provides a method to retrieve a singleton instance of LuceneIndexer using LuceneIndexerImpl.getInstance().
 * This class is Serializable, allowing the LuceneIndexerProvider instance to be serialized and deserialized.
 */
public class SerializableLuceneIndexerProvider implements LuceneIndexerProvider, Serializable {
    @Override
    public LuceneIndexer getLuceneIndexer() {
        return LuceneIndexerImpl.getInstance();
    }
}
