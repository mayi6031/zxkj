package com.zxkj.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissLock {

    @Value("${redis.single.ip}")
    private String singleIp;

    @Value("${redis.single.port}")
    private Integer singlePort;

    /***
     * 创建RedissonClient对象
     *      创建锁、解锁
     * @return
     */
    @Bean
    public RedissonClient redissonClient() {
        //创建Config
        Config config = new Config();
        //集群实现
        //config.useClusterServers().setScanInterval(2000).addNodeAddress("xxxx");
        config.useSingleServer().setAddress("redis://" + singleIp + ":" + singlePort);
        //创建RedissonClient
        return Redisson.create(config);
    }
}
