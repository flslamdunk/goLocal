package com.golocal.activity;

import com.golocal.activity.FeedActivity;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;
/**
 * Created by lfan on 9/19/14.
 */
public class Application extends android.app.Application {
    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "mdfnlTjH6l15LH3hzHB5CUlNZQoFd7ZhTHEaEAdy", "VMqzIetogOdeGO6oMHSfK29Z3mO0FGbr4tq5KeYY");

        // Specify an Activity to handle all pushes by default.
        PushService.setDefaultPushCallback(this, FeedActivity.class);
    }
}
