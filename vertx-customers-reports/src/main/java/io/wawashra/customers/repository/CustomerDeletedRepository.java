package io.wawashra.customers.repository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.wawashra.customers.model.DeletedCustomer;
import io.wawashra.customers.model.DeletedCustomerDTO;

public class CustomerDeletedRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerDeletedRepository.class);

	private static final String COLLECTION_NAME = "deleted-customers";

	private final MongoClient client;

	public CustomerDeletedRepository(MongoClient client) {
		this.client = client;
	}

	public Single<List<DeletedCustomerDTO>> getAll() {
		final JsonObject query = new JsonObject();

		return client.rxFind(COLLECTION_NAME, query).flatMap(result -> {
			final List<DeletedCustomerDTO> customers = new ArrayList<>();
			result.forEach(customer -> customers.add(new DeletedCustomerDTO(customer)));
			LOGGER.info(String.format("Read %s customers", customers.size()));
			return Single.just(customers);
		});
	}
	
	public Maybe<DeletedCustomer> insert(DeletedCustomer customer) {
		return client.rxInsert(COLLECTION_NAME, JsonObject.mapFrom(customer)).flatMap(result -> {
			customer.setId(result);
			
			LOGGER.info(String.format("Inserted one customer %s", JsonObject.mapFrom(customer)));

			return Maybe.just(customer);
		});
	}
}
