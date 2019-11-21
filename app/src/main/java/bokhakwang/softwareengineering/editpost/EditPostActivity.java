package bokhakwang.softwareengineering.editpost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import bokhakwang.softwareengineering.R;
import bokhakwang.softwareengineering.model.Post;

public class EditPostActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1010;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean flag = true;

        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, R.string.auth_write_external_storage, Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_edit_post);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }

        if (getIntent().getAction() != null) {
            if (getIntent().getAction().equals("update")) {
                Post post = (Post) getIntent().getSerializableExtra("post");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, EditPostFragment.newInstance(post)).commit();
            }
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EditPostFragment()).commit();
        }
    }
}
