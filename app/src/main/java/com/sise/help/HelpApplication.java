package com.sise.help;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

public class HelpApplication extends Application {

    private static final String APP_ID="11c4uqz91gvi02kyu6obu2ep6qpbfnwb62tkke0n27lfxx5e";
    private static final String APP_KEY="js6rik4w6wb3ae9nyik7ce87yzy8693lpkn7c0i3aga9s8sk";

    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, APP_ID, APP_KEY);
    }
}