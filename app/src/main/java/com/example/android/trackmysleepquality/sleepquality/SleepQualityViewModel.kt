package com.example.android.trackmysleepquality.sleepquality

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SleepQualityViewModel(private val sleepNightKey: Long = 0L, val database: SleepDatabaseDao)
    : ViewModel() {

    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()
    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker

    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

    @SuppressLint("CheckResult")
    fun onSetSleepQuality(quality: Int) {
        database.get(sleepNightKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { _navigateToSleepTracker.value = true }
                .subscribe {
                    it?.let {
                        val tonight = it.apply {
                            sleepQuality = quality
                        }
                        updateNight(it)
                    }
                }
    }

    private fun updateNight(night: SleepNight) {
        Single.fromCallable { database.update(night) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }
}