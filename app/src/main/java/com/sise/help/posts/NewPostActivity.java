package com.sise.help.posts;

import android.app.Activity;
import android.content.Intent;
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
import com.sise.help.user.HelpUser;
import com.squareup.picasso.Picasso;

/**
 * @author Chaos
 *         2015/02/22.
 */
public class NewPostActivity extends Activity implements View.OnClickListener {

    private BezelImageView mAvatar;

    private EditText mTitleInput;
    private EditText mContentInput;

    private TextView mScoreText;
    private TextView mNicknameText;

    private boolean mSending = false;

    private Post mPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mAvatar = (BezelImageView) findViewById(R.id.avatar);
        mTitleInput = (EditText) findViewById(R.id.title);
        mContentInput = (EditText) findViewById(R.id.content);
        mScoreText = (TextView) findViewById(R.id.score);
        mNicknameText = (TextView) findViewById(R.id.nickname);
        ImageButton sendButton = (ImageButton) findViewById(R.id.send);
        sendButton.setOnClickListener(this);

        setupControl();
    }


    private void setupControl() {
        HelpUser user = AVUser.getCurrentUser(HelpUser.class);
        if (user != null) {
            if (!TextUtils.isEmpty(user.getNickname())) {
                mNicknameText.setText(user.getNickname());
            } else {
                mNicknameText.setText(user.getUsername());
            }
            mScoreText.setText(String.format("积分：%d", user.getScore()));
            if (!TextUtils.isEmpty(user.getAvatarUrl())) {
                Picasso.with(this)
                        .load(user.getAvatarUrl())
                        .placeholder(R.drawable.person_image_empty)
                        .error(R.drawable.person_image_empty)
                        .resize(128, 128)
                        .centerCrop()
                        .into(mAvatar);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                createNewPost();
                break;
        }
    }

    private void createNewPost() {
        String title = mTitleInput.getText().toString().trim();
        String content = mContentInput.getText().toString().trim();
        if (!mSending && title.length() > 0 && content.length() > 0) {
            mSending = true;
            if (mPost == null) {
                mPost = new Post();
            }
            mPost.setTitle(title);
            mPost.setContent(content);
            mPost.setUser(HelpUser.getCurrentUser(HelpUser.class));
            mPost.setState(Post.STATE_TODO);
            mPost.setStartTime(System.currentTimeMillis());
            mPost.setScore(0);
            mPost.setFetchWhenSave(true);
            mPost.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                        Intent data = new Intent();
                        data.putExtra("title", mPost.getTitle());
                        data.putExtra("content", mPost.getContent());
                        data.putExtra("objId", mPost.getObjectId());
                        setResult(RESULT_OK, data);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                    }
                    mSending = false;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mSending) {
            return;
        }
        super.onBackPressed();
    }
}
