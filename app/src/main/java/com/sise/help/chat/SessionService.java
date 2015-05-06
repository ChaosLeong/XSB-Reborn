package com.sise.help.chat;

import com.avos.avoscloud.AVMessage;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.Session;
import com.avos.avoscloud.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Chaos
 *         2015/03/03.
 */
public class SessionService {
    private static SessionService instance;

    private List<String> peerIds;

    private SessionService() {
    }

    /**
     * 获取 SessionService 类唯一实例
     * @return SessionService 实例
     */
    public static SessionService getInstance() {
        //判断当前静态实例变量是否为空
        if (instance == null) {
            //实例化全局静态实例变量
            instance = new SessionService();
        }
        return instance;
    }

    private Session session;

    /**
     * 开启通信服务
     */
    public void openSession() {
        //判断当前用户是否为空，为空则不打开 LeanCloud 后端通信会话
        if (AVUser.getCurrentUser() != null && (session == null || !session.isOpen())) {
            //获取 LeanCloud SessionManager变量
            session = SessionManager.getInstance(AVUser.getCurrentUser().getObjectId());
            //打开通信会话，用于接受消息、获取消息发送状态
            session.open(new LinkedList<String>());
        }
    }

    /**
     * 关闭通信服务
     */
    public void closeSession() {
        if (session != null && session.isOpen()) {
            //关闭 LeanCloud 后端通信会话
            session.close();
        }
    }

    /**
     * 连接用户
     * @param peerId 需连接的用户的 ID
     */
    public void watchPeer(String peerId) {
        if (peerIds == null) {
            peerIds = new ArrayList<String>();
        }
        if (!session.isWatching(peerId)) {
            //将未连接过的用户添加到连接表中
            peerIds.add(peerId);
            //连接用户
            session.watchPeers(peerIds);
        }
    }

    /**
     * 发送消息
     * @param peerId 对方用户的 ID
     * @param msg 聊天信息
     */
    public void sendMessage(String peerId, String msg) {
        //连接用户
        watchPeer(peerId);
        //创建一个 LeanCloud AVMessage 对象
        AVMessage avMessage = new AVMessage(msg);
        //设置接收消息的用户的 ID
        avMessage.setToPeerIds(Arrays.asList(peerId));
        //发送消息到远程服务器，并由服务器推送到对应用户的设备上
        session.sendMessage(avMessage);
    }
}
