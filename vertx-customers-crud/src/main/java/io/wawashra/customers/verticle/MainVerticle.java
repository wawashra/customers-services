package io.wawashra.customers.verticle;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.reactivex.Single;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.kafka.client.producer.KafkaProducer;
import io.wawashra.customers.handler.CustomerHandler;
import io.wawashra.customers.repository.CustomerRepository;
import io.wawashra.customers.router.CustomerRouter;
import io.wawashra.customers.service.CustomerService;
import io.wawashra.customers.utils.DbUtils;
import io.wawashra.customers.utils.KafkaUtils;
import io.wawashra.customers.utils.LogUtils;

public class MainVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
	private static final String HTTP_PORT = "http.port";
	private MongoClient client;
	private KafkaProducer<JsonObject, JsonObject> kafkaProducer;

	@Override
	public void start() {
		final long start = System.currentTimeMillis();

		final ConfigStoreOptions store = new ConfigStoreOptions().setType("file").setFormat("properties")
				.setConfig(new JsonObject().put("path", "application.properties"));

		final ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(store);

		final ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
		retriever.rxGetConfig().flatMap(configurations -> {

			client = DbUtils.createMongoClient(vertx, configurations);
			kafkaProducer = KafkaUtils.createProducer(vertx, configurations);

			final CustomerRepository customerRepository = new CustomerRepository(client);
			final CustomerService customerService = new CustomerService(customerRepository, kafkaProducer);
			final CustomerHandler customerHandler = new CustomerHandler(customerService);
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

	@Override
	public void stop() throws Exception {
		client.close(result -> {
			if (result.succeeded())
				LOGGER.info("MongoClient is closed now");
		});

		kafkaProducer.close(result -> {
			if (result.succeeded())
				LOGGER.info("KafkaProducer is closed now");
		});
		super.stop();
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
