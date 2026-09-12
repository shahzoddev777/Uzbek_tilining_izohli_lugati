package shahzod.projects.ozbektiliningizohliugati.database.dao

import androidx.room.Dao
import androidx.room.Query
import shahzod.projects.ozbektiliningizohliugati.database.entity.Entity

@Dao
interface DictionaryDao {

    @Query("SELECT * FROM dictionary")
    suspend  fun getAllWords(): List<Entity>

    @Query("SELECT * FROM dictionary WHERE word LIKE '%' || :query || '%'")
    suspend  fun searchWords(query: String): List<Entity>

    @Query("SELECT * FROM dictionary ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(): Entity?
}