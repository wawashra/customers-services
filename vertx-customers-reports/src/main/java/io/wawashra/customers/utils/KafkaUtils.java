package io.wawashra.customers.utils;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer;
import io.vertx.reactivex.kafka.client.producer.KafkaProducer;

public class KafkaUtils {


    private static final String BOOT_SERVER = "kafka.bootstrap.servers";
    private static final String KEY_SERIALIZER = "kafka.key.serializer";
    private static final String VALUE_SERIALIZER = "kafka.value.serializer";
    
    public static final String CREATE_TOPIC = "customer-create";
    public static final String UPDATE_TOPIC = "customer-update";
    public static final String DELETE_TOPIC = "customer-delete";
    
    private static final String KEY_DESERIALIZER = "kafka.key.deserializer";
    private static final String VALUE_DESERIALIZER = "kafka.value.deserializer";
    private static final String AUTO_COMMIT = "kafka.enable.auto.commit";
    private static final String AUTO_OFFSET = "kafka.auto.offset.reset";
    private static final String GROUP_ID = "kafka.group.id";
    private static final String ACKS = "kafka.acks";
    
    

    private KafkaUtils() {

    }
    
    /**
     * Build KafkaProducer
     *
     * @param Vertx context
     * @param JsonObject configurations
     * @return KafkaProducer
     */

    public static KafkaProducer<JsonObject, JsonObject> createProducer(Vertx vertx, JsonObject configurations) {
    	Map<String, String> config = new HashMap<>();
    	config.put("bootstrap.servers", configurations.getString(BOOT_SERVER));
    	config.put("key.serializer", configurations.getString(KEY_SERIALIZER));
    	config.put("value.serializer", configurations.getString(VALUE_SERIALIZER));
    	config.put("acks", configurations.getString(ACKS));
        return KafkaProducer.create(vertx, config, JsonObject.class, JsonObject.class);
    }
    
    /**
     * Build KafkaProducer
     *
     * @param Vertx context
     * @param JsonObject configurations
     * @return KafkaProducer
     */

    public static KafkaConsumer<JsonObject, JsonObject> createConsumer(Vertx vertx, JsonObject configurations) {
    	Map<String, String> config = new HashMap<>();
    	config.put("bootstrap.servers", configurations.getString(BOOT_SERVER));
    	config.put("key.deserializer", configurations.getString(KEY_DESERIALIZER));
    	config.put("value.deserializer", configurations.getString(VALUE_DESERIALIZER));
    	config.put("group.id", configurations.getString(GROUP_ID));
    	config.put("auto.offset.reset", configurations.getString(AUTO_OFFSET));
    	config.put("enable.auto.commit", configurations.getString(AUTO_COMMIT));
        return KafkaConsumer.create(vertx, config, JsonObject.class, JsonObject.class);
    }

}
