package bokhakwang.softwareengineering;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class PictureActivity extends AppCompatActivity {
    private ImageView mPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        mPicture = findViewById(R.id.picture);

        if(getIntent() != null) {
            String url = (String) getIntent().getSerializableExtra("picture");
            Glide.with(getApplicationContext()).load(url).into(mPicture);
        }
    }
}
