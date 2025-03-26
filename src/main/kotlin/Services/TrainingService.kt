package Services
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable


import sport.models.TrainingDTO
import sport.models.Trainings
import TrainingTypes
import org.jetbrains.exposed.sql.update


@Serializable
data class TrainingResponse(
    val idTraining: Int,
    val calories: Int?,
    val startedDate: String,
    val endedDate: String,
    val icon: String,
    val label: String
)


@Serializable
data class AllTrainingsResponse(
    val trainings: List<TrainingResponse>
)

class TrainingService {
    suspend fun getAllTrainingsByUserId(userId: Int, limit: Int): AllTrainingsResponse = transaction {
        // Récupérer tous les trainings de l'utilisateur avec une limite
        val trainingsData = Trainings
            .select { Trainings.idUser eq userId }
            .limit(limit)
            .map { row ->
                val idTrainingType = row[Trainings.idTrainingType]

                // Récupérer les données du type de training
                val trainingTypeData = TrainingTypes.select { TrainingTypes.idTrainingType eq idTrainingType }
                    .singleOrNull()

                TrainingResponse(
                    idTraining = row[Trainings.idTraining],
                    calories = row[Trainings.calories],
                    startedDate = row[Trainings.startedDate].toString(),
                    endedDate = row[Trainings.endedDate]?.toString() ?: "",
                    label = trainingTypeData?.get(TrainingTypes.label) ?: "",
                    icon = trainingTypeData?.get(TrainingTypes.icon) ?: ""
                )
            }

        AllTrainingsResponse(trainings = trainingsData)
    }
    suspend fun updateTraining(id: Int, difficulty: String, feeling: String?): Boolean {
        return transaction {
            val updatedRows = Trainings.update({ Trainings.idTraining eq id }) {
                it[Trainings.difficulty] = difficulty
                it[Trainings.feeling] = feeling
            }
            updatedRows > 0
        }
    }
}