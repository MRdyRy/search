package com.redis.search.controller;

import com.redis.search.dto.RequestQuery;
import com.redis.search.service.Search;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("search")
public class SearchController {

    @Autowired
    Search search;

    @PostMapping("/query")
    public ResponseEntity<Map<String,Object>> searcData(@RequestBody RequestQuery requestQuery){
      log.info("hit endpoint /search/query : {} "+requestQuery);
      Map<String,Object> hashData = search.search(requestQuery);
      return ResponseEntity.ok(hashData);
    }


    @PostMapping("/jedis-command")
    public ResponseEntity<Map<String,Object>> searcDataUsingCommand(@RequestBody RequestQuery requestQuery) {
        log.info("hit endpoint /search/jedis-command : {} " + requestQuery);
        Map<String, Object> hashData = search.searchWithJedisCommand(requestQuery);
        return ResponseEntity.ok(hashData);
    }
}
