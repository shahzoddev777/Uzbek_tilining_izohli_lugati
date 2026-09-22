package shahzod.projects.ozbektiliningizohliugati.database.dao

import androidx.room.Dao
import androidx.room.Query
import shahzod.projects.ozbektiliningizohliugati.database.entity.AtamaEntity

@Dao
interface AtamaDao {
    @Query("select * from atama")
    suspend fun getAllAtamaWords(): List<AtamaEntity>

    @Query("SELECT * FROM atama WHERE word LIKE :letter || '%' ORDER BY word ASC")
    suspend fun getAtamaWordsByLetter(letter: String): List<AtamaEntity>
}