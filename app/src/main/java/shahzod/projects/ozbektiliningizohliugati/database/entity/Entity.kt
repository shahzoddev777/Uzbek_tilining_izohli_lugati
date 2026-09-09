package shahzod.projects.ozbektiliningizohliugati.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dictionary")
data class Entity(
    @PrimaryKey(autoGenerate = true)
    val _id: Int?,
    val word: String?,
    val description: String?,
    val history: Int?,
    val likess: Int?,
    val statess: Int?
)
