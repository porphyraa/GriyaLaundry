package com.example.griyalaundry

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.griyalaundry.database.Griya
import com.example.griyalaundry.database.GriyaDao
import com.example.griyalaundry.database.GriyaDatabase
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.list_item.view.*
import kotlin.collections.List

class GriyaAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var database : GriyaDao
    lateinit var context: Context

    var compositeDisposable = CompositeDisposable()

    var griyas: List<Griya> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = griyas.size

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        compositeDisposable.clear()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val griya = griyas[position]
        val layanan = if (griya.isRegular) "REGULER" else "EKSPRES"
        val harga = if (griya.isRegular) griya.berat * 5000 else griya.berat * 10000
        val status = if (griya.isPaid) "LUNAS" else "BELUM BAYAR"
        val bg = if (griya.isPaid) R.drawable.ic_list_paid else R.drawable.ic_list_unpaid
        val vis = if (griya.isPaid) View.INVISIBLE else View.VISIBLE
        val col = if (griya.isPaid) "#7FC1E5" else "#E57F98"

        holder.itemView.textIDListItem.text = "ID ${griya.tId}"
        holder.itemView.textNamaListItem.text = griya.nama.toUpperCase()
        holder.itemView.textBeratListItem.text = "${griya.berat} KG (${layanan})"
        holder.itemView.textHargaListItem.text = "IDR ${harga.toInt()}"
        holder.itemView.textMasukListItem.text = "${griya.masuk}"
        holder.itemView.textKeluarListItem.text = "${griya.keluar}"
        holder.itemView.textStatusListItem.text = status
        holder.itemView.btnKonfirmasi.visibility = vis
        holder.itemView.imgBgListItem.setImageResource(bg)
        holder.itemView.textIn.setTextColor(Color.parseColor(col))
        holder.itemView.textOut.setTextColor(Color.parseColor(col))

        holder.itemView.btnKonfirmasi.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Konfirmasi pembayaran?")
                .setCancelable(false)
                .setPositiveButton("Ya"){ dialogInterface: DialogInterface, i: Int ->
                    database.updateIsPaid(griya.nama, griya.berat, griya.isRegular)

                    griya.isPaid = true
                    val nama = griya.nama
                    val berat = griya.berat
                    val regular = griya.isRegular

                    Log.d("abc", "cek konfirmasi $nama, $berat, $regular")
                    notifyDataSetChanged()
                }
                .setNegativeButton("Tidak"){ dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        database = GriyaDatabase.getInstance(context = parent.context).griyaDao()
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        )
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}