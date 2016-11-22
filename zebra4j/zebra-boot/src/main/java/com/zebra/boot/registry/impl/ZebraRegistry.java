package com.zebra.boot.registry.impl;

import com.zebra.boot.registry.IRegistry;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by ae6623 on 2016/11/22.
 * <p/>
 * Zebra默认的注册中心实现类,供网关进行调用
 */
@Component
public class ZebraRegistry implements IRegistry, Watcher {

    /**
     * Time_out 心跳超时时间
     */
    private static final int SESSION_TIMEOUT = 6000;

    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(ZebraRegistry.class);

    /**
     * 线程同步
     */
    private static CountDownLatch latch = new CountDownLatch(1);

    /**
     * zk 根节点
     */
    private static final String REGISTRY_PATH = "/registry";

    /**
     * zk 地址前缀
     */
    private static final String ADDRESS_PREFIX = "address-";

    /**
     * zoo
     */
    private ZooKeeper zk;

    /**
     * constructor
     */
    public ZebraRegistry() {

    }

    public ZebraRegistry(String zkServers) {
        try {
            zk = new ZooKeeper(zkServers, SESSION_TIMEOUT, this);
            //在zk创建之前,阻塞线程,让线程处于等待状态
            latch.await();
            logger.info("Zebra is connecting to the zookeeper services : " + zkServers);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void register(String serviceName, String serviceAddress) {

        //根节点路径
        String registryPath = REGISTRY_PATH;

        try {

            if (zk.exists(registryPath, false) == null) {
                //如果木有根节点,则创建一个根节点
                zk.create(registryPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.info("Zebra create a new root node in zookeeper success!");
            }

            //服务节点路径
            String servicePath = registryPath + "/" + serviceName;

            if (zk.exists(servicePath, false) == null) {
                //如果没有服务节点,则创建服务节点
                zk.create(servicePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.info("Zebra create a new Service node in zookeeper success!");
            }

            //ip端口
            String addressPath = servicePath + "/" + ADDRESS_PREFIX;
            String addressNode = zk.create(addressPath, serviceAddress.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info("Zebra create a new address node at " + addressNode, serviceAddress);


        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            //结束线程阻塞,结束等待
            latch.countDown();
        }
    }
}
