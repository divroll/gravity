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
package com.divroll.datafactory.indexers.impl;

import com.divroll.datafactory.database.impl.DatabaseManagerImpl;
import com.divroll.datafactory.indexers.LuceneIndexer;
import jetbrains.exodus.lucene.ExodusDirectory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LatLonPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial3d.Geo3DPoint;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The LuceneIndexerImpl class is an implementation of the LuceneIndexer interface.
 * It provides methods for indexing and searching entities using the Lucene library.
 */
public final class LuceneIndexerImpl implements LuceneIndexer {
  /**
   * IndexSearcher is a class in the Lucene library that provides
   * search functionality on an index.
   */
  private IndexSearcher searcher;
  /**
   * The "writer" variable represents an instance of the "IndexWriter" class.
   * It is used for indexing documents in the Lucene library.
   */
  private IndexWriter writer;

  /**
   * The private static variable instance represents an implementation of the
   * interface.
   */
  private static LuceneIndexerImpl instance;

  /**
   * Private constructor for LuceneIndexerImpl.
   * This constructor ensures that only one instance of LuceneIndexerImpl can be created.
   * It throws a RuntimeException if an attempt is made to create a new instance.
   */
  private LuceneIndexerImpl() {
    if (instance != null) {
      throw new RuntimeException("Only one instance of LuceneIndexer is allowed");
    }
}

  /**
   * Retrieves the instance of the LuceneIndexerImpl class.
   * If the instance is not yet created, a new instance will be created.
   *
   * @return The instance of the LuceneIndexerImpl class.
   */
  public static LuceneIndexerImpl getInstance() {
    if (instance == null) {
      instance = new LuceneIndexerImpl();
    }
    return instance;
  }

  /**
   * Indexes a document in the given directory with the specified entity ID, longitude,
   * and latitude.
   *
   * @param dir       The directory where the index is stored.
   * @param entityId  The ID of the entity to be indexed.
   * @param longitude The longitude value of the entity's location.
   * @param latitude  The latitude value of the entity's location.
   * @return True if the index operation is successful, false otherwise.
   * @throws Exception If an error occurs during the indexing process.
   */
  @Override public Boolean index(final String dir,
                                 final String entityId,
                                 final Double longitude,
                                 final Double latitude)
      throws Exception {
    ExodusDirectory exodusDirectory = DatabaseManagerImpl.getInstance().getExodusDirectory(dir);
    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    return exodusDirectory.getEnvironment().computeInTransaction(txn -> {
      try {
        IndexWriter indexWriter =  new IndexWriter(exodusDirectory, iwc);
        Document doc = new Document();
        doc.add(new StoredField("entityId", entityId));
        doc.add(new LatLonPoint("latlon", latitude, longitude));
        Geo3DPoint point = new Geo3DPoint("geo3d", latitude, longitude);
        doc.add(point);
        indexWriter.addDocument(doc);
        indexWriter.close();
        return true;
      } catch (IOException e) {
        return false;
      }
    });
  }

  /**
   * Indexes the given text in the specified field for a given entity ID.
   *
   * @param dir the directory where the index is stored
   * @param entityId the ID of the entity to index
   * @param field the name of the field to index
   * @param text the text to index
   * @return true if the text was successfully indexed, false otherwise
   * @throws Exception if an error occurs while indexing the text
   */
  @Override public Boolean index(final String dir,
                                 final String entityId,
                                 final String field,
                                 final String text) throws Exception {
    ExodusDirectory exodusDirectory = DatabaseManagerImpl.getInstance().getExodusDirectory(dir);
    Analyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    return exodusDirectory.getEnvironment().computeInTransaction(txn -> {
      try {
        IndexWriter indexWriter =  new IndexWriter(exodusDirectory, iwc);
        Document doc = new Document();
        doc.add(new StoredField("entityId", entityId));
        doc.add(new TextField(field, text, Field.Store.YES));
        indexWriter.addDocument(doc);
        indexWriter.close();
        return true;
      } catch (IOException e) {
        return false;
      }
    });
  }

  /**
   * Searches for neighbors within a given radius of a specified location.
   *
   * @param dir      The name of the directory to search in.
   * @param longitude The longitude coordinate of the center of the search area.
   * @param latitude  The latitude coordinate of the center of the search area.
   * @param radius    The radius in which to search for neighbors from the center location.
   * @param after     A string representing the after parameter.
   * @param hits      The maximum number of neighbors to retrieve.
   * @return A list of neighbor IDs within the specified radius.
   * @throws Exception If an error occurs during the search process.
   */
  @Override
  public List<String> searchNeighbor(final String dir,
                                     final Double longitude,
                                     final Double latitude,
                                     final Double radius,
                                     final String after,
                                     final Integer hits) throws Exception {
    ExodusDirectory exodusDirectory = DatabaseManagerImpl.getInstance().getExodusDirectory(dir);
    Analyzer analyzer = new StandardAnalyzer();
    List<String> neighbors = new ArrayList<>();
    return exodusDirectory.getEnvironment().computeInTransaction(txn -> {
      try {
        IndexReader reader = DirectoryReader.open(exodusDirectory);
        searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(LatLonPoint.newDistanceQuery("latlon",
            latitude, longitude, radius), hits);
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
          Document doc = searcher.doc(scoreDoc.doc);
          neighbors.add(doc.get("entityId"));
        }
        reader.close();
      } catch (IOException e) {
        txn.abort();
        throw new UncheckedIOException(e);
      }
      return neighbors;
    });
  }

  /**
   * Searches for documents in the given directory based on the specified field and query string.
   *
   * @param dir            The directory path where the documents are stored.
   * @param field          The field to search within.
   * @param queryString    The query string to search for.
   * @param after          The specific value after which to start the search (optional).
   * @param hits           The maximum number of search results to retrieve.
   * @return A list of entity IDs matching the search criteria.
   * @throws Exception     If an error occurs during the search.
   */
  @Override public List<String> search(final String dir,
                                       final String field,
                                       final String queryString,
                                       final String after,
                                       final Integer hits)
      throws Exception {
    ExodusDirectory exodusDirectory = DatabaseManagerImpl.getInstance().getExodusDirectory(dir);
    Analyzer analyzer = new StandardAnalyzer();
    List<String> entityIds = new ArrayList<>();
    return exodusDirectory.getEnvironment().computeInTransaction(txn -> {
      try {
        IndexReader reader = DirectoryReader.open(exodusDirectory);
        searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse(queryString);
        TopDocs docs = searcher.search(query, hits);
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
          Document doc = searcher.doc(scoreDoc.doc);
          System.out.println("score: "
                  + scoreDoc.score
                  + " -- " + field
                  + ": "
                  + doc.get(field));
          entityIds.add(doc.get("entityId"));
        }
        reader.close();
      } catch (IOException e) {
        txn.abort();
        throw new UncheckedIOException(e);
      } catch (ParseException e) {
        txn.abort();
      }
      return entityIds;
    });
  }
}
