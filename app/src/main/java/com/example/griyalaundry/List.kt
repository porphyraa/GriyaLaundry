package com.example.griyalaundry

import android.app.DatePickerDialog
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.example.griyalaundry.database.Griya
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_list.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.List

class List : BaseSearchActivity() {

    private lateinit var disposable: Disposable

    var cal = Calendar.getInstance()
    private var filterIndex: Int = 0

    override fun onStart() {
        super.onStart()

        setSpinner()
        findViewById<Button>(R.id.btnTransaksi_list).setOnClickListener(this)

        val textChangeStream =
            createTextChangeObservable().toFlowable(BackpressureStrategy.BUFFER)

//        val searchTextFlowable = Flowable.merge<String>(filterChangeStream, textChangeStream)
        val searchTextFlowable = textChangeStream

        disposable = searchTextFlowable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showProgress() }
            .observeOn(Schedulers.io())
            .map { griyaSearchEngine.search(it, filterIndex) as List<Griya> }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                hideProgress()
                showResult(it)
            }

    }

    @Override
    override fun onStop() {
        super.onStop()
        // 1
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    private fun createTextChangeObservable(): Observable<String> {

        val textChangeObservable = Observable.create<String> { emitter ->
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    s?.toString()?.let { emitter.onNext(it) }
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    s?.toString()?.let { emitter.onNext(it) }
                }
            }

            editFilter.addTextChangedListener(textWatcher)

            emitter.setCancellable {
                editFilter.removeTextChangedListener(textWatcher)
            }
        }

        return textChangeObservable
            .filter { it.length >= 1 }
            .debounce(1000, TimeUnit.MILLISECONDS)
    }

    val dateSetListener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
            val editFilter = findViewById<EditText>(R.id.editFilter)
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd/MM/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            editFilter.setText(sdf.format(cal.getTime()))
        }
    }

    private fun setSpinner() {
        val editFilter = findViewById<EditText>(R.id.editFilter)
        val editDateFilter = findViewById<EditText>(R.id.editDateFilter)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter.createFromResource(
            this, R.array.filter, R.layout.filter_list
        )
        adapter.setDropDownViewResource(R.layout.filter_list);
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                editFilter.setText("")
                filterIndex = pos
                if (filterIndex == 3)
                    editDateFilter.visibility = View.VISIBLE
                else
                    editDateFilter.visibility = View.INVISIBLE

                Log.d("abc", "${spinner.selectedItem.toString()} $pos $filterIndex")

                when (filterIndex) {
                    0 -> {
                        editFilter.visibility = View.INVISIBLE
                        editFilter.setText("fliter index 0")
                    }
                    1 -> {
                        editFilter.visibility = View.VISIBLE
                        editFilter.inputType = InputType.TYPE_CLASS_TEXT
                    }
                    2 -> {
                        editFilter.visibility = View.VISIBLE
                        editFilter.inputType = InputType.TYPE_CLASS_NUMBER
                    }
                    3 -> {
                        editFilter.visibility = View.INVISIBLE
                        editFilter.inputType = InputType.TYPE_CLASS_DATETIME

                        editDateFilter.setOnTouchListener(View.OnTouchListener { v, event ->
                            if (MotionEvent.ACTION_UP == event.action) {
                                DatePickerDialog(
                                    this@List,
                                    dateSetListener,
                                    // set DatePickerDialog to point to today's date when it loads up
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                            true // return is important...
                        })
                    }
                    4 -> {
                        editFilter.visibility = View.INVISIBLE
                        editFilter.setText("fliter index 4")
                    }
                    5 -> {
                        editFilter.visibility = View.INVISIBLE
                        editFilter.setText("fliter index 5")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
}