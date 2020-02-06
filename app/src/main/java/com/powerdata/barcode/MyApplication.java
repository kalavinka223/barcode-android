package com.powerdata.barcode;

import android.app.Application;

import androidx.room.Room;

import com.powerdata.barcode.repository.AppDatabase;

public class MyApplication extends Application {

    public static AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = Room.databaseBuilder(this, AppDatabase.class, "barcode.db")
                .fallbackToDestructiveMigration()
                .build();
    }
}
