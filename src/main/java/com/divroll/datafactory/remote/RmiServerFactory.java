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
package com.divroll.datafactory.remote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;

/**
 * The RmiServerFactory class is a factory for creating RMI server sockets.
 */
public class RmiServerFactory implements RMIServerSocketFactory {
    /**
     * The host to which the server socket should be bound.
     */
    private String host;

    /**
     * The RmiServerFactory class is a factory for creating RMI server sockets.
     *
     * @param host the host to which the server socket should be bound
     */
    public RmiServerFactory(final String host) {
        this.host = host;
    }

    /**
     * Creates a new server socket on the specified port and binds it to the specified IP address.
     *
     * @param port the port number
     * @return the newly created ServerSocket
     * @throws IOException if an I/O error occurs when opening the server socket
     */
    @Override
    public ServerSocket createServerSocket(final int port) throws IOException {
        // Creates a server socket on a specified port, and binds it to the specified IP address.
        return new ServerSocket(port, 0, InetAddress.getByName(host));
    }
}
