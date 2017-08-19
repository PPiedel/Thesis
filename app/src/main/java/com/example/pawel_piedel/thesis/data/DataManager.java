package com.example.pawel_piedel.thesis.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.pawel_piedel.thesis.data.local.SharedPreferencesManager;
import com.example.pawel_piedel.thesis.data.model.AccessToken;
import com.example.pawel_piedel.thesis.data.model.Business;
import com.example.pawel_piedel.thesis.data.model.ReviewsResponse;
import com.example.pawel_piedel.thesis.data.model.SearchResponse;
import com.example.pawel_piedel.thesis.data.remote.ApiService;
import com.example.pawel_piedel.thesis.data.remote.ServiceFactory;
import com.example.pawel_piedel.thesis.injection.ApplicationContext;
import com.example.pawel_piedel.thesis.ui.main.tabs.cafes.CafesPresenter;
import com.example.pawel_piedel.thesis.ui.main.tabs.deliveries.DeliveriesPresenter;
import com.example.pawel_piedel.thesis.ui.main.tabs.restaurants.RestaurantsPresenter;
import com.example.pawel_piedel.thesis.util.Util;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;


import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscription;

import static com.example.pawel_piedel.thesis.data.remote.ServiceFactory.CLIENT_ID;
import static com.example.pawel_piedel.thesis.data.remote.ServiceFactory.CLIENT_SECRET;
import static com.example.pawel_piedel.thesis.data.remote.ServiceFactory.GRANT_TYPE;
import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by Pawel_Piedel on 21.07.2017.
 */
@Singleton
public class DataManager {
    private final String LOG_TAG = DataManager.class.getSimpleName();
    private ReactiveLocationProvider locationProvider;
    private final SharedPreferencesManager preferencesHelper;
    private ApiService apiService;
    private Context context;
    private List<Business> restaurants;
    private List<Business> cafes;
    private List<Business> deliveries;

    @Inject
    public DataManager(@ApplicationContext Context context,
                       ApiService apiService,
                       SharedPreferencesManager preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
        this.apiService = apiService;
        this.context = context;
        locationProvider = new ReactiveLocationProvider(context);
    }

    public SharedPreferencesManager getPreferencesHelper() {
        return preferencesHelper;
    }

    public Observable<AccessToken> getAccessToken() {
        AccessToken accessToken = preferencesHelper.getAccessToken();
        //just return value without call if cached
        if (accessToken != null) {
            return Observable.just(accessToken);
        } else {
            return apiService.getAccessToken(CLIENT_ID, CLIENT_SECRET, GRANT_TYPE);
        }
    }

    @SuppressLint("MissingPermission")
    public Observable<Location> getLastKnownLocation() {
        if (Util.mLastLocation != null) {
            return Observable.just(Util.mLastLocation);
        } else {
            return locationProvider.getLastKnownLocation();
        }
    }

    @SuppressLint("MissingPermission")
    public Observable<Location> getLocationUpdates() {
        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(5 * 1000)
                .setFastestInterval(1000);

        return locationProvider.getUpdatedLocation(request);
    }

    public void safelyUnsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public Observable<SearchResponse> loadBusinesses(String term, String category) {
        Observable<SearchResponse> observable;
        if (Objects.equals(category, CafesPresenter.CAFES) && cafes != null && !cafes.isEmpty()) {
            SearchResponse searchResponse = new SearchResponse();
            searchResponse.setBusinesses(cafes);
            observable = Observable.just(searchResponse);
        } else if (Objects.equals(category, RestaurantsPresenter.RESTAURANTS) && restaurants != null && !restaurants.isEmpty()) {
            SearchResponse searchResponse = new SearchResponse();
            searchResponse.setBusinesses(restaurants);
            observable = Observable.just(searchResponse);
        } else if (Objects.equals(category, DeliveriesPresenter.DELIVERIES) && deliveries != null && !deliveries.isEmpty()) {
            SearchResponse searchResponse = new SearchResponse();
            searchResponse.setBusinesses(deliveries);
            observable = Observable.just(searchResponse);
        } else { //non cached
            apiService = ServiceFactory.createService(ApiService.class);
            observable = apiService.getBusinessesList(
                    term,
                    Util.mLastLocation.getLatitude(),
                    Util.mLastLocation.getLongitude(),
                    null,
                    null);
        }
        return observable;
    }

    public Observable<Business> loadBusinessDetails(String id){
        Observable<Business> observable;
        apiService = ServiceFactory.createService(ApiService.class);
        return apiService.getBusinessDetails(id);
    }

    public Observable<ReviewsResponse> loadReviews(String id){
        Observable<ReviewsResponse> observable;
        apiService = ServiceFactory.createService(ApiService.class);
        observable =  apiService.getBusinessReviews(id,"pl_PL");
        return observable;
    }

    public void saveAccessToken(AccessToken accessToken) {
        ServiceFactory.accessToken = accessToken;
        preferencesHelper.saveAccessToken(accessToken);
    }

    public void saveLocation(Location location) {
        Util.mLastLocation = location;
    }

    public synchronized void saveBusinesses(@NonNull List<Business> businesses, String category) {
        checkNotNull(businesses);
        Log.d(LOG_TAG, "Saving : " + Arrays.toString(businesses.toArray()));
        Log.d(LOG_TAG, "Kategoria : " + category);
        switch (category) {
            case CafesPresenter.CAFES:
                Log.d(LOG_TAG, "Saving cafes...");

                cafes = new ArrayList<>();
                cafes.addAll(businesses);
                //Collections.sort(cafes, Business::compareTo);

                Log.d(LOG_TAG,Arrays.toString(cafes.toArray()));
                Log.d(LOG_TAG,""+cafes.size());
                break;
            case RestaurantsPresenter.RESTAURANTS:
                Log.d(LOG_TAG, "Saving restaurants...");

                restaurants = new ArrayList<>();
                restaurants.addAll(businesses);
               // Collections.sort(restaurants, Business::compareTo);

                Log.d(LOG_TAG, Arrays.toString(restaurants.toArray()));
                Log.d(LOG_TAG,""+restaurants.size());
                break;
            case DeliveriesPresenter.DELIVERIES:
                Log.d(LOG_TAG, "Saving deliveries...");

                deliveries = new ArrayList<>();
                deliveries.addAll(businesses);
               // Collections.sort(deliveries,Business::compareTo);

                Log.d(LOG_TAG, Arrays.toString(deliveries.toArray()));
                Log.d(LOG_TAG,""+deliveries.size());
                break;
            default:
                Log.d(LOG_TAG, "Kategoria nierozpoznana");
                break;

        }
    }

    public List<Business> getRestaurants() {
        return restaurants;
    }

    public List<Business> getCafes() {
        return cafes;
    }

    public List<Business> getDeliveries() {
        return deliveries;
    }
}
