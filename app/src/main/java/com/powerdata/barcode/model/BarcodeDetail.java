package com.powerdata.barcode.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "barcode_detail", primaryKeys = {"barcode", "ship_no"})
public class BarcodeDetail {
    @NonNull
    public String barcode = "";

    @ColumnInfo(name = "created_at")
    public String createdAt;

    @ColumnInfo(name = "updated_at")
    public String updatedAt;

    @ColumnInfo(name = "ship_no")
    @NonNull
    public String shipNo = "";

    @ColumnInfo(name = "pile_no")
    public String pileNo;

    @ColumnInfo(name = "status")
    public int status;

    @ColumnInfo(name = "thickness")
    public float thickness;

    @ColumnInfo(name = "weight")
    public float weight;

    @ColumnInfo(name = "mark")
    public String mark;
}
