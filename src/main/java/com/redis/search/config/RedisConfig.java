package com.redis.search.config;

import io.redisearch.client.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${hostname.redis}")
    private String host;

    @Value("${port.redis}")
    private String port;

    @Value("${index.name}")
    private String indexName;

    @Bean
    public JedisPool jedisPool() throws URISyntaxException {
        return new JedisPool(new URI(host+":"+port));
    }

    @Bean
    public Client jedisClient() throws URISyntaxException {
        return new Client(indexName,jedisPool());
    }
}
