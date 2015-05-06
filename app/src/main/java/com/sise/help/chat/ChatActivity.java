package com.sise.help.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.sise.help.R;
import com.sise.help.app.BaseActionBarActivity;
import com.sise.help.database.ChatMessage;
import com.sise.help.database.ChatMessageDao;
import com.sise.help.database.DatabaseManager;
import com.sise.help.database.UserDao;
import com.sise.help.posts.ArrayRecyclerAdapter;
import com.sise.help.user.HelpUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author Chaos
 *         2015/02/26.
 */
public class ChatActivity extends BaseActionBarActivity implements ChatMessageReceiver.MessageListener {

    private String otherPeerId;
    private List<ChatMessage> chatMessages;

    private RecyclerView messagesView;

    private EditText inputText;

    private ImageButton sendButton;

    private SwipeRefreshLayout refreshLayout;

    private MessagesAdapter adapter;

    private com.sise.help.database.User currentUser;
    private com.sise.help.database.User otherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //为发送按钮设置点击事件监听器
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户输入的聊天信息
                String text = inputText.getText().toString().trim();
                //创建一个 ChatMessage 实例
                ChatMessage message = new ChatMessage();
                //设置 message 的聊天信息
                message.setMsg(text);
                //保存当前用户的 ID 属性
                message.setPeerId(AVUser.getCurrentUser().getObjectId());
                //保存当前聊天窗口所属的用户的 ID 属性
                message.setOtherPeerId(otherPeerId);
                //标记当前聊天信息的时间戳
                message.setTimestamp(System.currentTimeMillis());
                //标记当前用户是否为接收信息者
                message.setIsFrom(false);
                //将当前聊天信息持久化都本地数据库中
                DatabaseManager.getInstance().getChatMessageDao().insert(message);
                //将该聊天内容同步到用户界面的数据集中
                adapter.add(message);
                //将该聊天内容显示到用户界面中
                adapter.notifyItemInserted(adapter.getItemCount() - 1);
                //将当前聊天界面定位到新的聊天消息中
                messagesView.scrollToPosition(adapter.getItemCount() - 1);
                //将当前聊天信息发送到服务器中
                SessionService.getInstance().sendMessage(otherPeerId, text);
                //清空聊天信息输入框
                inputText.setText("");
            }
        });
        setContentView(R.layout.activity_chat);

        if (!getIntent().hasExtra("UserId")) {
            finish();
            return;
        }

        setupUserInfo();
        SessionService.getInstance().openSession();
        SessionService.getInstance().watchPeer(otherPeerId);
        setupActionBar();

        messagesView = (RecyclerView) findViewById(R.id.recyclerView);
        messagesView.setLayoutManager(new LinearLayoutManager(this));
        inputText = (EditText) findViewById(R.id.input);
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendButton.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendButton = (ImageButton) findViewById(R.id.send);
        sendButton.setEnabled(false);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputText.getText().toString().trim();
                ChatMessage message = new ChatMessage();
                message.setMsg(text);
                message.setPeerId(AVUser.getCurrentUser().getObjectId());
                message.setOtherPeerId(otherPeerId);
                message.setTimestamp(System.currentTimeMillis());
                message.setIsFrom(false);
                DatabaseManager.getInstance().getChatMessageDao().insert(message);
                adapter.add(message);
                adapter.notifyItemInserted(adapter.getItemCount() - 1);
                messagesView.scrollToPosition(adapter.getItemCount() - 1);
                SessionService.getInstance().sendMessage(otherPeerId, text);
                inputText.setText("");
            }
        });

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiping);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryChatMessage();
                refreshLayout.setRefreshing(false);
            }
        });

        chatMessages = new ArrayList<ChatMessage>();
        adapter = new MessagesAdapter(this);
        messagesView.setAdapter(adapter);

        queryChatMessage();
        messagesView.scrollToPosition(adapter.getItemCount() - 1);

        ChatMessageReceiver.addMessageListener(this);
    }

    @Override
    protected void onDestroy() {
        ChatMessageReceiver.removeMessageListener(this);
        super.onDestroy();
    }

    private void setupUserInfo() {
        otherPeerId = getIntent().getStringExtra("UserId");
        QueryBuilder<com.sise.help.database.User> qb = DatabaseManager.getInstance().getUserDao().queryBuilder();
        qb.where(UserDao.Properties.PeerId.eq(otherPeerId));
        otherUser = qb.unique();
        qb = DatabaseManager.getInstance().getUserDao().queryBuilder();
        qb.where(UserDao.Properties.PeerId.eq(AVUser.getCurrentUser().getObjectId()));
        currentUser = qb.unique();
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("Nickname"));
    }

    private void queryChatMessage() {
        QueryBuilder<ChatMessage> qb = DatabaseManager.getInstance().getChatMessageDao().queryBuilder();
        qb.where(ChatMessageDao.Properties.OtherPeerId.eq(otherPeerId), ChatMessageDao.Properties.PeerId.eq(AVUser.getCurrentUser().getObjectId()));
        qb.offset(chatMessages.size()).limit(50).orderAsc(ChatMessageDao.Properties.Id);
        adapter.addAll(0, qb.list());
        adapter.notifyDataSetChanged();
    }

    private void addMessageToBottom(ChatMessage message) {
        adapter.add(message);
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
        messagesView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onMessage(ChatMessage message) {
        if (otherPeerId.equals(message.getOtherPeerId())) {
            addMessageToBottom(message);
        }
    }

    @Override
    public void onMessageSent(ChatMessage message) {
        if (otherPeerId.equals(message.getOtherPeerId())) {
            addMessageToBottom(message);
        }
    }

    @Override
    public void onMessageDelivered(ChatMessage message) {

    }

    @Override
    public void onMessageFailure(ChatMessage message) {
        if (otherPeerId.equals(message.getOtherPeerId())) {
            Toast.makeText(this, "发送失败", Toast.LENGTH_SHORT).show();
        }
    }

    private class MessagesAdapter extends ArrayRecyclerAdapter<ChatMessage, MessagesAdapter.ViewHolder> {

        private static final int TYPE_LEFT = 1;
        private static final int TYPE_RIGHT = 2;

        private Context context;

        public MessagesAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemViewType(int position) {
            if (get(position).getIsFrom()) {
                return TYPE_LEFT;
            } else {
                return TYPE_RIGHT;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView;
            if (viewType == TYPE_LEFT) {
                rootView = getLayoutInflater().inflate(R.layout.item_chat_left, parent, false);
            } else {
                rootView = getLayoutInflater().inflate(R.layout.item_chat_right, parent, false);
            }
            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            ChatMessage message = get(position);
            holder.message.setText(message.getMsg());
//            loadAvatar(holder, (message.getIsFrom() ? otherUser : currentUser).getAvatarUrl());
            QueryBuilder<com.sise.help.database.User> qb = DatabaseManager.getInstance().getUserDao().queryBuilder();
            qb.where(UserDao.Properties.PeerId.eq(message.getIsFrom() ? message.getOtherPeerId() : message.getPeerId()));
            com.sise.help.database.User user = qb.unique();
            //todo 两个用户本地变量存着就好
            if (user != null) {
                loadAvatar(holder, user.getAvatarUrl());
            } else {
                AVQuery<HelpUser> query = AVUser.getUserQuery(HelpUser.class);
                query.whereEqualTo("objectId", message.getIsFrom() ? message.getOtherPeerId() : message.getPeerId());
                query.getFirstInBackground(new GetCallback<HelpUser>() {
                    @Override
                    public void done(HelpUser avUser, AVException e) {
                        if (avUser != null && e == null) {
                            com.sise.help.database.User user = new com.sise.help.database.User();
                            user.setPeerId(avUser.getObjectId());
                            user.setGender(avUser.getGender());
                            user.setArea(avUser.getArea());
                            user.setAvatarUrl(avUser.getAvatarUrl());
                            user.setIntroduction(avUser.getIntroduction());
                            user.setScore(avUser.getScore());
                            user.setNickname(avUser.getNickname());
                            DatabaseManager.getInstance().getUserDao().insert(user);
                            loadAvatar(holder, user.getAvatarUrl());
                        }
                    }
                });
            }
        }

        private void loadAvatar(ViewHolder viewHolder, String url) {
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.default_avatar_blue)
                    .error(R.drawable.default_avatar_blue)
                    .resize(128, 128)
                    .centerCrop()
                    .into(viewHolder.avatar);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView message;
            private ImageView avatar;

            public ViewHolder(View itemView) {
                super(itemView);
                message = (TextView) itemView.findViewById(R.id.message);
                avatar = (ImageView) itemView.findViewById(R.id.avatar);
            }
        }
    }
}
