package com.example.mirry.recognizedemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by Mirry on 2017/12/11.
 */

public class SPUtil {

    public static boolean getBoolean(Context context, String name, String key, boolean defaultValue){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }
    public static void setBoolean(Context context, String name,String key,boolean value){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String name, String key, int defaultValue){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }
    public static void setInt(Context context, String name,String key,int value){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static String getString(Context context, String name,String key, String defaultValue){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }
    public static void setString(Context context,String name,String key,String value){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static long getLong(Context context, String name, String key, long defaultValue){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getLong(key, defaultValue);
    }
    public static void setLong(Context context, String name,String key,long value){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static float getFloat(Context context, String name, String key, float defaultValue){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getFloat(key, defaultValue);
    }
    public static void setFloat(Context context, String name,String key,float value){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static Map<String,String> getAll(Context context, String name){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Map<String, String> all = (Map<String, String>) sp.getAll();
        return all;
    }

    public static void deleteItem(Context context,String name ,String key){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void clearAll(Context context,String name){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
}

