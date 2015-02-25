package com.sise.help.user;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;

import com.sise.help.R;
import com.sise.help.app.BaseActionBarActivity;

/**
 * @author Chaos
 *         2015/02/24.
 */
public class UserInfoActivity extends BaseActionBarActivity {

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        setupActionBar();
    }

    private void setupActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.user_info);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
