package shahzod.projects.ozbektiliningizohliugati.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import shahzod.projects.ozbektiliningizohliugati.database.dao.AtamaDao
import shahzod.projects.ozbektiliningizohliugati.database.dao.DictionaryDao
import shahzod.projects.ozbektiliningizohliugati.database.entity.AtamaEntity
import shahzod.projects.ozbektiliningizohliugati.database.entity.Entity

@Database(entities = [Entity::class, AtamaEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getWordDao(): DictionaryDao
    abstract fun getAtamaDao(): AtamaDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database.db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration(true)
                    .createFromAsset("data.db")
                    .build().also { instance = it }
            }
        }

        fun getInstance()=instance
    }
}