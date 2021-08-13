package br.com.rm.cfv.activities;

import static br.com.rm.cfv.activities.ImageUtilsActivity.getBitmapFromAbsolutePath;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

import br.com.rm.cfv.R;

public class VisualizarImagemActivity extends ImageUtilsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_imagem);

        Intent i = getIntent();

        String filepath = i.getStringExtra("filepath");

        Bitmap bitmap = getBitmapFromAbsolutePath(filepath);

        ImageView imageview = findViewById(R.id.imageView);

        imageview.setImageBitmap(bitmap);

        Button fechar = findViewById(R.id.buttonFechar);
        fechar.setOnClickListener(v -> finish());

        hideFab();
    }

    @Override
    public void onPostCaptureCompleted(Bitmap bitmap, String path) {

    }

    @Override
    public View getCaptureTrigger() {
        return null;
    }

    @NotNull
    @Override
    public String getToobarTitle() {
        return null;
    }
}
