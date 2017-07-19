package com.example.pawel_piedel.thesis.main.tabs.cafes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pawel_piedel.thesis.R;
import com.example.pawel_piedel.thesis.model.Business;

import java.util.List;

import javax.inject.Inject;

import static dagger.internal.Preconditions.checkNotNull;

public class CafesFragment extends Fragment implements CafesContract.View {
    private static String LOG_TAG = CafesFragment.class.getName();

    private CafesContract.Presenter cafesPresenter;

    public CafesFragment() {
    }

    public static CafesFragment newInstance() {
        return new CafesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG,"onCreateView");
        return inflater.inflate(R.layout.fragment_coffies, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cafesPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull CafesContract.Presenter presenter) {
        cafesPresenter = checkNotNull(presenter);
    }


    @Override
    public void showCafes(List<Business> cafes) {
        for (Business b : cafes){
            Log.v(LOG_TAG,b.toString());
        }
    }



}