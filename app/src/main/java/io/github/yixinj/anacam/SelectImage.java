package io.github.yixinj.anacam;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;

public class SelectImage extends AppCompatActivity {

    String path = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        pickImage(getCurrentFocus());
    }


    /**
     * Called when the user taps the Pick Image button
     */
    public void pickImage(View view) {
        path = null;
        Log.d("A", "Image picker opening");
        ImagePicker.create(this)
                .single()   // Only one image allowed
                .showCamera(false)  // Don't show camera; no point
                .start();   // Start image picker
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a single image
            Image image = ImagePicker.getFirstImageOrNull(data);
            ImageView imagePreview = findViewById(R.id.upload_image_preview);
            path = image.getPath();
            Glide.with(imagePreview)
                    .load(path)
                    .into(imagePreview);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void analyseImage(View view) {
        if (path != null) {
            Intent intent = new Intent(this, Result.class);
            intent.putExtra("path", path);
            intent.putExtra("numContours", 3);
            startActivity(intent);
        } else {
            showToast("Please select an image.");
        }
    }

    private void showToast(final String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}

