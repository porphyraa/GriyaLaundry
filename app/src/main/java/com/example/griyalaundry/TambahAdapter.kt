package com.example.griyalaundry

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TambahAdapter(var item: MutableList<TambahModel>, val vModel: TambahViewModel, val tView: TextView) :
    RecyclerView.Adapter<TambahAdapter.TambahViewHolder>() {
    inner class TambahViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        var totalHarga = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TambahViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tambah_item, parent, false)
        return TambahViewHolder(view)
    }

    override fun onBindViewHolder(holder: TambahViewHolder, pos: Int) {
        holder.itemView.apply {
            val harga = hitungHarga(holder.itemView, pos)

            findViewById<EditText>(R.id.editBerat).setText(item[pos].berat.toString())
            findViewById<TextView>(R.id.textHarga).text = "IDR $harga"
            findViewById<RadioButton>(R.id.radio1).isChecked = item[pos].isReguler
            findViewById<RadioButton>(R.id.radio2).isChecked = !item[pos].isReguler
            findViewById<TextView>(R.id.textTglMasuk).text = item[pos].tglMasuk
            findViewById<TextView>(R.id.textTglKeluar).text = hitungTglKeluar(holder.itemView, pos)

            Log.d("abc", totalHarga.toString())

            findViewById<EditText>(R.id.editBerat).addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    if (!findViewById<EditText>(R.id.editBerat).text.isNullOrEmpty()) {
                        val harga = hitungHarga(holder.itemView, pos)

                        findViewById<TextView>(R.id.textHarga).text = "IDR $harga"

                        hitungTotalHarga(holder.itemView, tView)

                        Handler().postDelayed({
                            vModel.updateModel(pos, item[pos].berat, item[pos].isReguler)
                        }, 1000)
                    }
                }

                override fun beforeTextChanged(s: CharSequence, st: Int, co: Int, af: Int) {
                }

                override fun onTextChanged(s: CharSequence, st: Int, be: Int, co: Int) {
                }
            })

            findViewById<RadioGroup>(R.id.radioLayanan).setOnCheckedChangeListener { group, checkedId ->
                val harga = hitungHarga(holder.itemView, pos)
                val tglKeluar = hitungTglKeluar(holder.itemView, pos)
                findViewById<TextView>(R.id.textHarga).text = "IDR $harga"

                findViewById<TextView>(R.id.textTglKeluar).text = tglKeluar

                hitungTotalHarga(holder.itemView, tView)

                Handler().postDelayed({
                    vModel.updateModel(pos, item[pos].berat, item[pos].isReguler)
                }, 1000)
            }
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

    fun hitungTotalHarga(view: View, tView: TextView) {
        totalHarga = 0
        for (i in 0..itemCount-1)
            totalHarga += hitungHarga(view, i)

        tView.text = "IDR $totalHarga"
        Log.d("abc", totalHarga.toString())
    }

    private fun hitungHarga(itemView: View, i: Int): Int {
        var berat = 0F
        var harga = 0
        val checked = itemView.findViewById<RadioGroup>(R.id.radioLayanan).checkedRadioButtonId
        val isReguler = (checked == R.id.radio1)

        if (!itemView.findViewById<EditText>(R.id.editBerat).text.isNullOrEmpty()) {
            berat = itemView.findViewById<EditText>(R.id.editBerat).text.toString().toFloat()
            item[i].berat = berat
        }

        if (isReguler) {
            harga = (berat * 5000).toInt()
        } else {
            harga = (berat * 10000).toInt()
        }

        return harga
    }

    private fun hitungTglKeluar(itemView: View, i: Int): String {
        var d = 0
        var berat = 0F
        val checked = itemView.findViewById<RadioGroup>(R.id.radioLayanan).checkedRadioButtonId
        val isReguler = (checked == R.id.radio1)

        if (!itemView.findViewById<EditText>(R.id.editBerat).text.isNullOrEmpty()) {
            berat = itemView.findViewById<EditText>(R.id.editBerat).text.toString().toFloat()
            item[i].berat = berat
            item[i].isReguler = isReguler
        }

        if (isReguler) {
            if (berat < 2.5f)
                d += 2
            else if (berat < 4f)
                d += 3
            else
                d += 4
        } else {
            d++
        }

        val tgl = item[i].tglMasuk.split("/").toTypedArray()
        var dd = tgl[0].toInt()
        val mm = tgl[1]
        val yy = tgl[2]
        dd += d

        return "$dd/$mm/$yy"
    }
}
