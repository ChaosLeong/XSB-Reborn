package com.sise.help.user;

import com.avos.avoscloud.AVUser;

/**
 * @author Chaos
 *         2015/02/22.
 */
public class HelpUser extends AVUser {

    private static final String FIELD_NICKNAME = "nickname";
    private static final String FIELD_AREA = "area";
    private static final String FIELD_INTRODUCTION = "introduction";
    private static final String FIELD_GENDER = "gender";
    private static final String FIELD_AVATAR = "avatar";
    private static final String FIELD_SCORE = "score";

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

    public void setAvatar(String url) {
        put(FIELD_AVATAR,url);
    }

    public Integer getScore() {
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

    public String getAvatarUrl() {
        return getValue(FIELD_AVATAR);
    }

    private <T> T getValue(String key) {
        return (T) get(key);
    }

    public static HelpUser getCurrentUser2(){
        return AVUser.getCurrentUser(HelpUser.class);
    }
}
