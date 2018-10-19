package com.coco.livestreaming.app.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.coco.livestreaming.R;
import com.coco.livestreaming.app.server.response.SuccessFailureResponse;
import com.coco.livestreaming.app.server.sync.SyncInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GPSTracker implements LocationListener {

    private Context mContext = null;
    // GPS가능한가
    private boolean mIsGPSEnabled = false;
    // 네트워크가능한가
    private boolean mIsNetworkEnabled = false;
    // 위치정보얻기가 가능한가
    private boolean canGetLocation = false;
    private Location mLocation; // location
    private double mLatitude; // latitude
    private double mLongitude; // longitude
    private String mCityName;
    private SyncInfo mWebServer;

    // Declaring test2 Location Manager
    protected LocationManager   locationManager;
    protected Geocoder          gcd;

    public GPSTracker(Context context) {
        this.mContext = context;
        mWebServer = new SyncInfo(mContext);
    }

    public Location getLocation() {
        try {
            if(canGetLocation && mLocation != null)
                return mLocation;
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            mIsGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            mIsNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!mIsGPSEnabled && !mIsNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (mIsNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Constants.GPS_MIN_TIME_BW_UPDATES, Constants.GPS_MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (mLocation != null) {
                            mLatitude = mLocation.getLatitude();
                            mLongitude = mLocation.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (mIsGPSEnabled) {
                    if (mLocation == null) {
                        locationManager.requestLocationUpdates(  LocationManager.GPS_PROVIDER, Constants.GPS_MIN_TIME_BW_UPDATES, Constants.GPS_MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (mLocation != null) {
                                mLatitude = mLocation.getLatitude();
                                mLongitude = mLocation.getLongitude();
                            }
                        }
                    }
                }
                if (gcd== null) {
                    gcd = new Geocoder(mContext, Locale.getDefault());
                    getCityName();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mLocation;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(mLocation != null){
            mLatitude = mLocation.getLatitude();
        }

        // return latitude
        return mLatitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(mLocation != null){
            mLongitude = mLocation.getLongitude();
        }

        // return longitude
        return mLongitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public String getCityName(){
        mCityName = "";
        if (gcd != null) {
            List<Address> addresses = new ArrayList<>();
            try {
                if (mLocation != null) {

                    addresses = gcd.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                    //addresses = gcd.getFromLocation(37.541, 126.986, 1);
                }
                /*for(Address addr: addresses){
                    int index = addr.getMaxAddressLineIndex();
                    for(int i = 0; i <= index; i++){
                        if (addr.getAddressLine(i) == null)
                            continue;
                        mCityName += addr.getAddressLine(i);
                        mCityName += " ";
                    }
                    mCityName += "\n";
                }*/

                if (addresses != null) {
                    if (addresses.size() > 0) {
                        Address addr = addresses.get(0);
                        mCityName = addr.getCountryName() + " "                // 나라
                                + addr.getAdminArea() + " "                 // 시
                                + addr.getLocality();                        // 구
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this.mCityName;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
               new UpdateLocationAsync().execute();
            }
        }, 10);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    //위치정보가 갱신되는 경우 봉사기에 위치를 알려준다.
    class UpdateLocationAsync extends AsyncTask<String, String, SuccessFailureResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected SuccessFailureResponse doInBackground(String... strs) {
            String cityname = mLocation == null ? "" : getCityName();
            String latitude = mLocation == null ? "" : String.valueOf(getLatitude());
            String longitude = mLocation == null ? "" : String.valueOf(getLongitude());
            return mWebServer.syncUpdateLocation(cityname, latitude, longitude);
        }
        @Override
        protected void onPostExecute(SuccessFailureResponse result) {
            super.onPostExecute(result);
            if (result != null) {
                if(result.isSuccess()){
                    //Toast.makeText(mContext, result.getResult(), Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(mContext, "GPS update failure!", Toast.LENGTH_LONG).show();
                }
            } else {
                //Toast.makeText(mContext, "web error!", Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
