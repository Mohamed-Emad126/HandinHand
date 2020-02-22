package com.example.handinhand.Helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import java.util.Objects;

public class PermissionsHelper {

    public static boolean canReadExternalStorage(Context context){
        return ContextCompat.checkSelfPermission(Objects.requireNonNull(context),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

}
