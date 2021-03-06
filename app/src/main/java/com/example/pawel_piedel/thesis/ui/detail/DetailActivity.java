package com.example.pawel_piedel.thesis.ui.detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.pawel_piedel.thesis.R;
import com.example.pawel_piedel.thesis.data.model.Business;
import com.example.pawel_piedel.thesis.data.model.Hour;
import com.example.pawel_piedel.thesis.data.model.Review;
import com.example.pawel_piedel.thesis.ui.base.BaseActivity;
import com.example.pawel_piedel.thesis.ui.main.BusinessAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Pawel_Piedel on 27.07.2017.
 */

public class DetailActivity extends BaseActivity implements DetailContract.View {
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Inject
    DetailPresenter<DetailContract.View> presenter;

    @BindView(R.id.collapsing_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.business_image)
    ImageView imageView;

    @BindView(R.id.title_details)
    TextView title;

    @BindView(R.id.rating_bar_details)
    RatingBar ratingBar;

    @BindView(R.id.rating_details)
    TextView rating;

    @BindView(R.id.review_count_details)
    TextView review_count;

    @BindView(R.id.zipcode_details)
    TextView address;

    @BindView(R.id.distance_details)
    TextView distance;

    @BindView(R.id.street_details)
    TextView street;

    @BindView(R.id.today_hours)
    TextView todayHours;

    @BindView(R.id.horizontal_recycler_view)
    RecyclerView horizontalRecyclerView;

    @BindView(R.id.reviews_recycler_view)
    RecyclerView reviewsRecyclerView;

    @BindView(R.id.favourite_action)
    ImageButton favouriteButton;

    private ReviewsAdapter reviewsAdapter;

    private Business newBusiness;

    private String businessId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityComponent().inject(this);

        init();

        getOldBusinessFromIntent();

        setUpToolbar();

        reviewsAdapter = new ReviewsAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reviewsRecyclerView.setLayoutManager(linearLayoutManager);
        reviewsRecyclerView.setAdapter(reviewsAdapter);
        reviewsRecyclerView.setNestedScrollingEnabled(false);

        presenter.onViewPrepared(businessId);

    }

    private void init() {
        setContentView(R.layout.activity_details);

        setUnBinder(ButterKnife.bind(this));

        presenter.attachView(this);
    }

    @Override
    public void showBusinessDetails(Business business) {
        showBusinessImage(business);

        title.setText(String.format("%s", business.getName()));

        ratingBar.setRating((float) business.getRating());
        rating.setText(String.format("%s", business.getRating()));

        review_count.setText(String.format("(%s)", business.getReviewCount()));

        street.setText(business.getLocation().getAddress1());

        address.setText(String.format("%s %s", business.getLocation().getZipCode(), business.getLocation().getCity()));

        Log.d(LOG_TAG,""+business.getDistance());

        double oldDistance = getBusinessFromIntent().getDistance();
        business.setDistance(oldDistance);

        distance.setText(String.format("%.1f km", business.getDistance() / 1000));

        setUpHorizontalRecyclerView(business);

        if (business.getHours() != null) {
            boolean isOpenNow = false;
            for (Hour hour : business.getHours()) {
                if (hour.isIsOpenNow()) {
                    todayHours.setText(R.string.open_now);
                    isOpenNow = true;
                }
            }
            if (!isOpenNow) {
                todayHours.setText(R.string.closed_now);
                todayHours.setTextColor(Color.RED);
            }

        }

       presenter.showFavouriteIcon(business);


    }

    @Override
    public void fillFavouriteIcon() {
        favouriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
    }

    @Override
    public void showBorderIcon() {
        favouriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
    }

    @Override
    public void showReviews(List<Review> reviews) {
        reviewsAdapter.setReviews(reviews);
    }


    private void showBusinessImage(Business business) {
        Glide.with(this)
                .load(business.getImageUrl())
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @Override
    public void showOldBusiness() {
        Business business = getBusinessFromIntent();

        showBusinessDetails(business);
    }

    private Business getBusinessFromIntent(){
        return (Business) getIntent().getSerializableExtra(BusinessAdapter.BUSINESS);
    }

    @Override
    @OnClick(R.id.call_action)
    public void onCallButtonClicked() {
        presenter.onCallButtonClicked(newBusiness);
    }

    @Override
    @OnClick(R.id.website_action)
    public void onWebsiteButtonclicked() {
        presenter.onWebsiteButtonClicked(newBusiness);
    }

    @Override
    @OnClick(R.id.favourite_action)
    public void onFavouriteButtonClicked() {
        presenter.addOrRemoveFromFavourites(newBusiness);
    }

    @Override
    public void makeCall(Business business) {
        if (business.getPhone() != null) {
            startPhoneIntent(business);

        }
        else {
            Toast.makeText(this,"Phone not provided!",Toast.LENGTH_SHORT).show();
        }
    }

    private void startPhoneIntent(Business business) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + business.getPhone()));
        getViewActivity().startActivity(intent);
    }


    @Override
    public void goToWebsite(Business business) {
        if (business.getUrl() != null) {
            String url = business.getUrl();
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            getViewActivity().startActivity(browserIntent);
        }
    }


    private void setUpToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setUpHorizontalRecyclerView(Business business) {
        if (business.getPhotos() != null && !business.getPhotos().isEmpty()) {
            HorizontalPhotosAdapter horizontalAdapter = new HorizontalPhotosAdapter(this, new ArrayList<>(business.getPhotos()));
            horizontalRecyclerView.setAdapter(horizontalAdapter);
            horizontalAdapter.notifyDataSetChanged();

            LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            horizontalRecyclerView.setLayoutManager(horizontalLayoutManagaer);
            horizontalRecyclerView.setAdapter(horizontalAdapter);
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void getOldBusinessFromIntent() {
        Business business = (Business) getIntent().getSerializableExtra(BusinessAdapter.BUSINESS);
        businessId = business.getId();
        Log.d(LOG_TAG, "Business id : " + businessId);
    }

    @Override
    public Activity getViewActivity() {
        return this;
    }


    @Override
    public void setUpBusiness(Business business) {
        this.newBusiness = business;
    }

    public void setPresenter(DetailPresenter<DetailContract.View> presenter) {
        this.presenter = presenter;
    }

    public Business getNewBusiness() {
        return newBusiness;
    }

    public String getBusinessId() {
        return businessId;
    }
}
