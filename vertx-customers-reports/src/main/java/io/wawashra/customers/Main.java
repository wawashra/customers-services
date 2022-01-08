package io.wawashra.customers;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.wawashra.customers.verticle.MainVerticle;
/**
 * 
 * @author Awashra, Waseem
 * 
 * Main class to setup logger and deploy MainVerticle by use RxJava 2
 */
public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");


        final Vertx vertx = Vertx.vertx();

        vertx.rxDeployVerticle(MainVerticle.class.getName())
                .subscribe(
                        verticle -> LOGGER.info("New verticle started!"),
                        throwable -> {
                        	LOGGER.error("Error occurred before deploying a new verticle: " + throwable.getMessage());
                            System.exit(1);
                        });
    }

}
