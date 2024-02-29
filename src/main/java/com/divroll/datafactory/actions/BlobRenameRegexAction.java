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
package com.divroll.datafactory.actions;

import org.immutables.value.Value;

/**
 * Represents an action to rename a blob using a regular expression.
 */
@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PRIVATE)
public interface BlobRenameRegexAction extends EntityAction {
  /**
   * Retrieves the replacement string for a BlobRenameRegexAction.
   * The replacement string is used to replace the matched portion
   * of the blob name with a new value.
   *
   * @return The replacement string.
   */
  String replacement();

  /**
   * Retrieves the regular expression pattern for a BlobRenameRegexAction.
   * The regular expression pattern is used to match the portion of the
   * blob name that will be replaced with a new value.
   *
   * @return The regular expression pattern.
   */
  String regexPattern();
}
