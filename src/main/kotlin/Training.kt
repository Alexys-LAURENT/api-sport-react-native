import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Trainings : Table("trainings") {
    val idTraining = integer("id_training").autoIncrement()
    val idUser = integer("id_user").references(Users.idUser)
    val idTrainingType = integer("id_training_type").references(TrainingTypes.idTrainingType)
    val startedDate = timestamp("started_date")
    val endedDate = timestamp("ended_date").nullable()
    val calories = integer("calories").nullable()
    val difficulty = varchar("difficulty", 20).check {
        it inList listOf("très facile", "facile", "modéré", "difficile", "très difficile")
    }
    val feeling = text("feeling").nullable()
    val distance = float("distance").nullable()

    override val primaryKey = PrimaryKey(idTraining)
}

@Serializable
data class Training(
    val idTraining: Int,
    val idUser: Int,
    val idTrainingType: Int,
    val startedDate: String,
    val endedDate: String,
    val calories: Int?,
    val difficulty: String?,
    val feeling: String?,
    val distance: Float?
)