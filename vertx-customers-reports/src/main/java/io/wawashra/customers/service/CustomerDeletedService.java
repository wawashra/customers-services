package io.wawashra.customers.service;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.wawashra.customers.model.DeletedCustomer;
import io.wawashra.customers.model.DeletedCustomerDTO;
import io.wawashra.customers.repository.CustomerDeletedRepository;

public class CustomerDeletedService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerDeletedService.class);

	private final CustomerDeletedRepository customerDeletedRepository;

	public CustomerDeletedService(CustomerDeletedRepository CustomerDeletedRepository) {
		this.customerDeletedRepository = CustomerDeletedRepository;
	}

	public Single<List<DeletedCustomerDTO>> getAll() {
		return customerDeletedRepository.getAll();
	}

	public Maybe<DeletedCustomer> insert(DeletedCustomer customer) {
		return customerDeletedRepository.insert(customer).doOnSuccess(insertedCustomer -> {
			LOGGER.info(String.format("Producer msg to kafka about insert new one %s", insertedCustomer.getId()));

		});
	}
}
