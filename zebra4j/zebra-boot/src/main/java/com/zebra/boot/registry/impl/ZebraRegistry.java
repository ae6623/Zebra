package com.zebra.boot.registry.impl;

import com.zebra.boot.registry.IRegistry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * Created by ae6623 on 2016/11/22.
 *
 * Zebra默认的注册中心实现类,供网关进行调用
 */
@Component
public class ZebraRegistry implements IRegistry,Watcher {

    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(ZebraRegistry.class);

    /**
     * 线程同步
     */
    private static CountDownLatch latch = new CountDownLatch(1);



    @Override
    public void register(String serviceName, String address) {

    }

    @Override
    public void process(WatchedEvent watchedEvent) {

    }
}
