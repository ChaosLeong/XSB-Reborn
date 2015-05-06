package com.sise.help;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SessionManager;
import com.sise.help.chat.SessionService;
import com.sise.help.database.DatabaseManager;
import com.sise.help.posts.Post;
import com.sise.help.posts.comment.Comment;

import java.util.LinkedList;

public class HelpApplication extends Application {

    private static final String APP_ID = "11c4uqz91gvi02kyu6obu2ep6qpbfnwb62tkke0n27lfxx5e";
    private static final String APP_KEY = "js6rik4w6wb3ae9nyik7ce87yzy8693lpkn7c0i3aga9s8sk";

    @Override
    public void onCreate() {
        super.onCreate();
        AVObject.registerSubclass(Post.class);
        AVObject.registerSubclass(Comment.class);
        AVOSCloud.initialize(this, APP_ID, APP_KEY);
        //todo 搞回去
        AVOSCloud.setDebugLogEnabled(true);

        DatabaseManager.getInstance().init(this);

        SessionService.getInstance().openSession();
    }
}