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
 * The LuceneIndexer interface provides methods for indexing and searching entities
 * with geographical information using Lucene.
 */
public interface LuceneIndexer {
  /**
   * Indexes an entity at the specified directory with geographical information.
   * The entity is identified by its ID and its longitude and latitude coordinates are provided.
   *
   * @param dir the directory where the index will be stored
   * @param entityId the ID of the entity to be indexed
   * @param longitude the longitude coordinate of the entity
   * @param latitude the latitude coordinate of the entity
   * @return true if the entity is successfully indexed, false otherwise
   * @throws Exception if an error occurs during the indexing operation
   */
  Boolean index(String dir,
                String entityId,
                Double longitude,
                Double latitude) throws Exception;

  /**
   * Indexes the specified text for a given field in the Lucene index.
   *
   * @param dir the directory where the Lucene index is located
   * @param entityId the unique identifier of the entity
   * @param field the field to index the text in
   * @param text the text to be indexed
   * @return true if the text was successfully indexed, otherwise false
   * @throws Exception if an error occurs while indexing the text
   */
  Boolean index(String dir,
                String entityId,
                String field,
                String text) throws Exception;

  /**
   * Search neighbors within a given radius from the specified location.
   *
   * @param dir      The directory where the Lucene index is located.
   * @param longitude The longitude of the location.
   * @param latitude  The latitude of the location.
   * @param radius   The radius (in meters) within which to search for neighbors.
   * @param after    A timestamp to filter results after a certain date/time.
   * @param hits     The maximum number of results to return.
   * @return A list of neighbor entities that match the search criteria.
   * @throws Exception If an error occurs during the search.
   */
  List<String> searchNeighbor(String dir,
                              Double longitude,
                              Double latitude,
                              Double radius,
                              String after,
      Integer hits)
      throws Exception;

  /**
   * Searches for specific text in a given directory and field using the Lucene library.
   *
   * @param dir The directory to search in.
   * @param field The field to search in.
   * @param text The text to search for.
   * @param after Return only results after this value. If null, returns all results.
   * @param hits The maximum number of hits to return.
   * @return A list of strings representing the search results.
   * @throws Exception If an error occurs during the search process.
   */
  List<String> search(String dir,
                      String field,
                      String text,
                      String after,
                      Integer hits)
    throws Exception;
}
