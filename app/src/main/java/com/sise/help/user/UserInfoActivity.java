package com.sise.help.user;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.sise.help.R;
import com.sise.help.app.BaseActionBarActivity;
import com.squareup.picasso.Picasso;

/**
 * @author Chaos
 *         2015/02/24.
 */
public class UserInfoActivity extends BaseActionBarActivity implements View.OnClickListener {

    private ActionBar actionBar;

    private EditText nicknameText;
    private EditText introductionText;
    private EditText genderText;
    private EditText areaText;

    private TextView scoreText;

    private ImageView avatarImage;

    private Button chatButton;

    private boolean isEditing = false;
    private boolean isSaving = false;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        nicknameText = (EditText) findViewById(R.id.nickname);
        introductionText = (EditText) findViewById(R.id.introduction);
        areaText = (EditText) findViewById(R.id.area);
        genderText = (EditText) findViewById(R.id.gender);
        scoreText = (TextView) findViewById(R.id.score);
        avatarImage = (ImageView) findViewById(R.id.avatar);
        chatButton = (Button) findViewById(R.id.chat);

        setupActionBar();
        fetchUserData();
    }

    private void setupActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.user_info);
    }

    private void fetchUserData() {
        String userId = getIntent().getStringExtra("UserId");
        mUser = User.getCurrentUser2();
        if (mUser != null && userId.equals(mUser.getObjectId())) {
            setupUserInfo(User.getCurrentUser2());
        } else {
            AVQuery<User> query = AVUser.getUserQuery(User.class);
            query.whereEqualTo("objectId", userId);
            query.getFirstInBackground(new GetCallback<User>() {
                @Override
                public void done(User user, AVException e) {
                    mUser = user;
                    if (user != null && e == null) {
                        chatButton.setClickable(true);
                        chatButton.setText(R.string.chat);
                        setupUserInfo(user);
                    } else {
                        Toast.makeText(getApplicationContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setupUserInfo(User user) {
        nicknameText.setText(user.getNickname());
        introductionText.setText(user.getIntroduction());
        scoreText.setText("" + user.getScore());
        genderText.setText(user.getGender());
        areaText.setText(user.getArea());

        Picasso.with(this)
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.person_image_empty)
                .error(R.drawable.person_image_empty)
                .resize(128, 128)
                .centerCrop()
                .into(avatarImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String userId = getIntent().getStringExtra("UserId");
        if (User.getCurrentUser2() != null && userId.equals(User.getCurrentUser2().getObjectId())) {
            getMenuInflater().inflate(R.menu.menu_user_info, menu);
            menu.findItem(R.id.exit).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem edit = menu.findItem(R.id.edit);
        MenuItem save = menu.findItem(R.id.save);
        edit.setVisible(!isEditing);
        save.setVisible(isEditing);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                setEditing(true);
                break;
            case R.id.save:
                if (!isSaving) {
                    save();
                }
                break;
            case R.id.exit:
                AVUser.logOut();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat:
                break;
            case R.id.avatar:
                break;
        }
    }

    private void setEditing(boolean editing) {
        isEditing = editing;
        invalidateOptionsMenu();
        setEnable(editing);
    }

    private void setEnable(boolean enable) {
        nicknameText.setEnabled(enable);
        avatarImage.setClickable(enable);
        introductionText.setEnabled(enable);
        genderText.setEnabled(enable);
        areaText.setEnabled(enable);
    }

    private void save() {
        if (mUser != null) {
            isSaving = true;
            String nickname = nicknameText.getText().toString().trim();
            String introduction = introductionText.getText().toString().trim();
            String gender = genderText.getText().toString().trim();
            String area = areaText.getText().toString().trim();

            mUser.setNickname(nickname);
            mUser.setIntroduction(introduction);
            mUser.setGender(gender);
            mUser.setArea(area);

            mUser.setFetchWhenSave(true);
            mUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    isSaving = false;
                    if (e == null) {
                        setEditing(false);
                    }
                    Toast.makeText(getApplicationContext(), e == null ? "修改成功" : "修改失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
