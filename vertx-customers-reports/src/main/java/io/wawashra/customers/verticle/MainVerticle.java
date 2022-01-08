package io.wawashra.customers.verticle;

import io.reactivex.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer;
import io.wawashra.customers.handler.CustomerHandler;
import io.wawashra.customers.repository.CustomerCreatedRepository;
import io.wawashra.customers.repository.CustomerDeletedRepository;
import io.wawashra.customers.router.CustomerRouter;
import io.wawashra.customers.service.CustomerCreatedService;
import io.wawashra.customers.service.CustomerDeletedService;
import io.wawashra.customers.utils.DbUtils;
import io.wawashra.customers.utils.KafkaUtils;
import io.wawashra.customers.utils.LogUtils;

public class MainVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
	private static final String HTTP_PORT = "http.port";
	private MongoClient client;
	private KafkaConsumer<JsonObject, JsonObject> kafkaCreatedConsumer;
	private KafkaConsumer<JsonObject, JsonObject> kafkaUpdatedConsumer;
	private KafkaConsumer<JsonObject, JsonObject> kafkaDeletedConsumer;

	@Override
	public void start() {
		final long start = System.currentTimeMillis();
		
		final ConfigStoreOptions store = new ConfigStoreOptions().setType("file").setFormat("properties")
				.setConfig(new JsonObject().put("path", "application.properties"));

		final ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(store);

		final ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
		retriever.rxGetConfig().flatMap(configurations -> {

			client = DbUtils.createMongoClient(vertx, configurations);
			
			kafkaCreatedConsumer = KafkaUtils.createConsumer(vertx, configurations);
			kafkaUpdatedConsumer = KafkaUtils.createConsumer(vertx, configurations);
			kafkaDeletedConsumer = KafkaUtils.createConsumer(vertx, configurations);
			
			final CustomerCreatedRepository customerCreatedRepository = new CustomerCreatedRepository(client);
			final CustomerDeletedRepository customerDeletedRepository = new CustomerDeletedRepository(client);
			
			
			final CustomerCreatedService customerCreatedService = new CustomerCreatedService(customerCreatedRepository);
			final CustomerDeletedService customerDeletedService = new CustomerDeletedService(customerDeletedRepository);
			
			
			final CustomerHandler customerHandler = new CustomerHandler(customerCreatedService, customerDeletedService);
			
			customerHandler.consumeCustomerFromCreatedTopic(kafkaCreatedConsumer);
			customerHandler.consumeCustomerFromUpdatedTopic(kafkaUpdatedConsumer);
			customerHandler.consumeCustomerFromDeleteTopic(kafkaDeletedConsumer);

			final CustomerRouter customerRouter = new CustomerRouter(vertx, customerHandler);

			return createHttpServer(customerRouter.getRouter(), configurations);
		}).subscribe(
				server -> LOGGER
						.info(LogUtils.RUN_APP_SUCCESSFULLY_MESSAGE.buildMessage(System.currentTimeMillis() - start)),
				throwable -> {
					LOGGER.error("Error occurred before creating a new HTTP server: " + throwable.getMessage());
					System.exit(1);
				});
	}

	/**
	 * Run HTTP server on port 8888 with specified routes
	 *
	 * @param promise Callback
	 * @param router  Router
	 */

	private Single<HttpServer> createHttpServer(Router router, JsonObject configurations) {
		return vertx.createHttpServer().requestHandler(router).rxListen(configurations.getInteger(HTTP_PORT));
	}

}
