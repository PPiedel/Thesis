/*
  Copyright 2017 Google Inc. All Rights Reserved.
  <p>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p>
  http://www.apache.org/licenses/LICENSE-2.0
  <p>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

/*Permissions checking based on https://github.com/googlesamples/android-play-location*/

package com.example.pawel_piedel.thesis.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.example.pawel_piedel.thesis.R;
import com.example.pawel_piedel.thesis.adapters.ViewPagerAdapter;
import com.example.pawel_piedel.thesis.ui.base.BaseActivity;
import com.example.pawel_piedel.thesis.ui.tabs.cafes.CafesFragment;
import com.example.pawel_piedel.thesis.ui.tabs.cafes.CafesPresenter;
import com.example.pawel_piedel.thesis.ui.tabs.deliveries.DeliveriesFragment;
import com.example.pawel_piedel.thesis.ui.tabs.deliveries.DeliveriesPresenter;
import com.example.pawel_piedel.thesis.ui.tabs.restaurants.RestaurantsFragment;
import com.example.pawel_piedel.thesis.ui.tabs.restaurants.RestaurantsPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainContract.View {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Inject
    MainPresenter<MainContract.View> mPresenter;

    @Inject
    SharedPreferences sharedPreferences;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityComponent().inject(this);

        setContentView(R.layout.activity_main);

        setUnBinder(ButterKnife.bind(this));

        mPresenter.attachView(this);

        setUpLayout();

        //sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        setupViewPager(viewPager);

        setUpTabLayout();

        mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    private void setUpLayout() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        RestaurantsFragment restaurantsFragment = RestaurantsFragment.newInstance();
       // RestaurantsPresenter restaurantsPresenter = new RestaurantsPresenter(restaurantsFragment, sharedPreferences);
        adapter.addFragment(restaurantsFragment, getString(R.string.restaurants));

        CafesFragment cafesFragment = CafesFragment.newInstance();
        //CafesPresenter presenter = new CafesPresenter(cafesFragment, sharedPreferences);
        adapter.addFragment(cafesFragment, getString(R.string.cafes));

        DeliveriesFragment deliveriesFragment = DeliveriesFragment.newInstance();
        //DeliveriesPresenter presenter1 = new DeliveriesPresenter(deliveriesFragment, sharedPreferences);
        adapter.addFragment(deliveriesFragment, getString(R.string.delivery));

        viewPager.setAdapter(adapter);
    }

    private void setUpTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mPresenter.onPermissionResult(requestCode, permissions, grantResults);

    }

    @Override
    public Activity getViewActivity() {
        return this;
    }


}