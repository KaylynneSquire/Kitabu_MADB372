package com.example.kitabu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BookEntity::class,
        BookingEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class KitabuDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    abstract fun bookingDao(): BookingDao

    companion object {

        @Volatile
        private var INSTANCE: KitabuDatabase? = null

        fun getDatabase(context: Context): KitabuDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KitabuDatabase::class.java,
                    "kitabu_database"
                )
                    .addCallback(
                        object : RoomDatabase.Callback() {

                            override fun onCreate(
                                db: androidx.sqlite.db.SupportSQLiteDatabase
                            ) {
                                super.onCreate(db)

                                CoroutineScope(Dispatchers.IO).launch {
                                    getDatabase(context).bookDao().insertBooks(
                                        listOf(
                                            BookEntity(
                                                title = "Kotlin Programming",
                                                author = "John Smith",
                                                category = "Technology"
                                            ),
                                            BookEntity(
                                                title = "Android Development",
                                                author = "Sarah Williams",
                                                category = "Mobile Development"
                                            ),
                                            BookEntity(
                                                title = "Database Systems",
                                                author = "Jane Brown",
                                                category = "Computer Science"
                                            ),
                                            BookEntity(
                                                title = "Web Development",
                                                author = "Michael Jones",
                                                category = "Web Design"
                                            ),
                                            BookEntity(
                                                title = "Software Engineering",
                                                author = "David Wilson",
                                                category = "Software"
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    )
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}
