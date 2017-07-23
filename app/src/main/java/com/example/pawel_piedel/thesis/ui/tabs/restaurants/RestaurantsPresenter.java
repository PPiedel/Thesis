package com.example.pawel_piedel.thesis.ui.tabs.restaurants;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.pawel_piedel.thesis.ui.base.BaseView;
import com.example.pawel_piedel.thesis.data.ApiService;
import com.example.pawel_piedel.thesis.data.LocationService;
import com.example.pawel_piedel.thesis.data.ServiceFactory;
import com.example.pawel_piedel.thesis.data.model.AccessToken;
import com.example.pawel_piedel.thesis.data.model.SearchResponse;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.example.pawel_piedel.thesis.data.ServiceFactory.CLIENT_ID;
import static com.example.pawel_piedel.thesis.data.ServiceFactory.CLIENT_SECRET;
import static com.example.pawel_piedel.thesis.data.ServiceFactory.GRANT_TYPE;
import static com.example.pawel_piedel.thesis.data.ServiceFactory.accessToken;
import static com.example.pawel_piedel.thesis.util.Util.gson;
import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by Pawel_Piedel on 20.07.2017.
 */

public class RestaurantsPresenter implements RestaurantsContract.Presenter {
    private final String LOG_TAG = RestaurantsPresenter.class.getSimpleName();
    private RestaurantsContract.View restaurantsView;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;

    public RestaurantsPresenter(@NonNull RestaurantsContract.View restaurantsView, SharedPreferences sharedPreferences) {
        this.restaurantsView = checkNotNull(restaurantsView);
        this.sharedPreferences = sharedPreferences;
        this.restaurantsView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void attachView(BaseView view) {

    }

    @Override
    public void detachView() {

    }


    @Override
    public void onViewPrepared() {
        load();
    }

    @Override
    public void load() {
        ServiceFactory.accessToken = retrieveAccessTokenFromSharedPref();
        if (accessToken == null) {
            apiService = ServiceFactory.createService(ApiService.class);
            apiService.getAccessToken(CLIENT_ID, CLIENT_SECRET, GRANT_TYPE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<AccessToken>() {
                        @Override
                        public void onCompleted() {
                            saveAccessTokenInSharedPref();

                           manageToLoadRestaurants();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(AccessToken accessToken) {
                            ServiceFactory.accessToken = accessToken;
                            Log.v(LOG_TAG, "Cafes presenter" + accessToken.toString());
                        }
                    });
        } else {
            manageToLoadRestaurants();
        }
    }

    public void saveAccessTokenInSharedPref() {
        sharedPreferences
                .edit()
                .putString("access_token", gson.toJson(accessToken))
                .apply();
    }

    public AccessToken retrieveAccessTokenFromSharedPref() {
        String json = sharedPreferences
                .getString("access_token", "");
        Log.v(LOG_TAG, "Retrieved access token : " + json);
        return gson.fromJson(json, AccessToken.class);
    }

    @Override
    public void manageToLoadRestaurants() {
        if (LocationService.mLastLocation != null) {
            loadCafes();
        } else {
            ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(restaurantsView.provideContext());
            if (ActivityCompat.checkSelfPermission(restaurantsView.provideContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(restaurantsView.provideContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationProvider.getLastKnownLocation()
                    .subscribe(new Action1<Location>() {
                        @Override
                        public void call(Location location) {
                            LocationService.mLastLocation = location;
                            Log.i(LOG_TAG,"Location obtained : "+ location.toString());
                            loadCafes();
                        }
                    });
        }
    }

    private void loadCafes() {
        apiService = ServiceFactory.createService(ApiService.class);
        apiService.getBusinessesList(
                "restaurants",
                LocationService.mLastLocation.getLatitude(),
                LocationService.mLastLocation.getLongitude(),
                null,
                null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SearchResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v(LOG_TAG, e.toString());
                    }

                    @Override
                    public void onNext(SearchResponse searchResponse) {
                        restaurantsView.showRestaurants(searchResponse.getBusinesses());
                    }
                });
    }
}
