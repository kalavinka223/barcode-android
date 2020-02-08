package com.powerdata.barcode.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "barcode_error", primaryKeys = {"barcode", "ship_no"})
public class BarcodeError {
    @NonNull
    public String barcode = "";

    @ColumnInfo(name = "created_at")
    public String createdAt;

    @NonNull
    @ColumnInfo(name = "ship_no")
    public String shipNo = "";
}

