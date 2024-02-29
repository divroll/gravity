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
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;

/**
 * The RmiClientFactory class is a factory for creating RMI client sockets.
 */
public class RmiClientFactory implements RMIClientSocketFactory, Serializable {
    /**
     * The host variable represents the remote host to connect to.
     */
    private String host;

    /**
     * The RmiClientFactory class represents a factory for creating RMI client sockets.
     *
     * @param host the remote host to connect to
     */
    public RmiClientFactory(final String host) {
        this.host = host;
    }

    /**
     * Creates a client socket and connects it to the specified remote host at the specified
     * remote port.
     *
     * @param host the remote host to connect to
     * @param port the port on the remote host to connect to
     * @return a Socket object representing the connection
     * @throws IOException if an I/O error occurs while creating the socket
     */
    @Override
    public Socket createSocket(final String host, final int port) throws IOException {
        return new Socket(InetAddress.getByName(this.host), port);
    }
}
