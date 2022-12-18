package com.example.griyalaundry.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GriyaDao {

    @Query("SELECT * FROM griyas")
    fun findAll(): List<Griya>

    @Query("SELECT * FROM griyas WHERE tId LIKE :id")
    fun findByID(id: String): List<Griya>

    @Query("SELECT * FROM griyas WHERE nama LIKE :nama")
    fun findByName(nama: String): List<Griya>

    @Query("SELECT * FROM griyas WHERE masuk LIKE :date OR keluar LIKE :date")
    fun findByDate(date: String): List<Griya>

    @Query("SELECT * FROM griyas WHERE isPaid=:i")
    fun findByIsPaid(i: Int): List<Griya>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(griya: List<Griya>): List<Long>
}