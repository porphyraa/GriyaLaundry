package com.example.griyalaundry

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.griyalaundry.database.Griya
import com.example.griyalaundry.database.GriyaDatabase
import com.example.griyalaundry.database.GriyaUtil
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_list.*
import kotlin.collections.List

abstract class BaseSearchActivity : AppCompatActivity(), View.OnClickListener {

    protected lateinit var griyaSearchEngine: GriyaSearchEngine
    private val griyaAdapter = GriyaAdapter()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        rvList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        rvList.adapter = griyaAdapter

        griyaSearchEngine = GriyaSearchEngine(this@BaseSearchActivity)

        val initialLoadDisposable = loadInitialData(this@BaseSearchActivity).subscribe()
        compositeDisposable.add(initialLoadDisposable)


    }

    override fun onDestroy() {
        super.onDestroy()
        GriyaDatabase.destroyInstance()
        compositeDisposable.clear()
    }

    protected fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    protected fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    protected fun showResult(result: List<Griya>) {
        if (result.isEmpty()) {
            Toast.makeText(this, R.string.nothing, Toast.LENGTH_SHORT).show()
        }
        griyaAdapter.griyas = result
    }

    private fun loadInitialData(context: Context): Flowable<List<Long>> {
        return Maybe.fromAction<List<Long>> {

            val database = GriyaDatabase.getInstance(context = context).griyaDao()

            val griyaList = arrayListOf<Griya>()

            for (i in 0..GriyaUtil.nama.size - 1) {
                val t = GriyaUtil.tId[i]
                val n = GriyaUtil.nama[i]
                val b = GriyaUtil.berat[i].toString().toFloat()
                val iR = GriyaUtil.isRegular[i]
                val m = GriyaUtil.masuk[i]
                val k = GriyaUtil.keluar[i]
                val iP = GriyaUtil.isPaid[i]
                griyaList.add(
                    Griya(
                        tId = t, nama = n, berat = b, isRegular = iR, masuk = m, keluar = k, isPaid = iP
                    )
                )
            }

            Log.d("abc", "gada data bos makanya eorr")
            database.insertAll(griyaList)
            Log.d("abc", "gada data bos makanya eorr 2")

        }.toFlowable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
//                Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_LONG)
//                    .show()
            }
            .doOnError {
                Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_LONG).show()
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnTransaksi_list -> {
                intent = Intent(this, Transaksi::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }
    }


}