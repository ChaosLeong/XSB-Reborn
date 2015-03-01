package com.sise.help.chat;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVMessage;
import com.avos.avoscloud.AVMessageReceiver;
import com.avos.avoscloud.Session;

import java.util.Arrays;
import java.util.List;

/**
 * @author Chaos
 *         2015/02/26.
 */
public class ChatMessageReceiver extends AVMessageReceiver {
    @Override
    public void onSessionOpen(Context context, Session session) {
        logE("onSessionOpen");
    }

    @Override
    public void onSessionPaused(Context context, Session session) {
        logE("onSessionPaused");
    }

    @Override
    public void onSessionResumed(Context context, Session session) {
        logE("onSessionResumed");
    }

    @Override
    public void onPeersWatched(Context context, Session session, List<String> strings) {
        logE("onPeersWatched");
    }

    @Override
    public void onPeersUnwatched(Context context, Session session, List<String> strings) {
        logE("onPeersUnwatched");
    }

    @Override
    public void onMessage(Context context, Session session, AVMessage avMessage) {
        logE("onMessage");
    }

    @Override
    public void onMessageSent(Context context, Session session, AVMessage avMessage) {
        logE("onMessageSent");
    }

    @Override
    public void onMessageDelivered(Context context, Session session, AVMessage avMessage) {
        logE("onMessageDelivered");
    }

    @Override
    public void onMessageFailure(Context context, Session session, AVMessage avMessage) {
        logE("onMessageFailure");
    }

    @Override
    public void onStatusOnline(Context context, Session session, List<String> strings) {
        logE("onStatusOnline");
    }

    @Override
    public void onStatusOffline(Context context, Session session, List<String> strings) {
        logE("onStatusOffline");
    }

    @Override
    public void onError(Context context, Session session, Throwable throwable) {
        logE("onError");
    }

    private void logE(String msg){
        Log.e("MessageReceiver",msg);
    }
}
