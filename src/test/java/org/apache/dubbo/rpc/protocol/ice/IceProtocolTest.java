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

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.ProxyFactory;
import org.apache.dubbo.rpc.protocol.ice.demo.DemoService;
import org.apache.dubbo.rpc.protocol.ice.demo.DemoServicePrx;
import org.apache.dubbo.rpc.protocol.ice.user.UserService;
import org.apache.dubbo.rpc.protocol.ice.user.UserServicePrx;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * IceProtocolTest
 */
public class IceProtocolTest {

    @Test
    public void testIceProtocol() {
        DemoServiceImpl server = new DemoServiceImpl();
        ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();
        URL url = URL.valueOf(IceProtocol.NAME + "://127.0.0.1:3333/" + DemoService.class.getName() + "?version=1.0.0");
        Exporter<DemoService> exporter = protocol.export(proxyFactory.getInvoker(server, DemoService.class, url));
        Invoker<DemoServicePrx> invoker = protocol.refer(DemoServicePrx.class, url);
        DemoServicePrx client = proxyFactory.getProxy(invoker);
        Assertions.assertEquals("Hello, 33", client.sayHello("33"));
        Assertions.assertTrue(client.hasName(true));
        Assertions.assertEquals("Hello, 33", client.sayHello("33"));
        Assertions.assertEquals("Hello, 33. Hello, 33. ", client.sayHelloTimes("33", 2));
        Assertions.assertEquals(Float.MAX_VALUE, client.getFloatValue(Float.MAX_VALUE));
        Assertions.assertEquals(Double.MAX_VALUE, client.getDoubleValue(Double.MAX_VALUE));
        Assertions.assertEquals(Long.MAX_VALUE, client.getLongValue(Long.MAX_VALUE));
        Assertions.assertEquals(Short.MAX_VALUE, client.getLongValue(Short.MAX_VALUE));
        invoker.destroy();
        exporter.unexport();
    }



    @Test
    public void testIceProtocolMultipleServices() {
        DemoServiceImpl demoServer = new DemoServiceImpl();
        ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();
        URL demoUrl = URL.valueOf(IceProtocol.NAME + "://127.0.0.1:3333/" + DemoService.class.getName() + "?version=1.0.0");
        Exporter<DemoService> demoExporter = protocol.export(proxyFactory.getInvoker(demoServer, DemoService.class, demoUrl));
        Invoker<DemoServicePrx> demoInvoker = protocol.refer(DemoServicePrx.class, demoUrl);
        DemoServicePrx client1 = proxyFactory.getProxy(demoInvoker);
        Assertions.assertEquals("Hello, 33", client1.sayHello("33"));



        UserServiceImpl userServer = new UserServiceImpl();
        URL userUrl = URL.valueOf(IceProtocol.NAME + "://127.0.0.1:3333/" + UserService.class.getName() + "?version=1.0.0");
        Exporter<UserService> userExporter = protocol.export(proxyFactory.getInvoker(userServer, UserService.class, userUrl));
        Invoker<UserServicePrx> userInvoker = protocol.refer(UserServicePrx.class, userUrl);
        UserServicePrx client2 = proxyFactory.getProxy(userInvoker);
        Assertions.assertEquals("Hello, 33", client2.sayHello("33"));

        demoInvoker.destroy();
        demoExporter.unexport();
        userInvoker.destroy();
        userExporter.unexport();
    }
}
