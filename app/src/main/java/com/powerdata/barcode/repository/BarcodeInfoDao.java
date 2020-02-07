package com.powerdata.barcode.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.powerdata.barcode.model.BarcodeInfo;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface BarcodeInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(BarcodeInfo barcodeInfo);

    @Query("select * from barcode_info where ship_no = :shipNo")
    LiveData<List<BarcodeInfo>> listByShipNo(String shipNo);

    @Query("delete from barcode_info where ship_no = :shipNo")
    Completable deleteByShipNo(String shipNo);
}
