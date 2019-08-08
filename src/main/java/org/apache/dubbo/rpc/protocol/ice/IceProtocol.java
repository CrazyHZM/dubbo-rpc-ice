/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dubbo.rpc.protocol.ice;

import com.zeroc.Ice.Object;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.InitializationData;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Properties;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.protocol.AbstractProxyProtocol;

import java.lang.Exception;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;

/**
 * ice protocol support.
 */
public class IceProtocol extends AbstractProxyProtocol {
    public static final int DEFAULT_PORT = 33333;
    public static final String NAME = "ice";

    private static final Map<String, ObjectAdapter> adapterMap = new HashMap<>();


    public IceProtocol() {
        super(RpcException.class);
    }

    @Override
    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    @Override
    protected <T> Runnable doExport(T impl, Class<T> type, URL url) throws RpcException {
        InitializationData initializationData = getInitializationData();
        ObjectAdapter objectAdapter = adapterMap.get(url.getAddress());
        ObjectAdapter adapter;
        Communicator communicator;
        if (objectAdapter == null) {
            try {
                communicator = Util.initialize(initializationData);
                adapter = communicator.createObjectAdapterWithEndpoints(getIdentity(url), getIceEndpoint(url));
                adapter.add((Object) impl, Util.stringToIdentity(getIdentity(url)));

            } catch (Exception e) {
                logger.error("Fail to create ice server(" + url + ") due to create ObjectAdapter fail");
                throw new RpcException("Fail to create ice server(" + url + ") due to create ObjectAdapter fail");
            }
        } else {
            return null;
        }
        adapterMap.put(url.getAddress(), adapter);

        new Thread(() -> {
            logger.info("Start ice Server");
            adapter.activate();
            logger.info("ice Server started.");
        }).start();

        return () -> {
            try {
                logger.info("Close ice Server");
                adapter.destroy();
                communicator.destroy();
                adapterMap.remove(url.getAddress());
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }
        };
    }

    @Override
    protected <T> T doRefer(Class<T> type, URL url) throws RpcException {
        T iceClient;
        try {
            Communicator communicator = Util.initialize(getInitializationData());
            ObjectPrx base = communicator.stringToProxy(String.format("%s:%s", getIdentity(url), getIceEndpoint(url)));

            MethodType methodType = MethodType.methodType(type, ObjectPrx.class);
            MethodHandle aStatic = MethodHandles.lookup().findStatic(type, "checkedCast", methodType);
            iceClient = (T) aStatic.invoke(base);

            return iceClient;
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
            throw new RpcException("Fail to create remote client for service(" + url + "): " + throwable.getMessage(), throwable);
        }
    }

    private InitializationData getInitializationData() {
        InitializationData initializationData = new InitializationData();

        Properties properties = Util.createProperties();
        properties.setProperty("Ice.ThreadPool.Server.Size", "200");
        properties.setProperty("Ice.ThreadPool.Client.Size", "200");
        properties.setProperty("Ice.ThreadPool.Server.SizeMax", "200");
        properties.setProperty("Ice.ThreadPool.Client.SizeMax", "200");

        // 1000 connections
        properties.setProperty("Ice.TCP.Backlog", "1000");
        // ConnectTimeout：10s
        properties.setProperty("Ice.Override.ConnectTimeout", "10000");


        initializationData.properties = properties;
        return initializationData;
    }

    public String getIdentity(URL url) {
        return url.getPort() <= 0 ? url.getHost() : url.getHost() + "_" + url.getPort();
    }


    private String getIceEndpoint(URL url) {

        return String.format("%s -h %s -p %d", "default", url.getHost(), url.getPort());

    }

//    private String getPrxI(String typeName){
//
//    }
}
