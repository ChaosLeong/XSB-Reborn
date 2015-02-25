package com.sise.help.user;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.SaveCallback;

/**
 * @author Chaos
 *         2015/02/22.
 */
public class User extends AVUser {

    private static final String FIELD_NICKNAME = "nickname";
    private static final String FIELD_AREA = "area";
    private static final String FIELD_INTRODUCTION = "introduction";
    private static final String FIELD_GENDER = "gender";
    private static final String FIELD_AVATAR = "avatar";
    private static final String FIELD_SCORE = "score";

    private AVFile avatar;

    public void setScore(int score) {
        put(FIELD_SCORE, score);
    }

    public void setNickname(String nickname) {
        put(FIELD_NICKNAME, nickname);
    }

    public void setArea(String area) {
        put(FIELD_AREA, area);
    }

    public void setIntroduction(String introduction) {
        put(FIELD_INTRODUCTION, introduction);
    }

    public void setGender(String gender) {
        put(FIELD_GENDER, gender);
    }

    public void setAvatar(byte[] bytes) {
        avatar = new AVFile(FIELD_AVATAR, bytes);
    }

    public void saveAll(final SaveCallback callback) {
        if (avatar != null) {
            if (getAvatarAVFile() != null) {
                getAvatarAVFile().deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            avatar.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        put(FIELD_AVATAR, avatar);
                                    }
                                    saveInBackground(callback);
                                }
                            });
                        } else {
                            saveInBackground(callback);
                        }
                    }
                });
            }
        }
    }

    public int getScore() {
        return getValue(FIELD_SCORE);
    }

    public String getNickname() {
        return getValue(FIELD_NICKNAME);
    }

    public String getIntroduction() {
        return getValue(FIELD_INTRODUCTION);
    }

    public String getGender() {
        return getValue(FIELD_GENDER);
    }

    public String getArea() {
        return getValue(FIELD_AREA);
    }

    private AVFile getAvatarAVFile() {
        return getValue(FIELD_AVATAR);
    }

    public String getAvatarUrl() {
        if (getAvatarAVFile() != null) {
            return getAvatarAVFile().getUrl();
        }
        return null;
    }

    private <T> T getValue(String key) {
        return (T) get(key);
    }
}
