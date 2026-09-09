package shahzod.projects.ozbektiliningizohliugati.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "atama")
data class AtamaEntity(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val word: String,
)
