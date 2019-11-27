package bokhakwang.softwareengineering;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.List;

import bokhakwang.softwareengineering.data.source.Repository;
import bokhakwang.softwareengineering.editpost.EditPostFragment;
import bokhakwang.softwareengineering.model.District;
import bokhakwang.softwareengineering.model.MapModel;
import bokhakwang.softwareengineering.model.MapModelFactory;
import bokhakwang.softwareengineering.model.Post;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static float COORDINATE_OFFSET = 0.0001f;
    private Spinner mDistrictSpinner;
    private ArrayAdapter<District> mDistrictSpinnerAdapter;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;

    private MapModel mMapModel;
    private BottomSheetBehavior mBottomSheetBehavior;

    private List<Post> mPostList;

    private ImageView mPicture;
    private TextView mTime;
    private TextView mAuthor;
    private TextView mLocation;
    private TextView mContents;

    private ProgressBar mProgressBar;

    private FusedLocationProviderClient providerClient;
    private GeoPoint myLocation = null;

    public static final CameraPosition SEOUL =
            new CameraPosition.Builder().target(new LatLng(37.5649533f, 126.9811368f))
                    .zoom(11)
                    .bearing(0)
                    .tilt(0)
                    .build();

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(List<Post> postList) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putSerializable("postList", (Serializable) postList);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("MYTAG", "onCreate");
        super.onCreate(savedInstanceState);
        mDistrictSpinnerAdapter = new DistrictSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item);
        mMapModel = MapModelFactory.createMapModel();
        mDistrictSpinnerAdapter.clear();
        mDistrictSpinnerAdapter.addAll(mMapModel.getDistrictList());

        //mPostList = (List<Post>) getArguments().getSerializable("postList");


    }

//    private void fetchPosts() {
//        Repository.getRepo(getContext()).fetchPostList(res -> {
//            if(res) {
//                mPostList = Repository.getRepo(getContext()).getPostList();
//                mMapFragment.getMapAsync(this);
//            } else {
//
//            }
//        });
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("MYTAG", "onCreateView");
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        LinearLayout linearLayout = v.findViewById(R.id.bottom_sheet_layout);
        mBottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        mPicture = v.findViewById(R.id.bottom_sheet_picture);
        mTime = v.findViewById(R.id.bottom_sheet_time);
        mAuthor = v.findViewById(R.id.bottom_sheet_author);
        mLocation = v.findViewById(R.id.bottom_sheet_location);
        mContents = v.findViewById(R.id.bottom_sheet_contents);
        mProgressBar = v.findViewById(R.id.map_progressbar);


        providerClient = LocationServices.getFusedLocationProviderClient(getContext());

        mMapFragment.getMapAsync(this);

        mDistrictSpinner = v.findViewById(R.id.district_spinner);
        mDistrictSpinner.setAdapter(mDistrictSpinnerAdapter);
        mDistrictSpinner.setSelection(0, false);
        mDistrictSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                District district = (District) parent.getSelectedItem();
                CameraPosition cameraPosition =
                        new CameraPosition.Builder().target(new LatLng(district.getLat(), district.getLong()))
                                .zoom(11)
                                .bearing(0)
                                .tilt(0)
                                .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

    private int checkSelfPermission(String accessFineLocation) {
        Log.d("MYTAG", "checkPermission");
        return 0;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MYTAG", "onMapReady");

        //GeoPoint currentLocation = getCurrentLocation();

//        CameraPosition position =
//                new CameraPosition.Builder().target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
//                        .zoom(11)
//                        .bearing(0)
//                        .tilt(0)
//                        .build();

        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(SEOUL));
        //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        moveCameraCurrentLocation();

        mProgressBar.setVisibility(View.VISIBLE);
        Repository.getRepo(getContext()).fetchPostList(result -> {
            if (result) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mPostList = Repository.getRepo(getContext()).getPostList();

                for (Post post : mPostList) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    GeoPoint geoPoint = post.getLatLng();
                    LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                    COORDINATE_OFFSET += COORDINATE_OFFSET;
                    markerOptions.position(latLng)
                            .title(post.getAuthor())
                            .snippet(post.getContents());

                    mMap.addMarker(markerOptions);
                }

            } else {
                Log.d("MYTAG", "MapFragment에서... fetch 실패");
            }
        });

//        if(mPostList != null) {
//            for(Post post : mPostList) {
//                MarkerOptions markerOptions = new MarkerOptions();
//                GeoPoint geoPoint = post.getLatLng();
//                LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
//                COORDINATE_OFFSET += COORDINATE_OFFSET;
//                markerOptions.position(latLng)
//                        .title(post.getAuthor())
//                        .snippet(post.getContents());
//
//                mMap.addMarker(markerOptions);
//            }
//        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng latLng = marker.getPosition();
                Post markerPost = null;

                for (Post post : mPostList) {
                    GeoPoint geoPoint = post.getLatLng();
                    if (geoPoint.getLatitude() == latLng.latitude && geoPoint.getLongitude() == latLng.longitude) {
                        markerPost = post;
                        break;
                    }
                }

                Glide.with(getContext()).load(markerPost.getImages().get(0)).into(mPicture);
                mTime.setText(markerPost.getTime());
                mAuthor.setText(markerPost.getAuthor());
                mLocation.setText(markerPost.getDetail_location());
                mContents.setText(markerPost.getContents());
                mBottomSheetBehavior.setPeekHeight(400);

                return false;
            }
        });
    }

    public void moveCameraCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if(chkGpsService()) {
                providerClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        myLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                        CameraPosition position =
                                new CameraPosition.Builder().target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                                        .zoom(11)
                                        .bearing(0)
                                        .tilt(0)
                                        .build();
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
                    }
                });
            }
        }
    }

    private boolean chkGpsService() {
        String gps = android.provider.Settings.Secure.getString(getActivity().getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(getContext());
            gsDialog.setTitle(R.string.msg_gps_settings_title);
            gsDialog.setMessage(R.string.msg_gps_settings_contents);
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            }).create().show();
            return false;

        } else {
            return true;
        }
    }

//    private GeoPoint getCurrentLocation() {
//
//
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
////            providerClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
////                @Override
////                public void onSuccess(Location location) {
////                    myLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
////                    CameraPosition position =
////                            new CameraPosition.Builder().target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
////                                    .zoom(11)
////                                    .bearing(0)
////                                    .tilt(0)
////                                    .build();
////                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
////                }
////            });
//
//            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
//            return geoPoint;
//        }
//
//        return null;
//    }


    public static class DistrictSpinnerAdapter extends ArrayAdapter<District> {
        private final LayoutInflater mInflater;
        private final int mResource;

        public DistrictSpinnerAdapter(@NonNull Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            mInflater = LayoutInflater.from(context);
            mResource = textViewResourceId;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final View view = mInflater.inflate(mResource, parent, false);
            ((TextView) view).setText(getItem(position).getName());
            return view;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final View view = mInflater.inflate(mResource, parent, false);
            ((TextView) view).setText(getItem(position).getName());
            return view;
        }
    }
}
