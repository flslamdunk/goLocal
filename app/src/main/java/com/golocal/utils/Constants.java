package com.golocal.utils;

import java.text.SimpleDateFormat;

/**
 * Created by lfan on 8/28/14.
 */
public class Constants {

    public static final String BASE_DATE = "2014/09/07";
    public static final SimpleDateFormat yearMonthDayFormatter = new SimpleDateFormat("yyyy/MM/dd");
    public static final SimpleDateFormat monthDayFormatter = new SimpleDateFormat("MM/dd");

    public static final String LIKED_PREFIX = "golocal_liked";
    public static final String DISLIKED_PREFIX = "golocal_disliked";

    public static final String HOST = "http://go-local-server.herokuapp.com";
    public static final String EVENTS_URL = HOST + "/events?time=%d";
    public static final String ACTION_URL = HOST + "/action";
    public static final String CREATE_URL = HOST + "/create";
}
