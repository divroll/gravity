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

import java.util.List;

/**
 * The LuceneIndexer interface represents an indexer for indexing
 * and searching entities using the Lucene library.
 */
public interface LuceneIndexer {
  /**
   * Indexes an entity in the given directory with a specified entity ID,
   * longitude, and latitude.
   *
   * @param dir the directory where the entity will be indexed
   * @param entityId the ID of the entity to be indexed
   * @param longitude the longitude of the entity's location
   * @param latitude the latitude of the entity's location
   * @return true if the indexing is successful, false otherwise
   * @throws Exception if an error occurs during indexing
   */
  Boolean index(String dir, String entityId, Double longitude, Double latitude) throws Exception;

  Boolean index(String dir, String entityId, String field, String text) throws Exception;

  /**
   *
   * @param longitude
   * @param latitude
   * @param radius
   * @param after the ID of the the tail {@code Entity} from the previous search result
   * @param hits the number of documents to return
   * @return
   * @throws Exception
   */
  List<String> searchNeighbor(String dir, Double longitude, Double latitude, Double radius, String after,
      Integer hits)
      throws Exception;

  List<String> search(String dir, String field, String text, String after,
      Integer hits)
    throws Exception;
}
