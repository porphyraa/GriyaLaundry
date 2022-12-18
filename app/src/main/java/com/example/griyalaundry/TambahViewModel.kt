package com.example.griyalaundry

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class TambahViewModel : ViewModel() {
    private val listItem = MutableLiveData<MutableList<TambahModel>>()
    val listState = MutableLiveData<Long>()

    init {
        listItem.value = mutableListOf()
        listState.value = 0L

        val dd = LocalDate.now().dayOfMonth
        val mm = LocalDate.now().monthValue
        val yy = LocalDate.now().year

        listItem.value!!.add(TambahModel(0.0f, true, "$dd/$mm/$yy"))
    }

    fun getList() = listItem

    fun addModel(h: TambahModel) {
        listItem.value!!.add(h)
        listState.value = System.currentTimeMillis()
    }

    fun updateModel(i: Int, berat: Float, isReguler: Boolean) {
        listItem.value!![i].berat = berat
        listItem.value!![i].isReguler = isReguler
    }
}

