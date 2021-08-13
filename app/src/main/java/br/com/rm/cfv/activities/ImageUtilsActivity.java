package br.com.rm.cfv.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.rm.cfv.R;

abstract public class ImageUtilsActivity extends BaseActivity {

    String mCurrentPhotoPath;
    private final int MY_PERMISSIONS_REQUEST_TAKE_PHOTO = 1;

    private final String[] myPermissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onResume() {
        super.onResume();
        if(getCaptureTrigger() != null) {
            getCaptureTrigger().setOnClickListener(v -> takePicture());
        }
    }

    private void takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            choosePictureSource();
        }else{
            ActivityCompat.requestPermissions(this,
                    myPermissions,
                    MY_PERMISSIONS_REQUEST_TAKE_PHOTO);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpeg");
            mCurrentPhotoPath = photoFile.getAbsolutePath();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.setClipData(ClipData.newRawUri(null, photoURI));
                takePictureActivityResultLauncher.launch(takePictureIntent);
            }
        }
    }

    private final ActivityResultLauncher<Intent> takePictureActivityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            Integer resultCode = result.getResultCode();
            if(resultCode == Activity.RESULT_OK) {
                onPostCaptureCompleted(getBitmapFromAbsolutePath(mCurrentPhotoPath), mCurrentPhotoPath);
            }
        }
    );

    private final ActivityResultLauncher<Intent> getPictureGalleryActivityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            Integer resultCode = result.getResultCode();
            Intent data = result.getData();
            if(resultCode == Activity.RESULT_OK) {
                try {
                    Uri selectedImage = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    try (FileOutputStream out = new FileOutputStream(mCurrentPhotoPath)) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        onPostCaptureCompleted(bitmap, mCurrentPhotoPath);
                    }
                } catch (IOException e) {
                    Log.i("TAG", "Some exception " + e);
                }
            }
        }
    );

    private void dispatchGalleryIntent(){
        File photoFile = createImageFile(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpeg");
        mCurrentPhotoPath = photoFile.getAbsolutePath();
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        getPictureGalleryActivityResultLauncher.launch(photoPickerIntent);
    }

    private void choosePictureSource(){
        new AlertDialog.Builder(this)
                .setItems(new String[]{"Tirar uma foto", "Imagem Existente", "Remover imagem", "Cancelar"},
                        (dialog, which) -> {
                            switch (which){
                                case 0:
                                    dispatchTakePictureIntent();
                                    break;
                                case 1:
                                    dispatchGalleryIntent();
                                    break;
                                case 2:
                                    onPostCaptureCompleted(null, null);
                                    break;
                                default:
                                    Log.i("Take Picture", "Default Selected");
                            }
                            dialog.dismiss();
                        }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_TAKE_PHOTO) {
            if (grantResults.length == myPermissions.length) {
                dispatchTakePictureIntent();
            }
        } else {
            AlertDialog.Builder alertDialogBuilder =
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.camera_configuracao))
                            .setMessage(getString(R.string.habilitar_camera_mensagem))
                            .setPositiveButton(getString(R.string.configuracoes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivities(new Intent[]{intent});
                                }
                            })
                            .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

            alertDialogBuilder.show();
        }
    }

    public static Bitmap getBitmapFromAbsolutePath(String filename){
        if(filename != null){
            File file = new File(filename);
            if(file.exists()) {
                try {
                    ExifInterface exif = new ExifInterface(filename);

                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(exifToDegrees(orientation));
                    Bitmap myBitmap = BitmapFactory.decodeFile(filename);
                    myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
                    return myBitmap ;// Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth(), myBitmap.getHeight(), false);
                } catch (IOException e) {
                    Log.e("onActivityResult", e.getMessage());
                }
            }
        }
        return null;
    }

    public abstract void onPostCaptureCompleted(Bitmap bitmap, String path);

    public abstract View getCaptureTrigger();

    public File createImageFile(String filename){
        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(filename, null, storageDir);
        }catch (Exception e){
            Toast.makeText(this, "IMAGE CAPTURE ERROR!", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

}
