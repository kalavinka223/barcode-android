package com.powerdata.barcode.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.powerdata.barcode.model.BarcodeDetail;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface BarcodeDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable inserts(List<BarcodeDetail> barcodeDetails);

    @Query("select * from barcode_detail where ship_no = :shipNo")
    Flowable<List<BarcodeDetail>> listByShipNo(String shipNo);

    @Query("update barcode_detail set status = 1, updated_at = strftime('%Y-%m-%d %H:%M:%S', 'now','localtime')  where barcode = :barcode")
    Single<Integer> updateStatusByBarcode(String barcode);

    @Query("delete from barcode_detail where ship_no = :shipNo")
    Completable deleteByShipNo(String shipNo);

    @Query("select count(*) from barcode_detail where ship_no = :shipNo")
    LiveData<Integer> countByShiNo(String shipNo);

    @Query("select count(*) from barcode_detail where ship_no = :shipNo and status = :status")
    LiveData<Integer> countByShiNoAndStatus(String shipNo, int status);
}
