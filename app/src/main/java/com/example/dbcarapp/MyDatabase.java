package com.example.dbcarapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class MyDatabase extends SQLiteAssetHelper {
    public static final String DB_NAME="cars.dp";
    public static final int DB_VERSION=1;

    public static final String CAR_TB_NAME="car";
    public static final String CAR_CLN_ID="id";
    public static final String CAR_CLN_MODEL="model";
    public static final String CAR_CLN_COLOR="color";
    public static final String CAR_CLN_DESCRIPTION="description";
    public static final String CAR_CLN_IMAGE="image";
    public static final String CAR_CLN_DPL="distancePerLetter";

    public MyDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
}
