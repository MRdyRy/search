package com.redis.search.service;

import com.redis.search.dto.RequestQuery;

import java.util.Map;

public interface Search {
    Map<String,Object> search(RequestQuery requestQuery);

    Map<String,Object> searchWithJedisCommand(RequestQuery requestQuery);
}
