package bokhakwang.softwareengineering;

import android.content.Context;
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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

import bokhakwang.softwareengineering.data.source.Repository;
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

    public static final CameraPosition SEOUL =
            new CameraPosition.Builder().target(new LatLng(37.5649533f, 126.9811368f))
                    .zoom(11)
                    .bearing(0)
                    .tilt(0)
                    .build();

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("MYTAG", "onCreate");
        super.onCreate(savedInstanceState);
        mDistrictSpinnerAdapter = new DistrictSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item);
        mMapModel = MapModelFactory.createMapeModel();
        mDistrictSpinnerAdapter.clear();
        mDistrictSpinnerAdapter.addAll(mMapModel.getDistrictList());

        mPostList = Repository.getRepo(getContext()).getPostList();
    }

    private void fetchPosts() {
        Repository.getRepo(getContext()).fetchPostList(res -> {
            if(res) {
                mPostList = Repository.getRepo(getContext()).getPostList();
                mMapFragment.getMapAsync(this);
            } else {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("MYTAG", "onCreateView");
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        LinearLayout linearLayout = v.findViewById(R.id.bottom_sheet_layout);
        mBottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        mPicture = v.findViewById(R.id.bottom_sheet_picture);
        mTime = v.findViewById(R.id.bottom_sheet_time);
        mAuthor = v.findViewById(R.id.bottom_sheet_author);
        mLocation = v.findViewById(R.id.bottom_sheet_location);
        mContents = v.findViewById(R.id.bottom_sheet_contents);

        mDistrictSpinner = v.findViewById(R.id.district_spinner);
        mDistrictSpinner.setAdapter(mDistrictSpinnerAdapter);
        mDistrictSpinner.setSelection(0,false);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MYTAG", "onMapReady");
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(SEOUL));

        if(mPostList != null) {
            for(Post post : mPostList) {
                MarkerOptions markerOptions = new MarkerOptions();
                GeoPoint geoPoint = post.getLatLng();
                LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                COORDINATE_OFFSET += COORDINATE_OFFSET;
                markerOptions.position(latLng)
                        .title(post.getAuthor())
                        .snippet(post.getContents());

                mMap.addMarker(markerOptions);
            }
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng latLng = marker.getPosition();
                Post markerPost = null;

                for(Post post : mPostList) {
                    GeoPoint geoPoint = post.getLatLng();
                    if(geoPoint.getLatitude() == latLng.latitude && geoPoint.getLongitude() == latLng.longitude) {
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
            ((TextView)view).setText(getItem(position).getName());
            return view;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final View view = mInflater.inflate(mResource, parent, false);
            ((TextView)view).setText(getItem(position).getName());
            return view;
        }
    }
}
