package com.example.pawel_piedel.thesis.main.tabs.deliveries;

import android.content.Context;

import com.example.pawel_piedel.thesis.BasePresenter;
import com.example.pawel_piedel.thesis.BaseView;
import com.example.pawel_piedel.thesis.main.tabs.cafes.CafesContract;
import com.example.pawel_piedel.thesis.model.Business;

import java.util.List;

/**
 * Created by Pawel_Piedel on 19.07.2017.
 */

public interface DeliveriesContract {

    interface View extends BaseView<DeliveriesContract.Presenter> {
        void showDeliveries(List<Business> deliveries);
        Context provideContext();
    }


    interface Presenter extends BasePresenter {
        void onViewPrepared();
        void load();
        void manageToLoadDeliveries();
    }
}
