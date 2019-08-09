/*Demo service define file,can be generated to inteface files*/
/*Here test the 7 kind of data type*/
["java:package:org.apache.dubbo.rpc.protocol.ice"]
module demo
{
    interface DemoService
    {
        idempotent string sayHello(string name);

        bool hasName(bool hasName);

        string sayHelloTimes(string name, int times);

        float getFloatValue(float value);

        double getDoubleValue(double value);

        long getLongValue(long value);

        short getShortValue(short value);

        string context(string name);

    }
}
