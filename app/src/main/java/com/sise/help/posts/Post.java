package com.sise.help.posts;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;

/**
 * @author Chaos
 *         2015/02/24.
 */
public class Post {

    private static final String OBJ_NAME = "Post";

    private static final String FIELD_TITLE = "title";
    private static final String FIELD_CONTENT = "content";
    private static final String FIELD_SCORE = "score";
    private static final String FIELD_START_TIME = "start_time";
    private static final String FIELD_END_TIME = "end_time";
    private static final String FIELD_STATE = "state";
    private static final String FIELD_USER_ID = "user_id";
    private static final String FILED_HELP_USER_ID = "help_user_id";

    public static final int STATE_TODO = 0;
    public static final int STATE_COMPLETE = 1;

    private AVObject post;

    public Post() {
        post = new AVObject(OBJ_NAME);
    }

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

    public void setUserId(String userId) {
        put(FIELD_USER_ID, userId);
    }

    public void setHelpUserId(String helpUserId) {
        put(FILED_HELP_USER_ID, helpUserId);
    }

    public String getTitle() {
        return getValue(FIELD_TITLE);
    }

    public String getContent() {
        return getValue(FIELD_CONTENT);
    }

    public String getUserId() {
        return getValue(FIELD_USER_ID);
    }

    public String getHelpUserId() {
        return getValue(FILED_HELP_USER_ID);
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
        return (T) post.get(key);
    }

    private void put(String key, Object value) {
        post.put(key, value);
    }

    public void saveInBackground(SaveCallback callback){
        post.saveInBackground(callback);
    }
}
