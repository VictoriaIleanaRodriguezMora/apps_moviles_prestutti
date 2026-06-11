package com.prestutti.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.prestutti.data.local.entity.LoanEntity

@Database(entities = [LoanEntity::class], version = 1, exportSchema = false)
abstract class PrestuttiDatabase : RoomDatabase() {
    abstract fun loanDao(): LoanDao

    companion object {
        const val DATABASE_NAME = "prestutti_db"
    }
}
