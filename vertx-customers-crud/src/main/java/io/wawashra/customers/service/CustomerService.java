package io.wawashra.customers.service;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.wawashra.customers.model.Customer;
import io.wawashra.customers.repository.CustomerRepository;
import io.wawashra.customers.utils.LogUtils;

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
        return customerRepository.insert(customer).doOnSuccess(onSuccess->{
        	
        });
    }

    public Maybe<Customer> update(String id, Customer customer) {
        return customerRepository.update(id, customer);
    }

    public Completable delete(String id) {
        return customerRepository.delete(id);
    }

}
