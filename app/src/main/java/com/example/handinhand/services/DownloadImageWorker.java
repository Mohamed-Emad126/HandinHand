package com.example.handinhand.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.handinhand.API.ImagesClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadImageWorker extends Worker {

    Context mContext;
    public DownloadImageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        final boolean[] isSuccessful = {false};

        String url = inputData.getString("IMAGE_URL");
        StringBuilder actualUrl = new StringBuilder();
        for(int i=url.length()-1, x = 0; i>=0; --i){
            actualUrl.append(url.charAt(i));
            if(url.charAt(i) == '/'){
                x++;
            }
            if(x == 3){
                break;
            }
        }
        ImagesClient client = RetrofitApi
                .getInstance()
                .getImagesClient();

        client.downloadImage(actualUrl.reverse().toString()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    downloadImage(response.body(), mContext);
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                            Toast.makeText(mContext, R.string.saved, Toast.LENGTH_SHORT).show(),
                            0);
                }
                else{
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                                    Toast.makeText(mContext, R.string.something_wrong, Toast.LENGTH_SHORT).show(),
                            0);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new Handler(Looper.getMainLooper()).postDelayed(() ->
                                Toast.makeText(mContext, R.string.something_wrong, Toast.LENGTH_SHORT).show(),
                        0);
            }
        });

        return Result.success();
    }

    private boolean downloadImage(ResponseBody response, Context context) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/HandInHand");
        boolean mkdirs = myDir.mkdirs();
        if(!myDir.exists()){
            if(!mkdirs){
                return false;
            }
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String fname = "HiH" + timestamp + ".jpg";

        byte[] bytes = new byte[0];
        try {
            bytes = response.bytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap finalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        File file = new File(myDir, fname);
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
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(context, new String[] { file.toString() }, null,
                (path, uri) -> {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                });
        return true;

    }
}
