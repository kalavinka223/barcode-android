package com.powerdata.barcode.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "barcode_detail")
public class BarcodeDetail {
    @PrimaryKey
    @NonNull
    public String barcode = "";

    @ColumnInfo(name = "created_at")
    public String createdAt;

    @ColumnInfo(name = "updated_at")
    public String updatedAt;

    @ColumnInfo(name = "ship_no")
    public String shipNo;

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
