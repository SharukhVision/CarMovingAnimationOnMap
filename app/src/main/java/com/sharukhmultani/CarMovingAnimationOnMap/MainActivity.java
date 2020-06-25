package com.sharukhmultani.CarMovingAnimationOnMap;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import static java.lang.StrictMath.atan;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mapAPI;
    SupportMapFragment mapFragment;
    List<LatLngList> latLngListAddresses = new ArrayList<>();
    private Marker carMarker;
    private List<LatLng> lt = new ArrayList<>();
    private LatLng currentLatlong, prevLatLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this);
    }


    private List<LatLng> getLocations() {
        List<LatLng> locationList = new ArrayList<>();
        locationList.add(new LatLng(28.436970000000002, 77.11272000000001));
        locationList.add(new LatLng(28.43635, 77.11289000000001));
        locationList.add(new LatLng(28.4353, 77.11317000000001));
        locationList.add(new LatLng(28.435280000000002, 77.11332));
        locationList.add(new LatLng(28.435350000000003, 77.11368));
        locationList.add(new LatLng(28.4356, 77.11498));
        locationList.add(new LatLng(28.435660000000002, 77.11519000000001));
        locationList.add(new LatLng(28.43568, 77.11521));
        locationList.add(new LatLng(28.436580000000003, 77.11499));
        locationList.add(new LatLng(28.436590000000002, 77.11507));

        return locationList;
    }

    private void showCarAnimation(final int finalI, final GoogleMap map) {

        final boolean isRun = finalI < lt.size() - 1;
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (prevLatLong == null) {
                        currentLatlong = lt.get(finalI);
                        prevLatLong = currentLatlong;
                        if (isRun)
                            showCarAnimation(finalI + 1, map);
                    } else {
                        prevLatLong = currentLatlong;
                        currentLatlong = lt.get(finalI);
                        Log.e("TAG", "run: " + prevLatLong.latitude + " : " + currentLatlong.latitude);
                        ValueAnimator valueAnimator = new ValueAnimator();
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float multiplier = animation.getAnimatedFraction();
                                LatLng lng = new LatLng(multiplier * currentLatlong.latitude + (1 - multiplier) * prevLatLong.latitude,
                                        multiplier * currentLatlong.longitude + (1 - multiplier) * prevLatLong.longitude);
                                carMarker.setPosition(lng);
                                carMarker.setRotation(getRotation(prevLatLong, lng));

                                float distanceTime = getTime(currentLatlong.latitude, currentLatlong.longitude, prevLatLong.latitude, prevLatLong.longitude);
                                carMarker.setTitle(String.valueOf(distanceTime));
                                //map.animateCamera(CameraUpdateFactory.newLatLngZoom(lng, 10f));
                            }
                        });
                        valueAnimator.setFloatValues(0f, 1f); // Ignored.
                        valueAnimator.setDuration(3000);
                        valueAnimator.start();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 3000);
                        Log.e("aasd", "run: again");
                        if (isRun)
                            showCarAnimation(finalI + 1, map);
                    }
                }
            }, 3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private float getRotation(LatLng start, LatLng end) {
        Double latDifference = Math.abs(start.latitude - end.latitude);
        Double lngDifference = Math.abs(start.longitude - end.longitude);
        float rotation = -1F;
        if (start.latitude < end.latitude && start.longitude < end.longitude) {
            rotation = (float) Math.toDegrees(atan(lngDifference / latDifference));
        } else if (start.latitude >= end.latitude && start.longitude < end.longitude) {
            rotation = (float) (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 90);
        } else if (start.latitude >= end.latitude && start.longitude >= end.longitude) {
            rotation = (float) (Math.toDegrees(atan(lngDifference / latDifference)) + 180);
        } else if (start.latitude < end.latitude && start.longitude >= end.longitude) {
            rotation =
                    (float) (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 270);
        }

        return rotation;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap;
       /* LatLng Songadh=new LatLng(21.1616734,73.5475854);
        mapAPI.addMarker(new MarkerOptions().position(Songadh).title("Songadh, Junagam"));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLng(Songadh));*/
        latLngListAddresses = LatLngList.setLatLng();
        PolylineOptions opts = new PolylineOptions();
        for (int i = 0; i < latLngListAddresses.size(); i++) {
            //LatLng lt = new LatLng(latLngListAddresses.get(i).getLat(), latLngListAddresses.get(i).getLng());
            lt.add(new LatLng(latLngListAddresses.get(i).getLat(), latLngListAddresses.get(i).getLng()));
            // opts = new PolylineOptions().add(lt).color(Color.BLUE).width(10)
        }
        mapAPI.addMarker(new MarkerOptions().position((lt.get(0))).title("Start " + latLngListAddresses.get(0).getPlace()));
        mapAPI.addMarker(new MarkerOptions().position((lt.get(lt.size() - 1))).title("End " + latLngListAddresses.get(lt.size() - 1).getPlace()));
        //Bitmap bt = new BitmapFactory(context.resources, R.drawable.ic_car);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_car);
        Bitmap ic = Bitmap.createScaledBitmap(icon, 50, 100, false);

        carMarker = mapAPI.addMarker(new MarkerOptions().position((lt.get(0))).title("Start " + latLngListAddresses.get(0).getPlace()));
        carMarker.setIcon(BitmapDescriptorFactory.fromBitmap(ic));
        carMarker.setRotation(getRotation(lt.get(0), lt.get(1)));

        //mapAPI.addPolyline(opts);
        mapAPI.addPolyline(new PolylineOptions().addAll(lt));

        //LatLng lt = new LatLng(latLngListAddresses.get(0).getLat(), latLngListAddresses.get(0).getLng());
        mapAPI.getUiSettings().setZoomControlsEnabled(true);
        mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(lt.get(0), 8));

        showCarAnimation(0, mapAPI);
    }

    private float getTime(double lat1, double lon1, double lat2, double lon2) {
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);

        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);

        float distance = loc1.distanceTo(loc2);

        int speed = 30;
        return distance / speed;
    }
}
