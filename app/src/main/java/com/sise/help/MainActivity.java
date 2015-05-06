package com.sise.help;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.sise.help.app.BaseActionBarActivity;
import com.sise.help.chat.SessionService;
import com.sise.help.chat.SessionsFragment;
import com.sise.help.feedback.FeedbackFragment;
import com.sise.help.posts.PostsFragment;
import com.sise.help.rank.RanksFragment;
import com.sise.help.settings.SettingsFragment;
import com.sise.help.startup.StartupActivity;
import com.sise.help.ui.widget.BezelImageView;
import com.sise.help.ui.widget.ScrimInsetsFrameLayout;
import com.sise.help.user.HelpUser;
import com.sise.help.user.UserInfoActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends BaseActionBarActivity implements View.OnClickListener {

    /**
     * 双击 Back 键退出计时器
     */
    private long exitTime = 0;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private Spinner mFilter;

    private TextView mIntroduction;
    private TextView mName;
    private BezelImageView mAvatar;

    private ListView mDrawerListView;

    private ExploreSpinnerAdapter mTopLevelSpinnerAdapter = new ExploreSpinnerAdapter(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserInfo();
    }

    @Override
    protected void onDestroy() {
        SessionService.getInstance().closeSession();
        super.onDestroy();
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("学生帮");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_drawer);
        setupNavDrawer();
        setupActionBarSpinner();
    }

    private void setupActionBarSpinner() {
        mFilter = (Spinner) findViewById(R.id.filter);

        mTopLevelSpinnerAdapter.clear();
        mTopLevelSpinnerAdapter.addItem("", getString(R.string.all_type), false, 0);

        String[] titles = getResources().getStringArray(R.array.type_titles);
        String[] tags = getResources().getStringArray(R.array.type_tags);
        mTopLevelSpinnerAdapter.addHeader(titles[0]);
        for (int i = 0; i < tags.length; i++) {
            mTopLevelSpinnerAdapter.addItem(tags[i], tags[i], true, 65 * (i + 1));
        }
        mFilter.setAdapter(mTopLevelSpinnerAdapter);
        mFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setupNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));

        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            });
        }
        mDrawerLayout.setDrawerShadow(R.drawable.bt_drawer_shadow, Gravity.START);

        /*Drawer Footer*/
        ViewStub viewStub = (ViewStub) findViewById(R.id.drawer_list_view_stub);
        viewStub.inflate();

        ScrimInsetsFrameLayout navDrawer = (ScrimInsetsFrameLayout) mDrawerLayout.findViewById(R.id.nav_drawer);
        if (navDrawer != null) {
            final View chosenAccountView = getLayoutInflater().inflate(R.layout.nav_drawer_header, null);
            final View chosenAccountContentView = chosenAccountView.findViewById(R.id.chosen_account_content_view);
            chosenAccountContentView.setOnClickListener(this);

            mIntroduction = (TextView) chosenAccountView.findViewById(R.id.profile_introduction_text);
            mName = (TextView) chosenAccountView.findViewById(R.id.profile_name_text);
            mAvatar = (BezelImageView) chosenAccountView.findViewById(R.id.profile_image);

//            final int navDrawerChosenAccountHeight = getResources().getDimensionPixelSize(R.dimen.navdrawer_chosen_account_height);
            navDrawer.setOnInsetsCallback(new ScrimInsetsFrameLayout.OnInsetsCallback() {
                @Override
                public void onInsetsChanged(Rect insets) {
                    ViewGroup.MarginLayoutParams lp = (AbsListView.MarginLayoutParams) chosenAccountContentView.getLayoutParams();
                    lp.topMargin = insets.top;
                    chosenAccountContentView.setLayoutParams(lp);

//                    ViewGroup.LayoutParams lp2 = chosenAccountView.getLayoutParams();
//                    lp2.height = navDrawerChosenAccountHeight + insets.top;
//                    chosenAccountView.setLayoutParams(lp2);
                }
            });

            String[] drawerTags = getResources().getStringArray(R.array.drawer_tags);
            int[] drawerImgs = new int[]{
                    R.drawable.bt_ic_updates_clu_24dp,
                    R.drawable.bt_ic_forums_clu_24dp,
                    R.drawable.bt_ic_finance_clu_24dp,
            };
            mDrawerListView = (ListView) findViewById(R.id.drawer_list);
            mDrawerListView.addHeaderView(chosenAccountView);
            mDrawerListView.setAdapter(new DrawerAdapter(this, drawerImgs, drawerTags));
            mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position < mDrawerListView.getHeaderViewsCount()) {
                        return;
                    }
                    switch (position) {
                        case 1:
                            switchFragment(new PostsFragment());
                            break;
                        case 2:
                            switchFragment(new SessionsFragment());
                            break;
                        case 3:
                            switchFragment(new RanksFragment());
                            break;
                    }
                }
            });
            switchFragment(new PostsFragment());
        }

        setDrawerNavFooterEntry(R.id.drawer_list_settings, R.drawable.bt_ic_settings_g50_24dp, R.string.drawer_settings);
        setDrawerNavFooterEntry(R.id.drawer_list_feedback, R.drawable.bt_ic_help_g50_24dp, R.string.drawer_help_feedback);
    }

    private void updateUserInfo() {
        if (mIntroduction != null && mName != null && mAvatar != null) {
            mIntroduction.setText(R.string.thanks);
            mName.setText(getString(R.string.app_name));
            mAvatar.setImageResource(R.drawable.person_image_empty);
            HelpUser user = AVUser.getCurrentUser(HelpUser.class);
            if (user != null) {
                if (!TextUtils.isEmpty(user.getNickname())) {
                    mName.setText(user.getNickname());
                } else {
                    mName.setText(user.getUsername());
                }
                if (!TextUtils.isEmpty(user.getIntroduction())) {
                    mIntroduction.setText(user.getIntroduction());
                }
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
    }

    private void switchFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.content_layout, fragment).commit();
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    private void setDrawerNavFooterEntry(int viewId, int drawableRes, int strRes) {
        TextView entry = (TextView) findViewById(viewId);
        Drawable drawable = getResources().getDrawable(drawableRes);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        entry.setCompoundDrawables(drawable, null, null, null);
        entry.setText(strRes);
        entry.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        } else if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再次按返回键退出", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawer_list_feedback:
                switchFragment(new FeedbackFragment());
                Toast.makeText(this, "FeedbackFragment", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_list_settings:
                switchFragment(new SettingsFragment());
                Toast.makeText(this, "SettingsFragment", Toast.LENGTH_SHORT).show();
                break;
            case R.id.chosen_account_content_view:
                Intent intent = new Intent();
                if (AVUser.getCurrentUser() == null) {
                    intent.setClass(this, StartupActivity.class);
                } else {
                    intent.setClass(this, UserInfoActivity.class);
                    intent.putExtra("UserId", AVUser.getCurrentUser().getObjectId());
                }
                startActivity(intent);
                break;
        }
    }

    private class DrawerAdapter extends BaseAdapter {

        private int[] imgs;
        private String[] tags;
        private Context context;

        public DrawerAdapter(Context context, int[] imgs, String[] tags) {
            this.context = context;
            this.tags = tags;
            this.imgs = imgs;
        }

        @Override
        public int getCount() {
            return tags.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.nav_entry, parent, false);
                textView = (TextView) convertView.findViewById(R.id.nav_entry_text);
                convertView.setTag(textView);
            } else {
                textView = (TextView) convertView.getTag();
            }
            Drawable drawable = context.getResources().getDrawable(imgs[position]);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            textView.setCompoundDrawables(drawable, null, null, null);
            textView.setText(tags[position]);
            return convertView;
        }
    }

    private class ExploreSpinnerItem {
        boolean isHeader;
        String tag, title;
        int color;
        boolean indented;

        ExploreSpinnerItem(boolean isHeader, String tag, String title, boolean indented, int color) {
            this.isHeader = isHeader;
            this.tag = tag;
            this.title = title;
            this.indented = indented;
            this.color = color;
        }
    }

    /**
     * Adapter that provides views for our top-level Action Bar spinner.
     */
    private class ExploreSpinnerAdapter extends BaseAdapter {
        private int mDotSize;
        private boolean mTopLevel;

        private ExploreSpinnerAdapter(boolean topLevel) {
            this.mTopLevel = topLevel;
        }

        // pairs of (tag, title)
        private ArrayList<ExploreSpinnerItem> mItems = new ArrayList<ExploreSpinnerItem>();

        public void clear() {
            mItems.clear();
        }

        public void addItem(String tag, String title, boolean indented, int color) {
            mItems.add(new ExploreSpinnerItem(false, tag, title, indented, color));
        }

        public void addHeader(String title) {
            mItems.add(new ExploreSpinnerItem(true, "", title, false, 0));
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private boolean isHeader(int position) {
            return position >= 0 && position < mItems.size()
                    && mItems.get(position).isHeader;
        }

        @Override
        public View getDropDownView(int position, View view, ViewGroup parent) {
            if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
                view = getLayoutInflater().inflate(R.layout.explore_spinner_item_dropdown,
                        parent, false);
                view.setTag("DROPDOWN");
            }

            TextView headerTextView = (TextView) view.findViewById(R.id.header_text);
            View dividerView = view.findViewById(R.id.divider_view);
            TextView normalTextView = (TextView) view.findViewById(android.R.id.text1);

            if (isHeader(position)) {
                headerTextView.setText(getTitle(position));
                headerTextView.setVisibility(View.VISIBLE);
                normalTextView.setVisibility(View.GONE);
                dividerView.setVisibility(View.VISIBLE);
            } else {
                headerTextView.setVisibility(View.GONE);
                normalTextView.setVisibility(View.VISIBLE);
                dividerView.setVisibility(View.GONE);

                setUpNormalDropdownView(position, normalTextView);
            }

            return view;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
                view = getLayoutInflater().inflate(mTopLevel
                                ? R.layout.explore_spinner_item_actionbar
                                : R.layout.explore_spinner_item,
                        parent, false);
                view.setTag("NON_DROPDOWN");
            }
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getTitle(position));
            return view;
        }

        private String getTitle(int position) {
            return position >= 0 && position < mItems.size() ? mItems.get(position).title : "";
        }

        private int getColor(int position) {
            return position >= 0 && position < mItems.size() ? mItems.get(position).color : 0;
        }

        private String getTag(int position) {
            return position >= 0 && position < mItems.size() ? mItems.get(position).tag : "";
        }

        private void setUpNormalDropdownView(int position, TextView textView) {
            textView.setText(getTitle(position));
            ShapeDrawable colorDrawable = (ShapeDrawable) textView.getCompoundDrawables()[2];
            int color = getColor(position);
            if (color == 0) {
                if (colorDrawable != null) {
                    textView.setCompoundDrawables(null, null, null, null);
                }
            } else {
                if (mDotSize == 0) {
                    mDotSize = getResources().getDimensionPixelSize(
                            R.dimen.tag_color_dot_size);
                }
                if (colorDrawable == null) {
                    colorDrawable = new ShapeDrawable(new OvalShape());
                    colorDrawable.setIntrinsicWidth(mDotSize);
                    colorDrawable.setIntrinsicHeight(mDotSize);
                    colorDrawable.getPaint().setStyle(Paint.Style.FILL);
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, null, colorDrawable, null);
                }
                colorDrawable.getPaint().setColor(color);
            }

        }

        @Override
        public boolean isEnabled(int position) {
            return !isHeader(position);
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }
    }
}
