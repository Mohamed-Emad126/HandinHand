package com.example.handinhand.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import com.example.handinhand.Utils.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class RetrofitHelper {
    @NonNull
    public static MultipartBody.Part prepareFilePart(Context context, String partName, Uri fileUri) {
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(context, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(Objects
                                .requireNonNull(context.getContentResolver().getType(fileUri))),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    @NonNull
    public static RequestBody createPartFromString(String str){
        return RequestBody.create(MultipartBody.FORM, str);
    }


    private static boolean saveImageToExternalStorage(Context context, ResponseBody image,
                                            String imageNameWithExtension) {


        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/HandInHand");


        boolean mkdirs = myDir.mkdirs();
        if(!mkdirs){
            //Toast.makeText(this, "Can't Save Image", Toast.LENGTH_SHORT).show();
            return false;
        }



        byte[] bytes = new byte[0];
        try {
            bytes = image.bytes();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Bitmap finalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);


        File file = new File(myDir, imageNameWithExtension);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(context, new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
        return true;
    }

}
