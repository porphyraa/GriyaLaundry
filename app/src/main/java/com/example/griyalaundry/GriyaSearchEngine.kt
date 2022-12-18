package com.example.griyalaundry

import android.content.Context
import android.util.Log
import com.example.griyalaundry.database.Griya
import com.example.griyalaundry.database.GriyaDatabase
import kotlin.collections.List

class GriyaSearchEngine(private val context: Context) {

    fun search(query: String, i: Int): List<Griya>? {
        Thread.sleep(2000)
        Log.d("Searching", "Searching for $query")

        Log.d("abc", i.toString())
        when (i) {
            0 -> return GriyaDatabase.getInstance(context).griyaDao().findAll()
            1 -> return GriyaDatabase.getInstance(context).griyaDao().findByName("%$query%")
            2 -> return GriyaDatabase.getInstance(context).griyaDao().findByID("%$query%")
            3 -> return GriyaDatabase.getInstance(context).griyaDao().findByDate("%$query%")
            4 -> return GriyaDatabase.getInstance(context).griyaDao().findByIsPaid(1)
            5 -> return GriyaDatabase.getInstance(context).griyaDao().findByIsPaid(0)
            else -> return GriyaDatabase.getInstance(context).griyaDao().findByName("")
        }
    }
}
