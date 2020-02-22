package com.example.handinhand.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.handinhand.R;
import static android.content.Context.MODE_PRIVATE;


public class SharedPreferenceHelper {



    public static String getToken(Context context){
        return context.getSharedPreferences(
                context.getString(R.string.token_shared_preference),MODE_PRIVATE)
                .getString(context.getString(R.string.token), "");
    }

    public static void saveToken(Context ctx, String token){
        SharedPreferences sharedPref = ctx.getSharedPreferences(
                ctx.getString(R.string.token_shared_preference), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ctx.getString(R.string.token), token);
        editor.apply();
    }

    public static void removeToken(Context ctx){
        SharedPreferences sharedPref = ctx.getSharedPreferences(
                ctx.getString(R.string.token_shared_preference), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(ctx.getString(R.string.token));
        editor.apply();
    }
}
