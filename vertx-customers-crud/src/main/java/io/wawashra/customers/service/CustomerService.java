package io.wawashra.customers.service;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.kafka.client.producer.KafkaProducer;
import io.vertx.reactivex.kafka.client.producer.KafkaProducerRecord;
import io.wawashra.customers.model.Customer;
import io.wawashra.customers.repository.CustomerRepository;
import io.wawashra.customers.utils.KafkaUtils;

import java.util.List;

public class CustomerService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

	private final CustomerRepository customerRepository;
	private final KafkaProducer<JsonObject, JsonObject> kafkaProducer;

	public CustomerService(CustomerRepository customerRepository, KafkaProducer<JsonObject, JsonObject> kafkaProducer) {
		this.customerRepository = customerRepository;
		this.kafkaProducer = kafkaProducer;
	}

	public Single<List<Customer>> getAll() {
		return customerRepository.getAll();
	}

	public Maybe<Customer> getById(String id) {
		return customerRepository.getById(id);
	}

	public Maybe<Customer> insert(Customer customer) {
		return customerRepository.insert(customer).doOnSuccess(insertedCustomer -> {
			LOGGER.info(String.format("Producer msg to kafka about insert new one %s", insertedCustomer.getId()));
			produceCustomerToTopic(KafkaUtils.CREATE_TOPIC, JsonObject.mapFrom(insertedCustomer));

		});
	}

	public Maybe<Customer> update(String id, Customer customer) {
		return customerRepository.update(id, customer).doOnSuccess(updatedCustomer -> {
			LOGGER.info(String.format("Producer msg to kafka about update new one %s", updatedCustomer.getId()));
			produceCustomerToTopic(KafkaUtils.UPDATE_TOPIC, JsonObject.mapFrom(updatedCustomer));
		});
	}

	public Maybe<Customer> delete(String id) {
		return customerRepository.delete(id).doOnSuccess(deletedCustomer -> {
			LOGGER.info(String.format("Producer msg to kafka about delete new one %s", deletedCustomer.getId()));
			produceCustomerToTopic(KafkaUtils.DELETE_TOPIC, JsonObject.mapFrom(deletedCustomer));

		});
	}

	private void produceCustomerToTopic(String topicName, JsonObject customer) {
		KafkaProducerRecord<JsonObject, JsonObject> record = KafkaProducerRecord.create(topicName, customer);

		kafkaProducer.send(record, handler -> {
			
			LOGGER.info("Message " + record.value() + " written on topic=" + handler.result().getTopic()
					+ ", partition=" + handler.result().getPartition() + ", offset=" + handler.result().getOffset() + "time " + handler.result().getTimestamp() );
		
		}).exceptionHandler(handler -> {
			LOGGER.error(String.format("Something bad happened when produceing %s to topic %s", customer, topicName));
		});

	}

}
