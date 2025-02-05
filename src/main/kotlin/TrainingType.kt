import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object TrainingTypes : Table("training_types") {
    val idTrainingType = integer("id_training_type").autoIncrement()
    val icon = varchar("icon", 255)
    val label = varchar("label", 255)

    override val primaryKey = PrimaryKey(idTrainingType)
}

@Serializable
data class TrainingType(
    val idTrainingType: Int,
    val icon: String,
    val label: String
)