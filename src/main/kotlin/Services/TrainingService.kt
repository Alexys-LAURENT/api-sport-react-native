import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable


import sport.models.TrainingDTO
import sport.models.Trainings
import TrainingTypes

@Serializable
data class TrainingResponse(
    val idTraining: Int,
    val calories: Int?,
    val date: String,
    val idTrainingType: Int,
    val icon: String,
    val label: String
)

@Serializable
data class AllTrainingsResponse(
    val trainings: List<TrainingResponse>
)

class TrainingService {
    suspend fun getAllTrainingsByUserId(userId: Int): AllTrainingsResponse = transaction {
        // Récupérer tous les trainings de l'utilisateur
        val trainingsData = Trainings.select { Trainings.idUser eq userId }
            .map { row ->
                val idTrainingType = row[Trainings.idTrainingType]

                // Récupérer les données du type de training
                val trainingTypeData = TrainingTypes.select { TrainingType.id eq idTrainingType }
                    .singleOrNull()

                TrainingResponse(
                    idTraining = row[Trainings.idTraining],
                    calories = row[Trainings.calories],
                    date = row[Trainings.startedDate].toString(),
                    idTrainingType = idTrainingType,
                    label = trainingTypeData?.get(TrainingType.label) ?: "",
                    icon = trainingTypeData?.get(TrainingType.icon) ?: ""
                )
            }

        AllTrainingsResponse(trainings = trainingsData)
    }
}