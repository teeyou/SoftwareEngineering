package bokhakwang.softwareengineering.editpost;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import bokhakwang.softwareengineering.MainActivity;
import bokhakwang.softwareengineering.MapFragment;
import bokhakwang.softwareengineering.R;
import bokhakwang.softwareengineering.firestore.FireStorage;
import bokhakwang.softwareengineering.firestore.FirebaseListener;
import bokhakwang.softwareengineering.firestore.Firestore;
import bokhakwang.softwareengineering.model.District;
import bokhakwang.softwareengineering.model.MapModel;
import bokhakwang.softwareengineering.model.MapModelFactory;
import bokhakwang.softwareengineering.model.Post;

public class EditPostFragment extends Fragment {
    private ImageView mPostPicture;
    private FloatingActionButton mLoadImageFab;
    private EditText mLocation;
    private EditText mAuthor;
    private EditText mContents;
    private EditText mPassword;
    private Button mAutoLocationBtn;
    private Button mSaveBtn;
    private Spinner mDistrictSpinner;
    private ArrayAdapter<District> mDistrictSpinnerAdapter;

    private String post_distrcit;
    private String post_author;
    private String post_detail_location;
    private String post_contents;
    private String post_password;
    private Uri post_pictureUri;
    private GeoPoint post_geoPoint;
    private ProgressBar mProgressBar;

    private LocationManager locationManager;

    public static int REQUEST_CODE = 1000;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            post_geoPoint = geoPoint;
            List<Address> mAddressList = getAddress(geoPoint);
            post_detail_location = mAddressList.get(0).getAddressLine(0);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public static EditPostFragment newInstance(Post post) {
        EditPostFragment fragment = new EditPostFragment();
        Bundle args = new Bundle();
        args.putSerializable("post", post);

        fragment.setArguments(args);
        return fragment;
    }

    public EditPostFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        Log.d("MYTAG", "startLocationUpdates");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000,
                10,
                mLocationListener
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        Log.d("MYTAG", "stopLocationUpdates");
        locationManager.removeUpdates(mLocationListener);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        mDistrictSpinnerAdapter = new MapFragment.DistrictSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item);
        MapModel mMapModel = MapModelFactory.createMapeModel();

        mDistrictSpinnerAdapter.clear();
        mDistrictSpinnerAdapter.addAll(mMapModel.getDistrictList());
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_post, container, false);
        mPostPicture = v.findViewById(R.id.edit_image);
        mLocation = v.findViewById(R.id.edit_location);
        mAuthor = v.findViewById(R.id.edit_author);
        mContents = v.findViewById(R.id.edit_contents);
        mPassword = v.findViewById(R.id.edit_password);

        mAutoLocationBtn = v.findViewById(R.id.edit_btn_auto_location);
        mSaveBtn = v.findViewById(R.id.edit_btn_save);
        mLoadImageFab = v.findViewById(R.id.edit_fab);
        mDistrictSpinner = v.findViewById(R.id.edit_spinner);
        mProgressBar = v.findViewById(R.id.edit_progressbar);

        if(getArguments() != null) {
            Post post = (Post) getArguments().getSerializable("post");
            Glide.with(getContext()).load(post.getImages().get(0)).into(mPostPicture);
            mLocation.setText(post.getDetail_location());
            mAuthor.setText(post.getAuthor());
            mContents.setText(post.getContents());
            mPassword.setText(post.getPassword());

        } else {
            mDistrictSpinner.setSelection(0, false);
            mDistrictSpinner.setAdapter(mDistrictSpinnerAdapter);
            post_distrcit = mDistrictSpinnerAdapter.getItem(0).getName();
            mLocation.setText(post_distrcit);
        }

        mDistrictSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                post_distrcit = mDistrictSpinnerAdapter.getItem(position).getName();
                mLocation.setText(post_distrcit);
                mDistrictSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mLoadImageFab.setOnClickListener(__ -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
        });

        mAutoLocationBtn.setOnClickListener(__ -> {
            if (chkGpsService()) {
                GeoPoint geoPoint = getCurrentLocation();

                if (geoPoint.getLatitude() == 0 && geoPoint.getLongitude() == 0) {
                    Toast.makeText(getContext(), R.string.toast_fail_loading_address, Toast.LENGTH_SHORT).show();

                } else {
                    List<Address> addressList = getAddress(geoPoint);

                    if (addressList.size() != 0) {
                        Address address = addressList.get(0);
                        //Log.d("MYTAG", "getAddressLine : " + address.getAddressLine(0));
                        post_detail_location = address.getAddressLine(0);
                        mLocation.setText(post_detail_location);

                    } else {
                        //주소 불러오는데 실패
                        Toast.makeText(getContext(), R.string.toast_fail_loading_address, Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });

        post_pictureUri = null;
        mSaveBtn.setOnClickListener(__ -> {
            if (post_pictureUri == null || mAuthor.getText().toString().trim().equals("") || mLocation.getText().toString().trim().equals("") || mContents.getText().toString().equals("") || mPassword.getText().toString().trim().equals("")) {
                Toast.makeText(getContext(), R.string.toast_fill_in_items, Toast.LENGTH_SHORT).show();

            } else {
                post_detail_location = mLocation.getText().toString().trim();
                post_geoPoint = getGeoPoint(post_detail_location);
                post_author = mAuthor.getText().toString().trim();
                post_contents = mContents.getText().toString().trim();
                post_password = mPassword.getText().toString().trim();
                savePost(post_pictureUri, post_author, post_geoPoint, post_distrcit, post_detail_location, post_contents, post_password);

            }
        });

        return v;
    }

    public void savePost(Uri pictureUri, String author, GeoPoint geoPoint, String distrcit, String detail_location, String contents, String password) {
        mProgressBar.setVisibility(View.VISIBLE);
        Firestore firestore = Firestore.getInstance();

        List<Uri> uriList = new ArrayList<>();
        uriList.add(pictureUri);

        FireStorage.getInstance().uploadImages(uriList, storageResult -> {
            List<String> pictureList = storageResult;

            String time = getCurrentTime();

            Post post = new Post(UUID.randomUUID().toString(), author, geoPoint, pictureList, time, distrcit, detail_location, contents, password);
            firestore.addNewPost(post, res -> {
                if(res) {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Post was saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("addPost", post);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                } else {
                    //저장실패
                }
            });
        });
    }

    public String getCurrentTime() {
        Calendar cal = Calendar.getInstance();

        String dateToString , timeToString ;

        dateToString = String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

        timeToString = String.format("%02d:%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

        return dateToString +" " + timeToString;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            Uri photoUri = null;

            if (data != null) {
                photoUri = data.getData();
                setPhotoUri(photoUri);
                mPostPicture.setImageURI(photoUri);
            }

        }
    }

    public void setPhotoUri(Uri photoUri) {
        post_pictureUri = photoUri;
    }


    private List<Address> getAddress(GeoPoint geoPoint) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addressList = null;

        try {
            addressList = geocoder.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressList;
    }

    public GeoPoint getGeoPoint(String location) {
        Geocoder mGeoCoder = new Geocoder(getContext());
        List<Address> mResult = null;

        try {
            mResult = mGeoCoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new GeoPoint(mResult.get(0).getLatitude(), mResult.get(0).getLongitude());
    }

    private GeoPoint getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            return geoPoint;

        } else {
            return new GeoPoint(0, 0);
        }
    }

    private boolean chkGpsService() {
        String gps = android.provider.Settings.Secure.getString(getActivity().getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(getContext());
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("위치 서비스 기능을 설정하시겠습니까?");
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
}
