package com.example.griyalaundry

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.griyalaundry.database.Griya
import com.example.griyalaundry.database.GriyaDatabase
import com.example.griyalaundry.database.GriyaUtil
import io.reactivex.Observable
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
        findViewById<Button>(R.id.btnSimpan).setOnClickListener(this)
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
            R.id.btnSimpan -> {
                val rvTransaksi = findViewById<RecyclerView>(R.id.rvTambah)
                val viewModel = ViewModelProvider(this).get(TambahViewModel::class.java)

                val editNama = findViewById<EditText>(R.id.namaTextBox).text.toString()

                if (editNama.isNullOrEmpty()) {
                    Toast.makeText(this, "Nama tidak  boleh kosong", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Tambah transaksi?")
                        .setCancelable(false)
                        .setPositiveButton("Ya"){ dialogInterface: DialogInterface, i: Int ->
                            val list = viewModel.getList()
                            val database = GriyaDatabase.getInstance(context = this).griyaDao()
                            val griyaList = arrayListOf<Griya>()
                            for (i in 0 until (list.value!!.size)) {
                                val holder = rvTransaksi.getChildAt(i)

                                val id = database.getId()
                                val nama = findViewById<EditText>(R.id.namaTextBox).text.toString()
                                val berat = list.value!![i].berat
                                val regular = list.value!![i].isReguler
                                val masuk = list.value!![i].tglMasuk
                                val keluar =
                                    holder.findViewById<TextView>(R.id.textTglKeluar).text.toString()
                                val ispaid = false

                                Toast.makeText(this, "Transaksi berhasil ditambahkan", Toast.LENGTH_SHORT)
                                    .show()

                                griyaList.add(
                                    Griya(
                                        tId = id.toString(),
                                        nama = nama,
                                        berat = berat,
                                        isRegular = regular,
                                        masuk = masuk,
                                        keluar = keluar,
                                        isPaid = ispaid
                                    )
                                )
                            }
                            database.insertAll(griyaList)
                        }
                        .setNegativeButton("Tidak"){ dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
                }
            }
        }
    }
}