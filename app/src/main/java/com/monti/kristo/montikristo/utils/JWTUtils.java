package com.monti.kristo.montikristo.utils;

import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class JWTUtils {

    public static String decoded(String JWTEncoded) throws Exception {
        try {
            String[] split = JWTEncoded.split("\\.");
            Log.d("JWT_DECODED", "Header: " + getJson(split[0]));
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]));
            return getJson(split[1]);
        } catch (UnsupportedEncodingException e) {
            //Error
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}

