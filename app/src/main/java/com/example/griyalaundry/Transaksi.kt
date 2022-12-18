package com.example.griyalaundry

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class Transaksi : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaksi)

        var rvTransaksi = findViewById<RecyclerView>(R.id.rvTambah)
        val viewModel = ViewModelProvider(this).get(TambahViewModel::class.java)

        val tView = findViewById<TextView>(R.id.textTotalHarga)
        viewModel.listState.observe(this, Observer {
            rvTransaksi.adapter = TambahAdapter(viewModel.getList().value!!, viewModel, tView)
            rvTransaksi.layoutManager = LinearLayoutManager(this)
        })

        findViewById<Button>(R.id.btnList_transaksi).setOnClickListener(this)
        findViewById<Button>(R.id.btnTambah).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnList_transaksi -> {
                intent = Intent(this, List::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
            R.id.btnTambah -> {
                val dd = LocalDate.now().dayOfMonth
                val mm = LocalDate.now().monthValue
                val yy = LocalDate.now().year

                val item = TambahModel(0.0f, true, "$dd/$mm/$yy")
                val viewModel = ViewModelProvider(this).get(TambahViewModel::class.java)
                viewModel.addModel(item)
            }
        }
    }
}