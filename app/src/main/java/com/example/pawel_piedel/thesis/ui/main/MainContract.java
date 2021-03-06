package com.example.pawel_piedel.thesis.ui.main;

import android.app.Activity;

import com.example.pawel_piedel.thesis.ui.base.BaseView;

/**
 * Created by Pawel_Piedel on 18.07.2017.
 */

public interface MainContract {
    interface View extends BaseView {
        Activity getViewActivity();
        void startArActivity();
        void onFabButtonClicked();
        void init();
    }
    interface Presenter<V extends BaseView> extends com.example.pawel_piedel.thesis.ui.base.Presenter<V> {
        void onFabClick();
        void manageLocationSettings();
        void onLocationPermissionsGranted();
        boolean isPlaceSaved(String id);
    }
}
