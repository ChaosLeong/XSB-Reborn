package com.sise.help.posts;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.sise.help.user.HelpUser;

/**
 * @author Chaos
 *         2015/02/24.
 */
@AVClassName("Post")
public class Post extends AVObject {

    private static final String OBJ_NAME = "Post";

    private static final String FIELD_TITLE = "title";
    private static final String FIELD_CONTENT = "content";
    private static final String FIELD_SCORE = "score";
    private static final String FIELD_START_TIME = "start_time";
    private static final String FIELD_END_TIME = "end_time";
    private static final String FIELD_STATE = "state";
    private static final String FIELD_USER = "user";
    private static final String FILED_HELP_USER = "help_user";

    public static final int STATE_TODO = 0;
    public static final int STATE_COMPLETE = 1;

    public void setTitle(String title) {
        put(FIELD_TITLE, title);
    }

    public void setContent(String content) {
        put(FIELD_CONTENT, content);
    }

    public void setScore(int score) {
        put(FIELD_SCORE, score);
    }

    public void setStartTime(long startTime) {
        put(FIELD_START_TIME, startTime);
    }

    public void setEndTime(long endTime) {
        put(FIELD_END_TIME, endTime);
    }

    public void setState(int state) {
        put(FIELD_STATE, state);
    }

    public void setUser(HelpUser user) {
        put(FIELD_USER, user);
    }

    public void setHelpUser(HelpUser helpUser) {
        put(FILED_HELP_USER, helpUser);
    }

    public String getTitle() {
        return getValue(FIELD_TITLE);
    }

    public String getContent() {
        return getValue(FIELD_CONTENT);
    }

    public HelpUser getUser() {
        return getAVUser(FIELD_USER, HelpUser.class);
    }

    public HelpUser getHelpUser() {
        return getAVUser(FILED_HELP_USER, HelpUser.class);
    }

    public long getStartTime() {
        return getValue(FIELD_START_TIME);
    }

    public long getEndTime() {
        return getValue(FIELD_END_TIME);
    }

    public int getState() {
        return getValue(FIELD_STATE);
    }

    public int getScore() {
        return getValue(FIELD_SCORE);
    }

    private <T> T getValue(String key) {
        return (T) get(key);
    }

    public static void queryAll(FindCallback<Post> callback){
        AVQuery<Post> query = new AVQuery<Post>(OBJ_NAME);
        query.orderByDescending(FIELD_START_TIME);
        query.findInBackground(callback);
    }
}
