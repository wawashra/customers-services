package io.wawashra.customers.repository;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.wawashra.customers.model.Customer;
import io.wawashra.customers.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class CustomerRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerRepository.class);

	private static final String COLLECTION_NAME = "customers";

	private final MongoClient client;

	public CustomerRepository(MongoClient client) {
		this.client = client;
	}

	public Single<List<Customer>> getAll() {
		final JsonObject query = new JsonObject();

		return client.rxFind(COLLECTION_NAME, query)
				.doOnError(throwable -> LOGGER.error(
						LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read all customers", throwable.getMessage())))
				.flatMap(result -> {
					final List<Customer> customers = new ArrayList<>();
					result.forEach(customer -> customers.add(new Customer(customer)));
					LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read %s customers", customers.size(), query));
					return Single.just(customers);
				});
	}

	public Maybe<Customer> getById(String id) {
		final JsonObject query = new JsonObject().put("_id", id);

		return client.rxFindOne(COLLECTION_NAME, query, null).flatMap(result -> {
			final Customer customer = new Customer(result);
			
			return Maybe.just(customer);
		});
	}

	public Maybe<Customer> insert(Customer customer) {
		LOGGER.info("Here ! ");
		return client.rxInsert(COLLECTION_NAME, JsonObject.mapFrom(customer)).doOnError(throwable -> {
			LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Insert customers", throwable.getMessage()));
		}).flatMap(result -> {
			customer.setId(result);
			LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Inserted customers", JsonObject.mapFrom(customer)));

			return Maybe.just(customer);
		});
	}

	public Maybe<Customer> update(String id, Customer customer) {
		
		final JsonObject query = new JsonObject().put("_id", id);

		return client.rxFindOne(COLLECTION_NAME, query, null).flatMap(result -> {
			Customer customerFromDB = new Customer(result);
			customerFromDB.setAge(customer.getAge());
			customerFromDB.setFirstName(customer.getFirstName());
			customerFromDB.setLastName(customer.getLastName());
			JsonObject customerToUpdate = JsonObject.mapFrom(customerFromDB);
			customerToUpdate.remove("id");
			
			return client.rxReplaceDocuments(COLLECTION_NAME, query, customerToUpdate)
					.flatMap(updateResult -> {
						if (updateResult.getDocModified() == 1) {
							LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Updated customers", JsonObject.mapFrom(customerFromDB)));

							return Maybe.just(customerFromDB);
						} else {
	                        return Maybe.error(new NoSuchElementException("No customer with id " + id));
						}
					});
		});
	}

	public Completable delete(String id) {
		final JsonObject query = new JsonObject().put("_id", id);

		return client.rxRemoveDocument(COLLECTION_NAME, query).flatMapCompletable(result -> {
			if (result.getRemovedCount() == 1) {
				return Completable.complete();
			} else {
				return Completable.error(new NoSuchElementException("No customer with id " + id));
			}
		});
	}

}
