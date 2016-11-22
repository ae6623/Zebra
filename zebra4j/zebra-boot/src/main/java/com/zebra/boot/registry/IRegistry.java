package com.zebra.boot.registry;

/**
 * Created by ae6623 on 2016/11/22.
 * ervice 注册中心接口
 * 负责响应网关的服务注册,只要实现此接口,就可以担任整个框架的服务中心的角色
 * 可以参考实现类实现自己的注册中心
 */
public interface IRegistry {

    /**
     * 服务注册信息
     *
     * @param serviceName 服务名称 比如 zebra/service
     * @param address     服务真实地址 比如 ip:port
     */
    void register(String serviceName, String address);
}
