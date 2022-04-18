package com.redis.search.dto;

import lombok.Data;

@Data
public class RequestQuery {
    private String queryString;
    private int offset;
    private int limit;
    private String sortBy;
    private Boolean isAscending;
}
