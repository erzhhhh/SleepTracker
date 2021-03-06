package com.example.android.trackmysleepquality.sleeptracker

import android.annotation.SuppressLint
import android.app.Application
import android.text.Spanned
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SleepTrackerViewModel(val database: SleepDatabaseDao, application: Application)
    : AndroidViewModel(application) {

    private var _tonight = MutableLiveData<SleepNight?>()
    val tonight: LiveData<SleepNight?>
        get() = _tonight


    private var _nightString = MutableLiveData<Spanned>()
    val nightString: LiveData<Spanned>
        get() = _nightString

    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality

    init {
        getTonightFromDatabaseRx()
        getAllNights(application)
    }


    @SuppressLint("CheckResult")
    private fun getAllNights(application: Application) {
        database.getAllNights()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    _nightString.value = formatNights(it, application.resources)
                }
    }

    /**
     * Executes when the START button is clicked.
     */
    fun onStartTracking() {
        val newNight = SleepNight()
        Single.fromCallable { database.insertSleepNight(newNight) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { getTonightFromDatabaseRx() }
                .subscribe()
    }


    @SuppressLint("CheckResult")
    private fun getTonightFromDatabaseRx() {
        database.getTonight()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { _tonight.value = it }
                .subscribe()
    }

    /**
     * Executes when the STOP button is clicked.
     */
    fun onStopTracking() {
        val oldNight = _tonight.value
        oldNight!!.endTimeMilli = System.currentTimeMillis()
        Single.fromCallable { database.update(oldNight) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { _navigateToSleepQuality.value = oldNight }
                .subscribe()
    }

    /**
     * Executes when the CLEAR button is clicked.
     */
    fun onClear() {
        Single.fromCallable { database.clear() }
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { _tonight.value = null }
                .subscribe()
    }

    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }
}