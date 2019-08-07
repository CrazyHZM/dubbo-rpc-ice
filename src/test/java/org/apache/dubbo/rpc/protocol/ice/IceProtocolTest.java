package org.apache.dubbo.rpc.protocol.ice;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.ProxyFactory;
import org.apache.dubbo.rpc.protocol.ice.demo.DemoService;
import org.apache.dubbo.rpc.protocol.ice.demo.DemoServicePrx;
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
        client.sayHello(1, null);
//        Assertions.assertEquals("Hello, haha", result);
        invoker.destroy();
        exporter.unexport();
    }
}
