package com.powerdata.barcode.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.powerdata.barcode.model.BarcodeDetail;
import com.powerdata.barcode.model.BarcodeError;
import com.powerdata.barcode.model.BarcodeInfo;

@Database(entities = {BarcodeInfo.class, BarcodeDetail.class, BarcodeError.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BarcodeInfoDao barcodeInfoDao();

    public abstract BarcodeDetailDao barcodeDetailDao();

    public abstract BarcodeErrorDao barcodeErrorDao();
}
