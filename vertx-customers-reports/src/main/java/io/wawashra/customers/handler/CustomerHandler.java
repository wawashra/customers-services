package io.wawashra.customers.handler;

import java.util.Calendar;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer;
import io.wawashra.customers.model.CreatedCustomer;
import io.wawashra.customers.model.DeletedCustomer;
import io.wawashra.customers.service.CustomerCreatedService;
import io.wawashra.customers.service.CustomerDeletedService;
import io.wawashra.customers.utils.KafkaUtils;

public class CustomerHandler {

	private CustomerCreatedService customerCreatedService;
	private CustomerDeletedService customerDeletedService;
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerHandler.class);

	public CustomerHandler(CustomerCreatedService customerCreatedService, CustomerDeletedService customerDeletedService) {
		this.customerCreatedService = customerCreatedService;
		this.customerDeletedService = customerDeletedService;
	}

	public void getAllCreated(RoutingContext rc) {
        final String month = rc.queryParams().get("month");

		customerCreatedService.getAll(month).subscribe(result -> onSuccessResponse(rc, 200, result),
				throwable -> onErrorResponse(rc, 400, throwable));
	}

	public void getAllDeleted(RoutingContext rc) {
        final String day = rc.queryParams().get("day");
        

		customerDeletedService.getAll(day).subscribe(result -> onSuccessResponse(rc, 200, result),
				throwable -> onErrorResponse(rc, 400, throwable));
	}
	
	public void consumeCustomerFromCreatedTopic(KafkaConsumer<JsonObject, JsonObject> kafkaConsumer) {

		kafkaConsumer.subscribe(KafkaUtils.CREATE_TOPIC).handler(record -> {
			LOGGER.info(String.format("kafkaConsumer consumeing value %s from topic %s", KafkaUtils.CREATE_TOPIC, record.value()));
			
			JsonObject insertedCreatedCustomer = record.value();

			this.customerCreatedService.insert(new CreatedCustomer(insertedCreatedCustomer)).subscribe(res -> {
				LOGGER.info(String.format("Customer reports service will insert a %s record from topic %s", insertedCreatedCustomer, KafkaUtils.CREATE_TOPIC));
				kafkaConsumer.commit(commitResult -> {
					
					if(commitResult.succeeded())
							LOGGER.info(String.format("Customer reports service done committed on kafka for record %s on %s ", insertedCreatedCustomer, KafkaUtils.CREATE_TOPIC));
				});
				
			}, err -> {
				LOGGER.error(String.format("Something bad happend on kafkaConsumer inside topic %s ", KafkaUtils.CREATE_TOPIC));
			});
		});
	}
	
	
	public void consumeCustomerFromUpdatedTopic(KafkaConsumer<JsonObject, JsonObject> kafkaConsumer) {

		kafkaConsumer.subscribe(KafkaUtils.UPDATE_TOPIC).handler(record -> {
			LOGGER.info(String.format("kafkaConsumer consumeing value %s from topic %s", KafkaUtils.UPDATE_TOPIC, record.value()));
			
			JsonObject insertedCreatedCustomer = record.value();
		
			CreatedCustomer updatedCreatedCustomer = new CreatedCustomer(insertedCreatedCustomer);

			this.customerCreatedService.update(updatedCreatedCustomer.getId(), updatedCreatedCustomer).subscribe(res -> {
				LOGGER.info(String.format("Customer reports service will insert a %s record from topic %s", insertedCreatedCustomer, KafkaUtils.UPDATE_TOPIC));
				kafkaConsumer.commit(commitResult -> {
					
					if(commitResult.succeeded())
							LOGGER.info(String.format("Customer reports service done committed on kafka for record %s on %s ", insertedCreatedCustomer, KafkaUtils.UPDATE_TOPIC));
				});
				
			}, err -> {
				LOGGER.error(String.format("Something bad happend on kafkaConsumer inside topic %s ", KafkaUtils.UPDATE_TOPIC));
			});
		});
	}
	
	public void consumeCustomerFromDeleteTopic(KafkaConsumer<JsonObject, JsonObject> kafkaConsumer) {

		kafkaConsumer.subscribe(KafkaUtils.DELETE_TOPIC).handler(record -> {
			LOGGER.info(String.format("kafkaConsumer consumeing value %s from topic %s", KafkaUtils.DELETE_TOPIC, record.value()));
			JsonObject insertedDeletedCustomer = record.value();
			insertedDeletedCustomer.put("deleteAt", Calendar.getInstance().getTime().getTime());

			this.customerDeletedService.insert(new DeletedCustomer(insertedDeletedCustomer)).subscribe(res -> {
				LOGGER.info(String.format("Customer reports service will insert a %s record from topic %s", insertedDeletedCustomer, KafkaUtils.DELETE_TOPIC));
				kafkaConsumer.commit(commitResult -> {
					
					if(commitResult.succeeded())
							LOGGER.info(String.format("Customer reports service done committed on kafka for record %s on %s ", insertedDeletedCustomer, KafkaUtils.DELETE_TOPIC));
				});
				
			}, err -> {
				LOGGER.error(String.format("Something bad happend on kafkaConsumer inside topic %s ", KafkaUtils.DELETE_TOPIC));
			});
		});
	}

	// Generic responses
	private void onSuccessResponse(RoutingContext rc, int status, Object object) {
		rc.response().setStatusCode(status).putHeader("Content-Type", "application/json")
				.end(Json.encodePrettily(object));
	}

	private void onErrorResponse(RoutingContext rc, int status, Throwable throwable) {
		final JsonObject error = new JsonObject().put("error", throwable.getMessage());

		rc.response().setStatusCode(status).putHeader("Content-Type", "application/json")
				.end(Json.encodePrettily(error));
	}

}
