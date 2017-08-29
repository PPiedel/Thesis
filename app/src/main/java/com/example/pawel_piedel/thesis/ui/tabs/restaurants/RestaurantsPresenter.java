package com.example.pawel_piedel.thesis.ui.tabs.restaurants;

import android.location.Location;
import android.util.Pair;
import android.widget.Toast;

import com.example.pawel_piedel.thesis.data.DataManager;
import com.example.pawel_piedel.thesis.data.model.AccessToken;
import com.example.pawel_piedel.thesis.data.model.SearchResponse;
import com.example.pawel_piedel.thesis.injection.ConfigPersistent;
import com.example.pawel_piedel.thesis.ui.base.BasePresenter;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Pawel_Piedel on 20.07.2017.
 */
@ConfigPersistent
public class RestaurantsPresenter<V extends RestaurantsContract.View> extends BasePresenter<V> implements RestaurantsContract.Presenter<V> {
    private final static String LOG_TAG = RestaurantsPresenter.class.getSimpleName();
    public final static String RESTAURANTS = "restaurants";

    @Inject
    private RestaurantsPresenter(DataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void attachView(V view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }


    @Override
    public void onViewPrepared() {
        loadRestaurannts();
    }

    public void loadRestaurannts() {
        getView().showProgressDialog();
        Observable
                .zip(
                        getDataManager().getAccessToken(),
                        getDataManager().getLastKnownLocation(),
                        Pair::create)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Pair<AccessToken, Location>>() {
                    @Override
                    public void onCompleted() {
                        loadFromApi();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().hideProgressDialog();
                        Toast.makeText(getView().provideContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(Pair<AccessToken, Location> accessTokenLocationPair) {
                        getDataManager().saveAccessToken(accessTokenLocationPair.first);
                        getDataManager().saveLocation(accessTokenLocationPair.second);
                    }
                });

    }

    private void loadFromApi() {
        getDataManager().loadBusinesses("restaurant", RESTAURANTS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SearchResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().hideProgressDialog();
                        Toast.makeText(getView().provideContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(SearchResponse searchResponse) {
                        if (!searchResponse.getBusinesses().isEmpty()) {
                            getDataManager().saveBusinesses(searchResponse.getBusinesses(),RESTAURANTS);
                        }

                        getView().hideProgressDialog();
                        getView().showRestaurants(searchResponse.getBusinesses());

                    }
                });
    }
}
