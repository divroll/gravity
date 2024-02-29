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
package com.divroll.datafactory;

import com.divroll.datafactory.builders.Blob;
import com.divroll.datafactory.builders.Entity;
import com.divroll.datafactory.builders.BlobBuilder;
import com.divroll.datafactory.builders.EntityBuilder;
import com.divroll.datafactory.builders.queries.BlobQuery;
import com.divroll.datafactory.builders.queries.LinkQuery;
import com.divroll.datafactory.properties.EmbeddedArrayIterable;
import com.divroll.datafactory.properties.EmbeddedEntityIterable;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Marshaller class is used to build a Entity for remote transmission.
 * It provides methods to set the entity, link queries, and blob queries, and a build
 */
public class Marshaller {
  /**
   * Represents an Entity object in the system.
   * This class contains information such as the entity type, ID, namespace,
   * property map, blobs, links, and more.
   *
   * It should be constructed using the appropriate builder class.
   *
   * Private field representing the entity.
   */
  private jetbrains.exodus.entitystore.Entity entity;
  /**
   * Represents a list of LinkQuery objects.
   */
  private List<LinkQuery> linkQueries;
  /**
   * Represents a list of queries to rename blobs.
   */
  private List<BlobQuery> blobQueries;

  /**
   * Adds the provided entity to the marshaller.
   *
   * @param entity the entity to add
   * @return the updated marshaller
   */
  public Marshaller with(@NotNull final jetbrains.exodus.entitystore.Entity entity) {
    if (this.entity != null) {
      throw new IllegalArgumentException("Entity is already set");
    }
    this.entity = entity;
    return this;
  }

  /**
   * Adds the provided LinkQueries to the Marshaller.
   *
   * @param linkQueries an array of LinkQuery objects to add to the Marshaller
   * @return the updated Marshaller object
   * @throws IllegalArgumentException if LinkQuery is already set
   */
  public Marshaller with(@NotNull final LinkQuery[] linkQueries) {
    if (this.linkQueries != null) {
      throw new IllegalArgumentException("LinkQuery is already set");
    }
    this.linkQueries = Arrays.asList(linkQueries);
    return this;
  }

  /**
   * Adds the provided BlobQueries to the Marshaller.
   *
   * @param blobQueries an array of BlobQuery objects to add to the Marshaller
   * @return the updated Marshaller object
   */
  public Marshaller with(@NotNull final BlobQuery[] blobQueries) {
    if (this.blobQueries != null) {
      throw new IllegalArgumentException("BlobQuery is already set");
    }
    this.blobQueries = Arrays.asList(blobQueries);
    return this;
  }

  /**
   * Builds a {@linkplain jetbrains.exodus.entitystore.Entity} into a {@linkplain Entity} for
   * remote transmission. This method should be called within a database
   * {@linkplain StoreTransaction}.
   *
   * @return {@code entity}
   */
  public Entity build() {
    final EntityBuilder builder = new EntityBuilder();

    entity.getPropertyNames().forEach(propertyName -> {
      Comparable propertyValue = entity.getProperty(propertyName);
      if (propertyValue != null) {
        if (propertyValue instanceof EmbeddedEntityIterable) {
          builder.putPropertyMap(propertyName,
              ((EmbeddedEntityIterable) propertyValue).asObject());
        } else if (propertyValue instanceof EmbeddedArrayIterable) {
          builder.putPropertyMap(propertyName,
              (Comparable) ((EmbeddedArrayIterable) propertyValue).asObject());
        } else {
          builder.putPropertyMap(propertyName, propertyValue);
        }
      }
    });

    List<Blob> blobs = new ArrayList<>();
    Multimap<String, Entity> links = ArrayListMultimap.create();

    if (linkQueries == null) {
      linkQueries = new ArrayList<>();
    }
    linkQueries.forEach(linkQuery -> {
      EntityIterable linkedEntities = entity.getLinks(linkQuery.linkName());
      if (linkQuery.targetEntityId() != null) {
        linkedEntities.forEach(linkedEntity -> {
          if (linkedEntity.getId().toString().equals(linkQuery.targetEntityId())) {
            links.put(linkQuery.linkName(), new Marshaller()
                .with(linkedEntity)
                .build());
          }
        });
      } else {
        linkedEntities.forEach(linkedEntity -> {
          links.put(linkQuery.linkName(), new Marshaller()
              .with(linkedEntity)
              .build());
        });
      }
    });

    if (blobQueries == null) {
      blobQueries = new ArrayList<>();
    }
    blobQueries.forEach(blobQuery -> {
      entity.getBlobNames().forEach(blobName -> {
        if (blobQuery.blobName().equals(blobName) && blobQuery.include()) {
          InputStream blobStream = entity.getBlob(blobName);
          Long blobSize = entity.getBlobSize(blobName);
          blobs.add(new BlobBuilder()
              .blobName(blobName)
              .blobStream(new SimpleRemoteInputStream(blobStream))
              .blobSize(blobSize)
              .build());
        }
      });
    });

    String nameSpace = null;
    Comparable nameSpaceProperty = entity.getProperty(Constants.NAMESPACE_PROPERTY);
    if (nameSpaceProperty != null) {
      nameSpace = String.valueOf(nameSpaceProperty);
    }

    return builder
        .environment(entity.getStore().getLocation())
        .nameSpace(nameSpace)
        .entityType(entity.getType())
        .entityId(entity.getId().toString())
        .blobNames(Iterables.toArray(entity.getBlobNames(), String.class))
        .linkNames(Iterables.toArray(entity.getLinkNames(), String.class))
        .blobs(blobs)
        .links(links)
        .build();
  }
}
