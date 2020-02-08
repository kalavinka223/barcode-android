package com.powerdata.barcode.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "barcode_info", primaryKeys = {"barcode", "ship_no"})
public class BarcodeInfo {
    @NonNull
    public String barcode = "";

    @ColumnInfo(name = "created_at")
    public String createdAt;

    @ColumnInfo(name = "ship_no")
    @NonNull
    public String shipNo = "";
}
