package shahzod.projects.ozbektiliningizohliugati.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "atama", indices = [Index(value = ["word"])])
data class AtamaEntity(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val word: String,
)