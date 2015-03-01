package com.sise.help.chat;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.Session;
import com.avos.avoscloud.SessionManager;
import com.sise.help.R;
import com.sise.help.posts.ArrayRecyclerAdapter;
import com.sise.help.posts.Post;
import com.sise.help.user.User;
import com.sise.help.user.UserInfoActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chaos
 *         2015/02/22.
 */
public class SessionsFragment extends Fragment {

    private RecyclerView sessionsView;
    private TextView noneTipsText;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle("聊天信息");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sessions, container, false);
        sessionsView = (RecyclerView) rootView.findViewById(R.id.sessions);
        sessionsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sessionsView.setItemAnimator(new DefaultItemAnimator());

        noneTipsText = (TextView) rootView.findViewById(R.id.noneTips);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private static class SessionsAdapter extends ArrayRecyclerAdapter<Post, SessionsAdapter.ViewHolder> {

        private OnItemClickListener onItemClickListener;
        private Context context;

        public void setOnItemClickListener(Context context, OnItemClickListener onItemClickListener) {
            this.context = context;
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_session, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {
            final Post post = get(i);
            viewHolder.time.setText(post.getTitle());
            viewHolder.content.setText(post.getContent());
            viewHolder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra("UserId", post.getUser().getObjectId());
                    context.startActivity(intent);
                }
            });

            User user = post.getUser();
            if (user != null) {
                User currentUser = User.getCurrentUser2();
                if (currentUser != null && user.getObjectId().equals(currentUser.getObjectId())) {
                    setupUserInfo(viewHolder, currentUser);
                } else {
                    user.fetchIfNeededInBackground(new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject avObject, AVException e) {
                            if (avObject != null) {
                                User trueUser = ((User) avObject);
                                setupUserInfo(viewHolder, trueUser);
                            }
                        }
                    });
                }
            }

            if (onItemClickListener != null) {
                viewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick();
                    }
                });
            }
        }

        private void setupUserInfo(ViewHolder viewHolder, User user) {
            if (!TextUtils.isEmpty(user.getNickname())) {
                viewHolder.nickname.setText(user.getNickname());
            } else {
                viewHolder.nickname.setText(user.getUsername());
            }
            Picasso.with(context)
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.person_image_empty)
                    .error(R.drawable.person_image_empty)
                    .resize(128, 128)
                    .centerCrop()
                    .into(viewHolder.avatar);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            holder.avatar.setImageBitmap(null);
            super.onViewRecycled(holder);
        }

        public interface OnItemClickListener {
            void onItemClick();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView avatar;
            private TextView time;
            private TextView content;
            private TextView nickname;
            private View container;

            public ViewHolder(View itemView) {
                super(itemView);
                avatar = (ImageView) itemView.findViewById(R.id.avatar);
                time = (TextView) itemView.findViewById(R.id.time);
                content = (TextView) itemView.findViewById(R.id.content);
                nickname = (TextView) itemView.findViewById(R.id.nickname);
                container = itemView.findViewById(R.id.container);
            }
        }
    }
}
