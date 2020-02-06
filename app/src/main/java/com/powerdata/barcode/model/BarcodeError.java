package com.powerdata.barcode.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "barcode_error")
public class BarcodeError {
    @PrimaryKey
    @NonNull
    public String barcode = "";

    @ColumnInfo(name = "created_at")
    public String createdAt;

    @ColumnInfo(name = "ship_no")
    public String shipNo;
}

