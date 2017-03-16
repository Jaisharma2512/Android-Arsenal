/*
 *  Copyright (c) 2017.  Joe
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.lovejjfg.arsenal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lovejjfg.arsenal.R;
import com.lovejjfg.arsenal.api.mode.ArsenalListInfo;
import com.lovejjfg.arsenal.api.mode.ArsenalUserInfo;
import com.lovejjfg.arsenal.base.SupportActivity;
import com.lovejjfg.arsenal.base.SupportFragment;
import com.lovejjfg.arsenal.utils.JumpUtils;
import com.lovejjfg.arsenal.utils.glide.CircleTransform;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArsenalUserInfoActivity extends SupportActivity implements View.OnClickListener {
    private static final String TAG = ArsenalUserInfoActivity.class.getSimpleName();
    public static final String USER_INFO = "UserInfo";
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.iv_img)
    ImageView mIvPortrait;
    @Bind(R.id.tv_name)
    TextView mTvLocation;
    @Bind(R.id.tv_flowers)
    TextView mTvFlowers;
    @Bind(R.id.tv_flowing)
    TextView mTvFlowing;
    @Bind(R.id.tv_site)
    TextView mTvSite;
    @Bind(R.id.tv_repo)
    TextView mTvRepo;
    @Bind(R.id.tabs)
    TabLayout mTabs;
    @Bind(R.id.viewpager)
    ViewPager mViewpager;
    private CircleTransform circleTransform;
    private ArsenalUserInfo mInfo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mToolBar.setNavigationOnClickListener(this);
        circleTransform = new CircleTransform(this);
        mInfo = getIntent().getParcelableExtra(USER_INFO);
        if (savedInstanceState == null) {
            if (mInfo == null) {
                finish();
            } else {
                refreshUI(mInfo);
            }
        }

    }

    private void refreshUI(ArsenalUserInfo info) {
        Glide.with(ArsenalUserInfoActivity.this)
                .load(info.getPortraitUrl())
                .error(R.mipmap.ic_launcher)
                .transform(circleTransform)
                .into(mIvPortrait);
        mToolBar.setTitle(info.getUserName());
        mTvFlowers.setText(info.getFollowers());
        mTvFlowing.setText(info.getFollowing());
        mTvRepo.setText(info.getPublicRepo());
        mTvLocation.setText(info.getLocation());

        String text = info.getSite();
        mTvSite.setText(text);
        if (!TextUtils.equals("N/A", text)) {
            mTvSite.setOnClickListener(this);
        } else {
            mTvSite.setOnClickListener(null);
        }
        ArrayList<ArsenalListInfo.ListInfo> contributions = info.getContributions();
        ArrayList<ArsenalListInfo.ListInfo> ownProjects = info.getOwnProjects();
        Bundle contribution = new Bundle();
        contribution.putParcelableArrayList(ArsenalListInfoFragment.ARSENAL_LIST_INFO, contributions);
        contribution.putInt(ArsenalListInfoFragment.TYPE_NAME, ArsenalListInfoFragment.TYPE_USER_DETAIL);
        Bundle ownProject = new Bundle();
        ownProject.putParcelableArrayList(ArsenalListInfoFragment.ARSENAL_LIST_INFO, ownProjects);
        ownProject.putInt(ArsenalListInfoFragment.TYPE_NAME, ArsenalListInfoFragment.TYPE_USER_DETAIL);
        ArrayList<SupportFragment> fragments = new ArrayList<>();
        ArsenalListInfoFragment ownProFragment = new ArsenalListInfoFragment();
        ownProFragment.setArguments(ownProject);
        ArsenalListInfoFragment contributionFragment = new ArsenalListInfoFragment();
        contributionFragment.setArguments(contribution);

        fragments.add(ownProFragment);
        fragments.add(contributionFragment);

        ListInfoPagerAdapter adapter = new ListInfoPagerAdapter(getSupportFragmentManager(), fragments);

        mViewpager.setAdapter(adapter);

        mTabs.setupWithViewPager(mViewpager);

    }

    @Override
    public int initLayoutRes() {
        return R.layout.activity_user_info;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_site:
                JumpUtils.jumpToWeb(this, mTvSite.getText().toString());
                return;
        }
        onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        ArsenalUserInfo info = intent.getParcelableExtra(USER_INFO);
        if (mInfo.equals(info)) {
            return;
        }
        mInfo = info;
        refreshUI(mInfo);
        super.onNewIntent(intent);
    }
}
