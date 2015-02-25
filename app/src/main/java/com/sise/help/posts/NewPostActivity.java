package com.sise.help.posts;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.sise.help.R;
import com.sise.help.ui.widget.BezelImageView;
import com.sise.help.user.User;
import com.squareup.picasso.Picasso;

/**
 * @author Chaos
 *         2015/02/22.
 */
public class NewPostActivity extends Activity implements View.OnClickListener {

    private BezelImageView avatar;

    private EditText titleInput;
    private EditText contentInput;

    private TextView scoreText;
    private TextView nicknameText;

    private boolean sending = false;

    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        avatar = (BezelImageView) findViewById(R.id.avatar);
        titleInput = (EditText) findViewById(R.id.title);
        contentInput = (EditText) findViewById(R.id.content);
        scoreText = (TextView) findViewById(R.id.score);
        nicknameText = (TextView) findViewById(R.id.nickname);
        ImageButton sendButton = (ImageButton) findViewById(R.id.send);
        sendButton.setOnClickListener(this);

        setupControl();
    }


    private void setupControl() {
        User user = AVUser.getCurrentUser(User.class);
        if (user != null) {
            if (!TextUtils.isEmpty(user.getNickname())) {
                nicknameText.setText(user.getNickname());
            } else {
                nicknameText.setText(user.getUsername());
            }
            scoreText.setText(String.format("积分：%d", user.getScore()));
            if (!TextUtils.isEmpty(user.getAvatarUrl())) {
                Picasso.with(this)
                        .load(user.getAvatarUrl())
                        .placeholder(R.drawable.person_image_empty)
                        .error(R.drawable.person_image_empty)
                        .resize(128, 128)
                        .centerCrop()
                        .into(avatar);
            }
        }
    }

    @Override
    public void onClick(View v) {
        String title = titleInput.getText().toString().trim();
        String content = contentInput.getText().toString().trim();
        switch (v.getId()) {
            case R.id.send:
                if (!sending) {
                    sending = true;
                    if (post == null) {
                        post = new Post();
                    }
                    post.setTitle(title);
                    post.setContent(content);
                    post.setUserId(User.getCurrentUser().getObjectId());
                    post.setState(Post.STATE_TODO);
                    post.setStartTime(System.currentTimeMillis());
                    post.setScore(0);
                    post.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                            }
                            sending = false;
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (sending) {
            return;
        }
        super.onBackPressed();
    }
}
