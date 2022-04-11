

package com.ultrafastappconductor.ultrafastconductor.Activitys.ModuloMisPublicaciones.PublicarViajeActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.ultrafastappconductor.ultrafastconductor.Providers.Authprovider;
import com.ultrafastappconductor.ultrafastconductor.Providers.Choferprovider;
import com.ultrafastappconductor.ultrafastconductor.R;

import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicarViajeOrigen extends AppCompatActivity {
    private double mExtraOrigenlat;
    private double mExtraOrigenlong;
    CircleImageView nexpubli;
    CircleImageView salir;
    String cityOrigen=" ";
    private boolean mOriginSelect = true;
    private boolean marcador = false;
    Authprovider authprovider;
    Choferprovider choferprovider;
    private FusedLocationProviderClient mFusedLocation;
    private LocationRequest mLocationrequest;

    GoogleMap mMapM;
    LatLng mOriginlnLo;
    private PlacesClient mplaces;
    private String mOrigin;

    private GoogleMap.OnCameraIdleListener mcameraListener;
    private final static int LOCATION_REQUEST_CODE = 1;
    private final int REQUEST_CHECHK_SETIING = 0x1;
    private SupportMapFragment mapFragment;
    private AutocompleteSupportFragment mAutocomplete;
    private GoogleApiClient mGoogleApiClient;
    private LatLng mlatLng;

    LocationCallback mLocationCallbal = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (marcador==false)
                {
                    if (getApplicationContext() != null) {
                    /*marker = mMapM.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(), location.getLongitude())).title("Tu posicion").icon(BitmapDescriptorFactory.fromResource(R.drawable.point)));*/

                        mlatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMapM.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .zoom(15f)
                                        .build()
                        ));
                        LimitadSearch();
                        marcador=true;
                    }
                    else{

                    }
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicarviaje);
        nexpubli = findViewById(R.id.nextpubli);
        salir=findViewById(R.id.salir);

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this::onMapReady);
        mGoogleApiClient = getApiCientInstance();
        if (mGoogleApiClient!=null)
        {
            mGoogleApiClient.connect();
        }
        checklocationpermiss();
        startLocation();

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }
        mplaces = Places.createClient(this);
        instanceAutocompleteOrigin();
        onCameramove();

        nexpubli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cityOrigen.equals("Centro")||cityOrigen=="Centro"||cityOrigen==null||cityOrigen==" ")
                {
                    Toast.makeText(PublicarViajeOrigen.this, "Por favor arrastra el icono a otro punto "+cityOrigen, Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(PublicarViajeOrigen.this,PublicarViajeDestino.class);
                    intent.putExtra("origin_lat", mOriginlnLo.latitude);
                    intent.putExtra("origin_log", mOriginlnLo.longitude);
                    intent.putExtra("origin", mOrigin);
                    intent.putExtra("cityorigen", cityOrigen);
                    startActivity(intent);
                }
            }
        });
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void LimitadSearch() {
        LatLng northSide = SphericalUtil.computeOffset(mlatLng, 5000, 0);
        LatLng southSide = SphericalUtil.computeOffset(mlatLng, 5000, 180);
        // mAutocomplete.setCountry("MX");
        mAutocomplete.setLocationBias(RectangularBounds.newInstance(southSide, northSide));
        mAutocomplete.setCountry("MX");
        mAutocomplete.setLocationBias(RectangularBounds.newInstance(southSide, northSide));
    }
    private GoogleApiClient getApiCientInstance() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();
        return googleApiClient;
    }
    private void instanceAutocompleteOrigin() {

        mAutocomplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placeAutoCompleteDes);
        mAutocomplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME,Place.Field.ADDRESS));
        mAutocomplete.setHint("¿Dónde es tu salida?");
        mAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mOrigin = place.getAddress();
                mOriginlnLo = place.getLatLng();

                mMapM.moveCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(new LatLng(mOriginlnLo.latitude, mOriginlnLo.longitude))
                                .zoom(15f)
                                .build()
                ));
            }
            @Override
            public void onError(@NonNull Status status) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        choferprovider = new Choferprovider();
        authprovider = new Authprovider();
        choferprovider.getusuario(authprovider.getid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String status = snapshot.child("status").getValue().toString();
                    if (status.equals("Sin validar"))
                    {
                        Intent intent=new Intent(PublicarViajeOrigen.this, AvisoValidar.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    else
                    {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void onCameramove()
    {
        mcameraListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {


                    Geocoder geocoder = new Geocoder(PublicarViajeOrigen.this);

                    mOriginlnLo = mMapM.getCameraPosition().target;
                    List<Address> addressList = geocoder.getFromLocation(mOriginlnLo.latitude, mOriginlnLo.longitude, 1);
                    cityOrigen = addressList.get(0).getLocality().trim();
                    String pais = addressList.get(0).getCountryName();
                    String addres = addressList.get(0).getAddressLine(0);

                    String state = addressList.get(0).getAdminArea();
                    String postalCode = addressList.get(0).getPostalCode();

                    mOrigin = cityOrigen + " " + addres+" "+pais;
                    mAutocomplete.setText(cityOrigen + " " + addres+" "+pais);
                    mOriginSelect = false;
                    Log.d("placenta","ciudad: "+cityOrigen+", Pais: "+pais+", direccion: "+addres);

                } catch (Exception e) {
                    Log.d("Error" + " Error", e.getMessage());
                }

            }
        };


    }
    private void onMapReady(GoogleMap googleMap) {
        mMapM = googleMap;
        mMapM.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMapM.getUiSettings().setZoomControlsEnabled(true);

        mMapM.setOnCameraIdleListener(mcameraListener);
        mLocationrequest = new LocationRequest();
        mLocationrequest.setInterval(1000);
        mLocationrequest.setFastestInterval(1000);
        mLocationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationrequest.setSmallestDisplacement(5);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }



        startLocation();
        //mMapM.setMyLocationEnabled(true);
    }
    private boolean gpsActive()
    {
        boolean isActv=false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            isActv=true;

        }
        return isActv;
    }
    private void startLocation()
    {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            {
                if (gpsActive())
                {

                    mFusedLocation.requestLocationUpdates(mLocationrequest, mLocationCallbal, Looper.myLooper());
                }
                else
                {
                    //alertDialog();
                    RequestGPSSetting();
                }
            }
            else
            {
                checklocationpermiss();
            }

        }
        else {
            if (gpsActive())
            {
                mFusedLocation.requestLocationUpdates(mLocationrequest, mLocationCallbal, Looper.myLooper());
            }
            else
            {
                // alertDialog();
                RequestGPSSetting();

            }
        }

    }
    private void checklocationpermiss()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion necesita permisos para usarse")
                        .setPositiveButton("Otorgar permisos", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(PublicarViajeOrigen.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);


                            }
                        })
                        .create()
                        .show();

            }
            else
            {
                ActivityCompat.requestPermissions(PublicarViajeOrigen.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }


        }

    }
    private void RequestGPSSetting() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationrequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                if (status.getStatusCode() == LocationSettingsStatusCodes.SUCCESS) {
                    Toast.makeText(PublicarViajeOrigen.this, "El gps ya está activado", Toast.LENGTH_SHORT).show();
                } else if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        status.startResolutionForResult(PublicarViajeOrigen.this, REQUEST_CHECHK_SETIING);

                    } catch (IntentSender.SendIntentException e) {
                        Toast.makeText(PublicarViajeOrigen.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (ActivityCompat.checkSelfPermission(PublicarViajeOrigen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PublicarViajeOrigen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mFusedLocation.requestLocationUpdates(mLocationrequest, mLocationCallbal, Looper.myLooper());
                    mMapM.setMyLocationEnabled(false);

                } else if (status.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                    Toast.makeText(PublicarViajeOrigen.this, "La configuracion del gps tiene un error", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}