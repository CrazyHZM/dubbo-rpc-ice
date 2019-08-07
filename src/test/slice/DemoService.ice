["java:package:org.apache.dubbo.rpc.protocol.ice"]
module demo
{
    interface DemoService
    {
        idempotent void sayHello(int delay);
        void shutdown();
    }
}
