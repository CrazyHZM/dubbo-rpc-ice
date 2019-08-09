["java:package:org.apache.dubbo.rpc.protocol.ice"]
module user
{
    interface UserService
    {
        idempotent string sayHello(string name);

        bool hasName(bool hasName);

        string sayHelloTimes(string name, int times);

        float getMoney(float value);

        string context(string name);

    }
}
