package com.zebra.boot.registry;

import com.zebra.boot.registry.impl.ZebraRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by ae6623 on 2016/11/23.
 * 通过该类配置注册中心,prefix注解是为了读取application.properties中的配置的前缀变量,比如registry.servers
 */
@Configuration
@ConfigurationProperties(prefix = "registry")
public class RegistryConfig {

    /**
     * 会被Spring boot 自动塞入进来
     */
    private String servers;

    /**
     * 返回注册中心实例
     * @return
     */
    @Bean
    public IRegistry serviceRegistry() {
        return new ZebraRegistry(servers);
    }

    /**
     * 供Spring boot 自动注入
     * @param servers
     * @return
     */
    public void setServers(String servers) {
        this.servers = servers;
    }
}
