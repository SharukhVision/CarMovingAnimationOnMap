package com.sharukhmultani.CarMovingAnimationOnMap;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.StrictMath.acos;
import static java.lang.StrictMath.atan;
import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mapAPI;
    SupportMapFragment mapFragment;
    List<LatLngList> latLngListAddresses = new ArrayList<>();
    private Marker carMarker;
    TextView tvDistnace, tvTime;
    private List<LatLng> lt = new ArrayList<>();
    private LatLng currentLatlong, prevLatLong;
    private double totalDistance, mainDistance;
    private double totalDistanceTime, mainDistanceTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        tvDistnace = findViewById(R.id.tvDistnace);
        tvTime = findViewById(R.id.tvTime);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this);
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
                        final int locationDist = (int) getKilometers(prevLatLong.latitude, prevLatLong.longitude, currentLatlong.latitude, currentLatlong.longitude);
                        Log.e("TAG", "run: " + totalDistance + " : " + locationDist);
                        totalDistance -= locationDist;
                        final int locationDistTime = (int) getTime(prevLatLong.latitude, prevLatLong.longitude, currentLatlong.latitude, currentLatlong.longitude);
                        Log.e("TAG", "run: " + totalDistanceTime + " : " + locationDistTime);
                        totalDistanceTime -= locationDistTime;
                        ValueAnimator valueAnimator = new ValueAnimator();
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float multiplier = animation.getAnimatedFraction();
                                LatLng lng = new LatLng(multiplier * currentLatlong.latitude + (1 - multiplier) * prevLatLong.latitude,
                                        multiplier * currentLatlong.longitude + (1 - multiplier) * prevLatLong.longitude);
                                carMarker.setPosition(lng);
                                carMarker.setRotation(getRotation(prevLatLong, lng));

                                float distanceTime = getTime(prevLatLong.latitude, prevLatLong.longitude, lng.latitude, lng.longitude);
                                double distance = getKilometers(prevLatLong.latitude, prevLatLong.longitude, lng.latitude, lng.longitude);
                               /* tvDistnace.setText("Total KM " + mainDistance + "\nKM " + (totalDistance + (locationDist - distance)) + "\n" +
                                        "Total Time " + convertSecondsToHMmSs((long) mainDistanceTime) + "\nTime " + convertSecondsToHMmSs((long) (totalDistanceTime + (locationDistTime - distanceTime))));*/
                                DateFormat dateFormat = new SimpleDateFormat("HH:MM a");
                                Calendar cal = Calendar.getInstance();
                                tvDistnace.setText(dateFormat.format(cal.getTime()) + "| 1 Item. 400");
                                tvTime.setText(convertSecondsToHMmSs((long) (totalDistanceTime + (locationDistTime - distanceTime))));
                                //carMarker.setTitle("KM " + String.format("%.0f", distance) + " Time " + convertSecondsToHMmSs((long) distanceTime));

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
                        if (isRun) {
                            Log.e("Location", "run: left total distance : " + totalDistance + " : " + getKilometers(prevLatLong.latitude, prevLatLong.longitude, currentLatlong.latitude, currentLatlong.longitude));
                            showCarAnimation(finalI + 1, map);
                        }
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
            lt.add(new LatLng(latLngListAddresses.get(i).getLat(), latLngListAddresses.get(i).getLng()));
        }
        Bitmap icon, ic;
        icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.plate);
        ic = Bitmap.createScaledBitmap(icon, 50, 50, false);
        mapAPI.addMarker(new MarkerOptions()
                .position((lt.get(0))).title("Start " + latLngListAddresses.get(0).getPlace())
                .title("Restaurant ")
                .icon(BitmapDescriptorFactory.fromBitmap(ic)));
        icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.home);
        ic = Bitmap.createScaledBitmap(icon, 50, 50, false);
        mapAPI.addMarker(new MarkerOptions()
                .position((lt.get(lt.size() - 1))).title("End " + latLngListAddresses.get(lt.size() - 1).getPlace())
                .icon(BitmapDescriptorFactory.fromBitmap(ic))
                .title("Home")
                .rotation(360));
        //Bitmap bt = new BitmapFactory(context.resources, R.drawable.ic_car);
        icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_cars);
        ic = Bitmap.createScaledBitmap(icon, 50, 50, false);
        carMarker = mapAPI.addMarker(new MarkerOptions().position((lt.get(0))).title("Start " + latLngListAddresses.get(0).getPlace()));
        carMarker.setIcon(BitmapDescriptorFactory.fromBitmap(ic));
        carMarker.setRotation(getRotation(lt.get(0), lt.get(1)));
        //mapAPI.addPolyline(opts);
        mapAPI.addPolyline(new PolylineOptions()
                .addAll(lt)
                .color(R.color.colorBg));

        //LatLng lt = new LatLng(latLngListAddresses.get(0).getLat(), latLngListAddresses.get(0).getLng());
        mapAPI.getUiSettings().setZoomControlsEnabled(true);
        mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(lt.get(0), 8));
        totalDistance = mainDistance = getTotalDistance();
        totalDistanceTime = mainDistanceTime = getTotalTime();
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

        int speed = 10;
        return distance / speed;
    }


    public double getKilometers(double lat1, double long1, double lat2, double long2) {
        double PI_RAD = Math.PI / 180.0;
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;

        return Math.round(6371.01 * acos(sin(phi1) * sin(phi2) + cos(phi1) * cos(phi2) * cos(lam2 - lam1)));
    }

    public static String convertSecondsToHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h, m, s);
    }

    private int getTotalDistance() {
        int distance = 0;
        for (int i = 0; i < latLngListAddresses.size() - 1; i++) {
            distance += getKilometers(latLngListAddresses.get(i).getLat(), latLngListAddresses.get(i).getLng(), latLngListAddresses.get(i + 1).getLat(), latLngListAddresses.get(i + 1).getLng());
            Log.e("Location", "getTotalDistance: " + distance + " : " + getKilometers(latLngListAddresses.get(i).getLat(), latLngListAddresses.get(i).getLng(), latLngListAddresses.get(i + 1).getLat(), latLngListAddresses.get(i + 1).getLng()));
        }
        return distance;
    }

    private int getTotalTime() {
        int time = 0;
        for (int i = 0; i < latLngListAddresses.size() - 1; i++) {
            time += getTime(latLngListAddresses.get(i).getLat(), latLngListAddresses.get(i).getLng(), latLngListAddresses.get(i + 1).getLat(), latLngListAddresses.get(i + 1).getLng());
            Log.e("Location", "getTotalDistanceTime: " + time + " : " + getTime(latLngListAddresses.get(i).getLat(), latLngListAddresses.get(i).getLng(), latLngListAddresses.get(i + 1).getLat(), latLngListAddresses.get(i + 1).getLng()));
        }
        return time;
    }
}
