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

import java.io.Serializable;

/**
 * The Constants class provides constant values used throughout the application.
 */
public final class Constants implements Serializable {
  /**
   * The environment variable for the Java RMI host.
   */
  public static final String JAVA_RMI_HOST_ENVIRONMENT
          = "java.rmi.server.hostname";

  /**
   * The environment variable for the Java RMI port.
   */
  public static final String JAVA_RMI_PORT_ENVIRONMENT
          = "java.rmi.server.port";
  /**
   * The environment variable for the Java RMI test port.
   */
  public static final String JAVA_RMI_TEST_PORT_ENVIRONMENT
          = "java.rmi.server.test.port";
  /**
   * The default value for the Java RMI port.
   */
  public static final String JAVA_RMI_PORT_DEFAULT
          = "1099";
  /**
   * The environment variable for the DataFactory directory.
   */
  public static final String DATAFACTORY_DIRECTORY_ENVIRONMENT
          = "datafactory.dir";
  /**
   * The property for the namespace.
   */
  public static final String NAMESPACE_PROPERTY
          = "____NAMESPACE____";

  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private Constants() {
  }
}
