package io.wawashra.customers.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class DateUtils {


    private static final String DELETED_DATE_FORMAT = "yyyy, MMM d";
    private static final String CREATED_DATE_FORMAT = "yyyy, MMM";
	private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);


    private DateUtils() {

    }
    
    
    
    /**
     * return Day in format (%YEAR%, %MONTH% %DAY-NO%) : ex. 2021, June 20 
     *
     * @param long time
     */

    public static String getDeletedDateFormated(long time) {
        DateFormat dateFormat = new SimpleDateFormat(DELETED_DATE_FORMAT);
        Date date = new Date(time);
        return dateFormat.format(date);
    }
    
    /**
     * return Day in format (%YEAR%, %MONTH%) : ex. 2021, June 
     *
     * @param long time
     */

    public static String getCreatedDateFormated(long time) {
        DateFormat dateFormat = new SimpleDateFormat(CREATED_DATE_FORMAT);
        Date date = new Date(time);
        return dateFormat.format(date);
    }

    /**
     * return return time in ms 
     *
     * @param String day
     */

    public static long getDayFromString(String day) {
    	
        SimpleDateFormat formatter = new SimpleDateFormat(DELETED_DATE_FORMAT);
        try {
			return formatter.parse(day).getTime();
		} catch (ParseException e) {
			LOGGER.error("Error when convert day" + e.getMessage());
			return new Date().getTime();
		}
    }
    
    /**
     * return return time in ms 
     *
     * @param String day
     */

    public static long getMonthFromString(String day) {
    	
        SimpleDateFormat formatter = new SimpleDateFormat(CREATED_DATE_FORMAT);
        try {
			return formatter.parse(day).getTime();
		} catch (ParseException e) {
			LOGGER.error("Error when convert day" + e.getMessage());
			return new Date().getTime();
		}
    }
}
