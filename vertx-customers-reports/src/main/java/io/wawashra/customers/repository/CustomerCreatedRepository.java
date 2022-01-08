package io.wawashra.customers.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.wawashra.customers.model.CreatedCustomer;
import io.wawashra.customers.model.CreatedCustomerDTO;

public class CustomerCreatedRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerCreatedRepository.class);

	private static final String COLLECTION_NAME = "created-customers";

	private final MongoClient client;

	public CustomerCreatedRepository(MongoClient client) {
		this.client = client;
	}

	public Single<List<CreatedCustomerDTO>> getAll() {
		final JsonObject query = new JsonObject();

		return client.rxFind(COLLECTION_NAME, query).flatMap(result -> {
			final List<CreatedCustomerDTO> customers = new ArrayList<>();
			result.forEach(customer -> customers.add(new CreatedCustomerDTO(customer)));
			LOGGER.info(String.format("Read %s customers", customers.size()));
			return Single.just(customers);
		});
	}
	

	public Maybe<CreatedCustomer> getById(String id) {
		final JsonObject query = new JsonObject().put("_id", id);

		return client.rxFindOne(COLLECTION_NAME, query, null).flatMap(result -> {
			final CreatedCustomer customer = new CreatedCustomer(result);
			LOGGER.info(String.format("Read one customer %s - query was %s", JsonObject.mapFrom(customer), query));
			return Maybe.just(customer);
		});
	}

	public Maybe<CreatedCustomer> insert(CreatedCustomer customer) {
		return client.rxInsert(COLLECTION_NAME, JsonObject.mapFrom(customer)).flatMap(result -> {
			customer.setId(result);
			LOGGER.info(String.format("Inserted one customer %s", JsonObject.mapFrom(customer)));

			return Maybe.just(customer);
		});
	}

	public Maybe<CreatedCustomer> update(String id, CreatedCustomer customer) {

		final JsonObject query = new JsonObject().put("id", id);

		return client.rxFindOne(COLLECTION_NAME, query, null).flatMap(result -> {
			CreatedCustomer customerFromDB = new CreatedCustomer(result);
			customerFromDB.setAge(customer.getAge());
			customerFromDB.setFirstName(customer.getFirstName());
			customerFromDB.setLastName(customer.getLastName());
			JsonObject customerToUpdate = JsonObject.mapFrom(customerFromDB);
			customerToUpdate.remove("id");

			return client.rxReplaceDocuments(COLLECTION_NAME, query, customerToUpdate).flatMap(updateResult -> {
				if (updateResult.getDocModified() == 1) {

					LOGGER.info(String.format("Updated one customer %s", JsonObject.mapFrom(customerToUpdate)));

					return Maybe.just(customerFromDB);
				} else {
					return Maybe.error(new NoSuchElementException("No customer with id " + id));
				}
			});
		});
	}

	public Maybe<CreatedCustomer> delete(String id) {
		final JsonObject query = new JsonObject().put("_id", id);
		return client.rxFindOne(COLLECTION_NAME, query, null).flatMap(result -> {
			CreatedCustomer customerFromDB = new CreatedCustomer(result);
			LOGGER.info(String.format("Deleted one customer %s", JsonObject.mapFrom(customerFromDB)));
			return client.rxRemoveDocument(COLLECTION_NAME, query).flatMap(deleteResult -> {
				if (deleteResult.getRemovedCount() == 1) {
					return Maybe.just(customerFromDB);
				} else {
					return Maybe.error(new NoSuchElementException("No customer with id " + id));
				}
			});
		});

	}

}
