package io.github.yixinj.anacam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
// Your IDE likely can auto-import these classes, but there are several
// different implementations so we list them here to disambiguate


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openCamera(getCurrentFocus());
    }

    /**
     * Called when the user taps the Upload button
     */
    public void uploadImage(View view) {
        Intent intent = new Intent(this, UploadImage.class);
        startActivity(intent);
    }

    public void takePicture(View view) {
        Intent intent = new Intent(this, TakePicture.class);
        startActivity(intent);
    }

    public void openCamera(View view) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.camera_container, Camera2BasicFragment.newInstance())
                .commit();
    }


}