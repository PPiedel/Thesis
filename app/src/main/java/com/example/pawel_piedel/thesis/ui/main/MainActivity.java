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

package com.example.pawel_piedel.thesis.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.example.pawel_piedel.thesis.R;
import com.example.pawel_piedel.thesis.ui.augumented_reality.ARActivity;
import com.example.pawel_piedel.thesis.ui.base.BaseActivity;
import com.example.pawel_piedel.thesis.ui.network_connection.NetworkFragment;
import com.example.pawel_piedel.thesis.ui.tabs.cafes.CafesFragment;
import com.example.pawel_piedel.thesis.ui.tabs.deliveries.DeliveriesFragment;
import com.example.pawel_piedel.thesis.ui.tabs.restaurants.RestaurantsFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MainContract.View {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Inject
    MainPresenter<MainContract.View> mPresenter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);
        mPresenter.attachView(this);
        ButterKnife.setDebug(true);
        setUnBinder(ButterKnife.bind(this));
        setUpLayout();

        addNetworkConnectionFragment();

        mPresenter.manageLocationSettings();

    }

    public void init() {

        setupViewPager();

        setUpTabLayout();
    }


    private void addNetworkConnectionFragment() {
        NetworkFragment networkFragment = (NetworkFragment) getFragmentManager().findFragmentByTag(NetworkFragment.LOG_TAG);
        if (networkFragment == null) {
            networkFragment = NetworkFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .add(networkFragment, NetworkFragment.LOG_TAG)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    private void setUpLayout() {
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setupViewPager() {
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager());


        RestaurantsFragment restaurantsFragment = RestaurantsFragment.newInstance();
        adapter.addFragment(restaurantsFragment, getString(R.string.restaurants));

        CafesFragment cafesFragment = CafesFragment.newInstance();
        adapter.addFragment(cafesFragment, getString(R.string.cafes));

        DeliveriesFragment deliveriesFragment = DeliveriesFragment.newInstance();
        adapter.addFragment(deliveriesFragment, getString(R.string.delivery));


        viewPager.setAdapter(adapter);

    }

    private void setUpTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
    }



    @Override
    public Activity getViewActivity() {
        return this;
    }

    @Override
    public void startArActivity() {
        Intent arIntent = new Intent(getViewActivity().getApplication(), ARActivity.class);
        getViewActivity().startActivity(arIntent);
    }

    @OnClick(R.id.fab)
    public void onFabButtonClicked() {
        mPresenter.onFabClick();
    }

    public void setmPresenter(MainPresenter<MainContract.View> mPresenter) {
        this.mPresenter = mPresenter;
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MainPresenter.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        mPresenter.onLocationPermissionsGranted();
                        break;
                }
                break;
        }
    }
}
