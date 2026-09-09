package shahzod.projects.ozbektiliningizohliugati.database.dao

import androidx.room.Dao
import androidx.room.Query
import shahzod.projects.ozbektiliningizohliugati.database.entity.AtamaEntity

@Dao
interface AtamaDao {
    @Query("select * from atama")
    fun getAllAtamaWords(): List<AtamaEntity>
}