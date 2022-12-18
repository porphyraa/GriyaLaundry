package com.example.griyalaundry

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.example.griyalaundry.database.Griya
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_list.*
import java.util.concurrent.TimeUnit
import kotlin.collections.List

class List : BaseSearchActivity() {

    private lateinit var disposable: Disposable

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

    private fun setSpinner() {
        val editFilter = findViewById<EditText>(R.id.editFilter)
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
                        editFilter.visibility = View.VISIBLE
                        editFilter.inputType = InputType.TYPE_CLASS_DATETIME
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