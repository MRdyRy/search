//package com.redis.search.load;
//
//import com.redis.search.service.Search;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import redis.clients.jedis.Jedis;
//
//import java.util.Random;
//
//@Component
//@Order(1)
//@Slf4j
//public class InitLoadData implements CommandLineRunner {
//
//    @Value("${initData}")
//    private boolean isInitiate;
//
//    @Value("${number.of.sample}")
//    private int numberSample;
//
//    @Autowired
//    Jedis jedis;
//
//    @Autowired
//    Search search;
//
//
//
//    @Override
//    public void run(String... args) throws Exception {
//        List<Feature> featuresCollection = new ArrayList<Feature>();
//        if (isInitiate) {
//            log.info("initial data start ....................!!!");
//            try {
//                log.info("Start proses load data with value is initiate : {}" + isInitiate);
//
//                for (int i = 0; i < numberSample; i++) {
//                    Feature feature = new Feature();
//                    feature.setFeatureName(randomString() + " " + randomString());
//                    feature.setValuePath(randomString() + "idx:" + i);
//                    feature.setId(String.valueOf(i));
//                    featuresCollection.add(feature);
//
//                    log.info("create data dummy : {}"+feature);
//                }
//                String json = new Gson().toJson(featuresCollection);
//                jedis.del("all");
//                jedis.set("all", json);
//
//
//                log.info("seeding data .......! ");
//                search.seedDocuments();
//                log.info("creating index .......!");
//                search.createIndex();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (jedis != null && jedis.isConnected()) {
//                    jedis.close();
//                }
//            }
//        }
//
//
//    }
//
//
//    private String randomString() {
//        // create a string of all characters
//        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//
//        // create random string builder
//        StringBuilder sb = new StringBuilder();
//
//        // create an object of Random class
//        Random random = new Random();
//
//        // specify length of random string
//        int length = 7;
//
//        for (int i = 0; i < length; i++) {
//
//            // generate random index number
//            int index = random.nextInt(alphabet.length());
//
//            // get character specified by index
//            // from the string
//            char randomChar = alphabet.charAt(index);
//
//            // append the character to string builder
//            sb.append(randomChar);
//        }
//
//        String randomString = sb.toString();
//        return randomString;
//    }
//
//}
