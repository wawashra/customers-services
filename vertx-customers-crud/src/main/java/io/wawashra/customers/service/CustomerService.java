package io.wawashra.customers.service;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.wawashra.customers.model.Customer;
import io.wawashra.customers.repository.CustomerRepository;

import java.util.List;

public class CustomerService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

	private final CustomerRepository customerRepository;

	public CustomerService(CustomerRepository bookRepository) {
		this.customerRepository = bookRepository;
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
		});
	}

	public Maybe<Customer> update(String id, Customer customer) {
		return customerRepository.update(id, customer).doOnSuccess(updatedCustomer -> {
			LOGGER.info(String.format("Producer msg to kafka about update new one %s", updatedCustomer.getId()));

		});
	}

	public Maybe<Customer> delete(String id) {
		return customerRepository.delete(id).doOnSuccess(deletedCustomer -> {
			LOGGER.info(String.format("Producer msg to kafka about delete new one %s", deletedCustomer.getId()));

		});
	}

}
