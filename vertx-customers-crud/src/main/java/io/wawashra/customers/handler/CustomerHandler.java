package io.wawashra.customers.handler;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.wawashra.customers.model.Customer;
import io.wawashra.customers.service.CustomerService;

import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;

public class CustomerHandler {

	private CustomerService customerService;
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerHandler.class);

	public CustomerHandler(CustomerService customerService) {
		this.customerService = customerService;
	}

	public void getAll(RoutingContext rc) {
		customerService.getAll().subscribe(result -> onSuccessResponse(rc, 200, result),
				throwable -> onErrorResponse(rc, 400, throwable));
	}

	public void getOne(RoutingContext rc) {
		final String id = rc.pathParam("id");

		customerService.getById(id).subscribe(result -> onSuccessResponse(rc, 200, result),
				throwable -> onErrorResponse(rc, 400, throwable),
				() -> onErrorResponse(rc, 400, new NoSuchElementException("No customer with id " + id)));
	}

	public void insertOne(RoutingContext rc) {
		Customer customer = mapRequestBodyToCustomer(rc);
		customer.setCreateAt(Calendar.getInstance().getTime());
		customerService.insert(customer).subscribe(result -> onSuccessResponse(rc, 201, result),
				throwable -> onErrorResponse(rc, 400, throwable));
	}

	public void updateOne(RoutingContext rc) {
		final String id = rc.pathParam("id");
		final Customer customer = mapRequestBodyToCustomer(rc);

		customerService.update(id, customer).subscribe(result -> onSuccessResponse(rc, 201, result),
				throwable -> onErrorResponse(rc, 400, throwable),
				() -> onErrorResponse(rc, 400, new NoSuchElementException("No customer with id " + id)));
	}

	public void deleteOne(RoutingContext rc) {
		final String id = rc.pathParam("id");

		customerService.delete(id).subscribe(result -> onSuccessResponse(rc, 204, null),
				throwable -> onErrorResponse(rc, 400, throwable),
				() -> onErrorResponse(rc, 400, new NoSuchElementException("No customer with id " + id)));
	}

	// Mapping between customer class and request body JSON object
	private Customer mapRequestBodyToCustomer(RoutingContext rc) {
		Customer customer = new Customer();
		try {
			customer = rc.getBodyAsJson().mapTo(Customer.class);
		} catch (IllegalArgumentException ex) {
			LOGGER.error("error here ! ", ex);
			onErrorResponse(rc, 400, ex);
		}

		return customer;
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
