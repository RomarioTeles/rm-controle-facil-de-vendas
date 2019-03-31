package br.com.rm.cfv.activities;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import br.com.rm.cfv.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

abstract public class ImageUtilsActivity extends BaseActivity {

    String mCurrentPhotoPath;
    private final int REQUEST_TAKE_PHOTO = 1034;
    private final int MY_PERMISSIONS_REQUEST_TAKE_PHOTO = 1;

    private final String[] myPermissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onResume() {
        super.onResume();
        if(getCaptureTrigger() != null) {
            getCaptureTrigger().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePicture();
                }
            });
        }
    }

    private void takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
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
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_TAKE_PHOTO:
                if (grantResults.length == myPermissions.length) {
                    dispatchTakePictureIntent();
                    return;
                }
                break;
            default:
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

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_TAKE_PHOTO) {
            onPostCaptureCompleted(getBitmapFromAbsolutePath(mCurrentPhotoPath, true));
        }
    }

    public Bitmap getBitmapFromAbsolutePath(String filename, Boolean delete){
        File file = new File(filename);
        if(filename != null && file.exists()) {
            try {
                ExifInterface exif = new ExifInterface(filename);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Matrix matrix = new Matrix();
                matrix.postRotate(exifToDegrees(orientation));
                Bitmap myBitmap = BitmapFactory.decodeFile(filename);
                myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
                if (delete) file.delete();
                return Bitmap.createScaledBitmap(myBitmap, 200, 250, false);
            } catch (IOException e) {
                Log.e("onActivityResult", e.getMessage());
            }
        }
        return null;
    }

    public abstract void onPostCaptureCompleted(Bitmap bitmap);

    public abstract View getCaptureTrigger();

    public File createImageFile(String filename){
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(storageDir, filename);
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

}
