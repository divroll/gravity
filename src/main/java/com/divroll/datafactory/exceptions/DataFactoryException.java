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
package com.divroll.datafactory.exceptions;

import jetbrains.exodus.ExodusException;

/**
 * Custom exception class that is thrown when there is an error in the data factory.
 */
public class DataFactoryException extends ExodusException {
  /**
   * Exception thrown when there is an error in the data factory.
   *
   * @since 0-SNAPSHOT
   */
  public DataFactoryException(String message) {
    super(message);
  }

  /**
   * DataFactoryException is an exception class that is thrown when an error occurs in the data factory.
   * It extends the ExodusException class and provides constructors to set the error message and cause of the exception.
   */
  public DataFactoryException(String message, Throwable cause) {
    super(message, cause);
  }
}
