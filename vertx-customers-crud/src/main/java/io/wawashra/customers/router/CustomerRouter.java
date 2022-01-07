package io.wawashra.customers.router;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.wawashra.customers.handler.CustomerHandler;

public class CustomerRouter {

    private Vertx vertx;
    private CustomerHandler customerHandler;

    public CustomerRouter(Vertx vertx, CustomerHandler customerHandler) {
        this.vertx = vertx;
        this.customerHandler = customerHandler;
    }

    public Router getRouter() {
        final Router customerRouter = Router.router(vertx);

        customerRouter.route("/api/v1/customers*").handler(BodyHandler.create());
        customerRouter.get("/api/v1/customers").handler(customerHandler::getAll);
        customerRouter.get("/api/v1/customers/:id").handler(customerHandler::getOne);
        customerRouter.post("/api/v1/customers").handler(customerHandler::insertOne);
        customerRouter.put("/api/v1/customers/:id").handler(customerHandler::updateOne);
        customerRouter.delete("/api/v1/customers/:id").handler(customerHandler::deleteOne);

        return customerRouter;
    }

}
