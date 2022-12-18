package com.example.griyalaundry.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "griyas")
data class Griya(
    @PrimaryKey(autoGenerate = true) var id: Long=0,
    @ColumnInfo(name = "tId") var tId: String,
    @ColumnInfo(name = "nama") var nama: String,
    @ColumnInfo(name = "berat") var berat: Float,
    @ColumnInfo(name = "isRegular") var isRegular: Boolean,
    @ColumnInfo(name = "masuk") var masuk: String,
    @ColumnInfo(name = "keluar") var keluar: String,
    @ColumnInfo(name = "isPaid") var isPaid: Boolean
)