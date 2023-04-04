package com.sipl.rfidtagscanner.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManagerUtil {
    final SharedPreferences.Editor editor;
    final SharedPreferences sharedPreferences;

    public PrefManagerUtil(Context context) {
        this.sharedPreferences = context.getSharedPreferences("PositionApplication", Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public float getFloat(String key) {
        return sharedPreferences.getFloat(key, 0F);
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, 0L);
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void setString(String key, String data) {
        editor.putString(key, data).commit();
    }

    public void setInt(String key, int data) {
        editor.putInt(key, data).commit();
    }

    public void setFloat(String key, float data) {
        editor.putFloat(key, data).commit();
    }

    public void setLong(String key, long data) {
        editor.putLong(key, data).commit();
    }

    public void setBoolean(String key, boolean data) {
        editor.putBoolean(key, data).commit();
    }
}
