package com.example.android.trackmysleepquality.database

import androidx.room.*
import io.reactivex.Maybe
import io.reactivex.Observable

@Dao
interface SleepDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSleepNight(night: SleepNight)

    @Update
    fun update(night: SleepNight)

    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key")
    fun get(key: Long): Maybe<SleepNight?>

    @Query("DELETE FROM daily_sleep_quality_table")
    fun clear()


    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNights(): Observable<List<SleepNight>>

    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    fun getTonight(): Maybe<SleepNight?>
}

