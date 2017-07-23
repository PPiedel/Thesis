package com.example.pawel_piedel.thesis.ui.tabs.deliveries;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pawel_piedel.thesis.R;
import com.example.pawel_piedel.thesis.adapters.BusinessAdapter;
import com.example.pawel_piedel.thesis.data.model.Business;
import com.example.pawel_piedel.thesis.injection.components.ActivityComponent;
import com.example.pawel_piedel.thesis.ui.base.BaseFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

public class DeliveriesFragment extends BaseFragment implements DeliveriesContract.View {
    private final String LOG_TAG = DeliveriesFragment.class.getName();

    private BusinessAdapter businessAdapter = new BusinessAdapter();

    @Inject
    DeliveriesContract.Presenter<DeliveriesContract.View> deliveriesPresenter;

    @BindView(R.id.deliveries_recycler_view)
    RecyclerView mRecyclerView;

    public DeliveriesFragment() {
        // Required empty public constructor
    }

    public static DeliveriesFragment newInstance() {
        return new DeliveriesFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_deliveries, container, false);
        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            deliveriesPresenter.attachView(this);
            //mBlogAdapter.setCallback(this);
        }

        setUpRecyclerView();
        return view;
    }

    protected void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(businessAdapter);

        deliveriesPresenter.onViewPrepared();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        deliveriesPresenter.detachView();
        super.onDestroyView();
    }


    @Override
    public void requestPermissionsSafely(String[] permissions, int requestCode) {

    }



    @Override
    public boolean hasPermission(String permission) {
        return false;
    }


    @Override
    public void showDeliveries(List<Business> deliveries) {
        businessAdapter.addBusinessList(deliveries);
    }

    @Override
    public Context provideContext(){
        return getActivity();
    }

}