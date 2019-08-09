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

import com.zeroc.Ice.Current;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.protocol.ice.user.UserService;

/**
 * UserServiceImpl
 */
public class UserServiceImpl implements UserService {
    @Override
    public String sayHello(String name, Current current) {
        return "Hello, " + name;
    }

    @Override
    public boolean hasName(boolean hasName, Current current) {
        return hasName;
    }

    @Override
    public String sayHelloTimes(String name, int times, Current current) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append("Hello, " + name + ". ");
        }
        return sb.toString();
    }

    @Override
    public float getMoney(float value, Current current) {
        return value;
    }

    @Override
    public String context(String name, Current current) {
        return "Hello, " + name + " context, " + RpcContext.getContext().getAttachment("context");
    }
}
