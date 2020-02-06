package com.powerdata.barcode.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.powerdata.barcode.model.BarcodeInfo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface BarcodeInfoDao {

}
