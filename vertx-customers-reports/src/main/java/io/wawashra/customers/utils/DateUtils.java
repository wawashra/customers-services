package io.wawashra.customers.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {


    private static final String DELETED_DATE_FORMAT = "yyyy, MMM d";
    private static final String CREATED_DATE_FORMAT = "yyyy, MMM";
    

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

}
