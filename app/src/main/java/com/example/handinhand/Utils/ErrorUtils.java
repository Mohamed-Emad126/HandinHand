package com.example.handinhand.Utils;

import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Models.ApiErrors;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {

    public static ApiErrors parseError(Response<?> response) {
        Converter<ResponseBody, ApiErrors> converter =
                RetrofitApi.getInstance().getRetrofit()
                        .responseBodyConverter(ApiErrors.class, new Annotation[0]);

        ApiErrors error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ApiErrors();
        }

        return error;
    }
}