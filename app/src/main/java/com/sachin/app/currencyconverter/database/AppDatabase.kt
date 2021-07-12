package com.sachin.app.currencyconverter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sachin.app.currencyconverter.network.Rate


@Database(entities = [Country::class, Rate::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun countriesDao(): CountriesDao

    companion object {

        @Volatile
        private var ourInstance: AppDatabase? = null

        operator fun invoke(context: Context): AppDatabase = ourInstance ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database.db"
            )/*.setJournalMode(JournalMode.TRUNCATE)*/
                .createFromAsset("database/database.db")
                /*.addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        CoroutineScope(Dispatchers.IO).launch{
                            val c = Gson().fromJson(
                                context.assets.readAssetsFile("countries.json"),
                                Countries::class.java
                            ).toTypedArray()

                            AppDatabase(context).countriesDao().insertAll(*c)
                        }
                    }
                })*/
                .build().also {
                    ourInstance = it
                }
        }
    }
}
/*

private fun AssetManager.readAssetsFile(fileName: String): String =
    open(fileName).bufferedReader().use { it.readText() }
*/
