package com.sise.help.posts;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.sise.help.R;
import com.sise.help.app.BaseActionBarActivity;
import com.sise.help.user.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Chaos
 *         2015/02/25.
 */
public class PostActivity extends BaseActionBarActivity {

    private Button likeButton;
    private Button commentButton;

    private TextView nicknameText;
    private TextView updateTimeText;
    private TextView contentText;

    private ImageView stateImage;
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        likeButton = (Button) findViewById(R.id.like);
        commentButton = (Button) findViewById(R.id.comment);
        nicknameText = (TextView) findViewById(R.id.nickname);
        updateTimeText = (TextView) findViewById(R.id.updateTime);
        contentText = (TextView) findViewById(R.id.content);
        stateImage = (ImageView) findViewById(R.id.state);
        avatar = (ImageView) findViewById(R.id.avatar);

        String title = getIntent().getStringExtra("PostTitle");
        setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupControl();
    }

    private void setupControl() {
        String postObjId = getIntent().getStringExtra("ObjId");
        AVQuery<Post> query = new AVQuery<Post>("Post");
        query.getInBackground(postObjId, new GetCallback<Post>() {
            @Override
            public void done(Post post, AVException e) {
                if (post != null) {
                    setTitle(post.getTitle());
                    contentText.setText(post.getContent());
                    stateImage.setImageResource(post.getState() == Post.STATE_TODO ? R.drawable.bt_ic_snooze_amb_24dp : R.drawable.bt_ic_done_grn_24dp);
                    updateTimeText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(post.getUpdatedAt()));
                    post.getUser().fetchIfNeededInBackground(new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject avObject, AVException e) {
                            User trueUser = ((User) avObject);
                            if (!TextUtils.isEmpty(trueUser.getNickname())) {
                                nicknameText.setText(trueUser.getNickname());
                            } else {
                                nicknameText.setText(trueUser.getUsername());
                            }
                            Picasso.with(PostActivity.this)
                                    .load(trueUser.getAvatarUrl())
                                    .placeholder(R.drawable.person_image_empty)
                                    .error(R.drawable.person_image_empty)
                                    .resize(128, 128)
                                    .centerCrop()
                                    .into(avatar);
                        }
                    });
                }
            }
        });
    }
}
