package io.wawashra.customers.utils;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.mongo.MongoClient;

public class DbUtils {

	private static final String HOST_CONFIG = "datasource.host";
	private static final String PORT_CONFIG = "datasource.port";
	private static final String DATABASE_CONFIG = "datasource.database";
	private static final String USERNAME_CONFIG = "datasource.username";
	private static final String PASSWORD_CONFIG = "datasource.password";
	private static final String AUTH_SOURCE = "datasource.authsource";

	private DbUtils() {

	}

	/**
	 * Build DB MongoClient
	 * 
	 * @param JsonObject configurations
	 * @param Vertx      context
	 * @return MongoClient
	 */

	public static MongoClient createMongoClient(Vertx vertx, JsonObject configurations) {
		final JsonObject config = new JsonObject().put("host", configurations.getString(HOST_CONFIG))
				.put("port", Integer.parseInt(configurations.getString(PORT_CONFIG)))
				.put("username", configurations.getString(USERNAME_CONFIG))
				.put("password", configurations.getString(PASSWORD_CONFIG))
				.put("authSource", configurations.getString(AUTH_SOURCE))
				.put("db_name", configurations.getString(DATABASE_CONFIG)).put("useObjectId", true);
		return MongoClient.createShared(vertx, config);
	}
	
	
	

	public static JsonObject getDayQuery(String day) {
		final JsonObject query = new JsonObject();
		JsonObject sDay = new JsonObject();
		
		long sDayTime = DateUtils.getDayFromString(day);			
		long eDayTime = sDayTime + (24*60*60*1000);
		
		sDay.put("deleteAt", new JsonObject().put("$gte", sDayTime));

		JsonObject eDay = new JsonObject();

		eDay.put("deleteAt", new JsonObject().put("$lt", eDayTime));

		List<JsonObject> dates = new ArrayList<>();
		dates.add(sDay);
		dates.add(eDay);
		
		query.put("$and", dates);

		return query;

	}

	public static JsonObject getMonthQuery(String month) {
		final JsonObject query = new JsonObject();

		JsonObject sDay = new JsonObject();

		long sDayTime = DateUtils.getMonthFromString(month);
		long afterOneDay = 24 * 60 * 60 * 1000;
		long afterOneMonth = 30 * afterOneDay;
		long eDayTime = sDayTime + afterOneMonth;

		sDay.put("createAt", new JsonObject().put("$gte", sDayTime));

		JsonObject eDay = new JsonObject();

		eDay.put("createAt", new JsonObject().put("$lt", eDayTime));

		List<JsonObject> dates = new ArrayList<>();
		dates.add(sDay);
		dates.add(eDay);

		query.put("$and", dates);

		return query;

	}

}
