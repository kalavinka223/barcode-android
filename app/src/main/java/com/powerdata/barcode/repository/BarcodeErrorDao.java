package com.powerdata.barcode.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.powerdata.barcode.model.BarcodeError;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface BarcodeErrorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(BarcodeError error);

    @Query("select * from barcode_error where ship_no = :shipNo order by created_at desc")
    Flowable<List<BarcodeError>> listByShipNo(String shipNo);

    @Query("select count(*) from barcode_error where ship_no = :shipNo")
    LiveData<Integer> countByShiNo(String shipNo);

    @Query("delete from barcode_error where ship_no = :shipNo")
    Completable deleteByShipNo(String shipNo);
}
