package io.wawashra.customers.service;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.wawashra.customers.model.CreatedCustomer;
import io.wawashra.customers.model.CreatedCustomerDTO;
import io.wawashra.customers.repository.CustomerCreatedRepository;

public class CustomerCreatedService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerCreatedService.class);

	private final CustomerCreatedRepository customerRepository;

	public CustomerCreatedService(CustomerCreatedRepository bookRepository) {
		this.customerRepository = bookRepository;
	}

	public Single<List<CreatedCustomerDTO>> getAll() {
		return customerRepository.getAll();
	}

	public Maybe<CreatedCustomer> insert(CreatedCustomer customer) {
		return customerRepository.insert(customer);
	}

	public Maybe<CreatedCustomer> update(String id, CreatedCustomer customer) {
		return customerRepository.update(id, customer);
	}
}
