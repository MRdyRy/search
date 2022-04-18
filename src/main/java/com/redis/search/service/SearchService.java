package com.redis.search.service;

import com.redis.search.Util.RediSearchCommands;
import com.redis.search.dto.RequestQuery;
import io.redisearch.Client;
import io.redisearch.Document;
import io.redisearch.Query;
import io.redisearch.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SearchService implements Search{

    @Autowired
    Client client;
    @Autowired
    JedisPool jedisPool;

    @Value("${index.name}")
    private String indexName;

    @Override
    public Map<String,Object> search(RequestQuery requestQuery){
        Map<String,Object> returnValue = new HashMap<>();
        Map<String,Object> resultMeta = new HashMap<>();

        Query query = new Query(requestQuery.getQueryString());
        if(requestQuery.getSortBy()==null && !requestQuery.getSortBy().isEmpty())
            query.setSortBy(requestQuery.getSortBy(),requestQuery.getIsAscending());

        SearchResult searchResult = client.search(query);

        resultMeta.put("queryString",requestQuery.getQueryString());
        resultMeta.put("totalOffset",searchResult.totalResults);
        resultMeta.put("offset", requestQuery.getOffset());
        resultMeta.put("limit",requestQuery.getLimit());

        returnValue.put("meta",resultMeta);
        returnValue.put("raw_docs",searchResult.docs);

        List<Map<String,Object>> docsList = new ArrayList<>();

        List<Document> docs = searchResult.docs;

        for (Document doc : docs) {
            Map<String,Object> props = new HashMap<>();
            Map<String,Object> meta = new HashMap<>();
            meta.put("id",doc.getId());
            meta.put("score",doc.getScore());
            doc.getProperties().forEach(e->{
                props.put(e.getKey(), e.getValue());
            });

            Map<String, Object> docMeta = new HashMap<>();
            docMeta.put("meta",meta);
            docMeta.put("fields",props);
            docsList.add(docMeta);
        }

        returnValue.put("docs",docsList);

        return returnValue;
    }

    @Override
    public Map<String, Object> searchWithJedisCommand(RequestQuery requestQuery) {
        Map<String,Object> returnValue = new HashMap<>();
        Map<String,Object> resultMeta = new HashMap<>();

        try(Jedis jedis = jedisPool.getResource()){

            List<String> commandParams = new ArrayList<>();
            commandParams.add(indexName);
            commandParams.add(requestQuery.getQueryString());
            commandParams.add("WITHSCORES");

            //set limit
            commandParams.add("LIMIT");
            commandParams.add(String.valueOf(requestQuery.getOffset()));
            commandParams.add(String.valueOf(requestQuery.getLimit()));

            if(requestQuery.getSortBy()!=null && !requestQuery.getSortBy().isEmpty()){
                commandParams.add("SORTBY");
                commandParams.add(requestQuery.getSortBy());
                commandParams.add(requestQuery.getIsAscending()?"ASC":"DESC");
            }

            log.info("command params : {}"+commandParams);
            List result = (ArrayList)jedis.sendCommand(
                    RediSearchCommands.Command.SEARCH,
                    commandParams.toArray(new String[0]));


            Long totalResults = (Long) result.get(0);
            List docs = new ArrayList<>(result.size() - 1);
            int stepForDoc = 3; // iterate over doc_id/score/values

            List<Map<String,Object>> docList = new ArrayList<>();


            if (totalResults != 0) {
                for (int i = 1; i < result.size(); i += stepForDoc) {

                    Map<String,Object> meta = new HashMap<>();
                    String docId = new String((byte[]) result.get(i));

                    Double score = Double.valueOf(new String((byte[]) result.get(i+1)));
                    meta.put("id", docId);
                    meta.put("score", score);

                    // parse the list of fields and create a map of it
                    Map<String,String> fieldsMap = new HashMap<>();
                    List<byte[]> fields =  (List<byte[]>) result.get(i + 2);
                    for (int j = 0; j < fields.size(); j += 2) {
                        String fieldName = new String((byte[]) fields.get(j));
                        String fieldValue = new String((byte[]) fields.get(j+1));
                        fieldsMap.put(fieldName, fieldValue);
                    }

                    Map<String,Object>doc = new HashMap<>();
                    doc.put("meta", meta);
                    doc.put("fields", fieldsMap);
                    docList.add(doc);
                }
            }

            resultMeta.put("totalResults", totalResults);
            resultMeta.put("queryString", requestQuery.getQueryString());
            resultMeta.put("offset", requestQuery.getOffset());
            resultMeta.put("limit", requestQuery.getLimit());

            returnValue.put("meta", resultMeta);
            returnValue.put("docs", docList);
            return returnValue;

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
