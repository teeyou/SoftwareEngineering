package bokhakwang.softwareengineering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1000;

    private FrameLayout mHomeLayout;
    private FrameLayout mMapLayout;
    private FrameLayout mCurrVisibleLayout;
    private MapFragment mMapFragment;
    private HomeFragment mHomeFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setFragmentVisible(mHomeFragment, R.id.fragment_home, mHomeLayout);
                    return true;
                case R.id.navigation_map:
                    setFragmentVisible(mMapFragment, R.id.fragment_map, mMapLayout);
                    return true;
            }
            return false;
        }
    };

    private void setFragmentVisible(Fragment fragment, int layoutId, FrameLayout frameLayout) {
        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(layoutId, fragment).commit();
        }
        mCurrVisibleLayout.setVisibility(View.GONE);
        frameLayout.setVisibility(View.VISIBLE);
        mCurrVisibleLayout = frameLayout;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean flag = true;

        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, R.string.auth_fine_location, Toast.LENGTH_SHORT).show();
                    flag = false;
                }
            }
        }

        if (!flag)
            finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        mHomeFragment = new HomeFragment();
        mMapFragment = new MapFragment();

        mHomeLayout = findViewById(R.id.fragment_home);
        mMapLayout = findViewById(R.id.fragment_map);

        mCurrVisibleLayout = mHomeLayout;

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home, mHomeFragment).commit();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}