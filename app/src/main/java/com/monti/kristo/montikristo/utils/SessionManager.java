package com.monti.kristo.montikristo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


public class SessionManager {
    // Shared preferences file name
    private static final String PREF_NAME = "Monti";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_IS_LOGGED_OUT = "isLoggedOut";
    private static final String KEY_USER_ID = "token";
    private static final String KEY_ITEM_COUNT = "count";
    private static final String GRAND_TOTAL = "total";
    private static final Double SUB_TOTAL = 0.0;
    private static final int DELIVERY_FREE = 0;
    private static final Double GRAND_TOTAL_CALCULATED = 0.0;


    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
    // Shared Preferences
    SharedPreferences pref;
    SessionManager sessionManager;
    Editor editor;
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;



	/*public static SessionManager get(){
		SessionManager sessionManager=null;
		if (sessionManager == null){
			sessionManager=new SessionManager(App.getContext());
		}
		return sessionManager;
	}*/

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public float getGrandTotal() {
        return pref.getFloat(GRAND_TOTAL, 0);
    }

    public void setGrandTotal(double value) {
        editor.putFloat(GRAND_TOTAL, 0);
        editor.commit();

    }

    public void setLoggined(boolean islogedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, islogedIn);
        editor.commit();
    }

    public void setLogOut(boolean islogedOut) {
        editor.putBoolean(KEY_IS_LOGGED_OUT, islogedOut);
        editor.commit();
    }

    public void clearBadge() {
        editor.remove(KEY_ITEM_COUNT);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean isLoggedOut() {
        return pref.getBoolean(KEY_IS_LOGGED_OUT, false);
    }

    public String getKeyUserId() {
        return pref.getString(KEY_USER_ID, "");
    }

    public void setKeyUserId(String id) {
        editor.putString(KEY_USER_ID, id);
        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public int getBadgeValue() {
        return pref.getInt(KEY_ITEM_COUNT, 0);
    }

    public void setBadgeValue(int val) {
        editor.putInt(KEY_ITEM_COUNT, val);
        editor.commit();
    }

    public static Double getSubTotal() {
        return SUB_TOTAL;
    }

    public static int getDeliveryFree() {
        return DELIVERY_FREE;
    }

    public static Double getGrandTotalCalculated() {
        return GRAND_TOTAL_CALCULATED;
    }
}
