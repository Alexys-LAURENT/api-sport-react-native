package Services

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable
import sport.models.Trainings
import TrainingTypes
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.LocalDateTime

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
    suspend fun getAllTrainingsByUserId(userId: Int): AllTrainingsResponse = transaction {
        // Récupérer tous les trainings de l'utilisateur avec une limite
        val trainingsData = Trainings
            .select { Trainings.idUser eq userId }
            .orderBy(Trainings.startedDate, SortOrder.DESC)
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

    suspend fun getAllTrainingsByUserId(userId: Int, limit: Int): AllTrainingsResponse = transaction {
        // Récupérer tous les trainings de l'utilisateur avec une limite
        val trainingsData = Trainings
            .select { Trainings.idUser eq userId }
            .orderBy(Trainings.startedDate, SortOrder.DESC)
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
                it[Trainings.endedDate] = Instant.now()
            }
            updatedRows > 0
        }
    }

    suspend fun createTraining(idUser: Int, idTrainingType: Int): Int = transaction {
        try {
            val insertStatement = Trainings.insert {
                it[Trainings.idUser] = idUser
                it[Trainings.idTrainingType] = idTrainingType
                it[startedDate] = Instant.now()
                it[endedDate] = null
                it[calories] = null
                it[difficulty] = null
                it[feeling] = null
                it[distance] = null
            }

            insertStatement.resultedValues?.firstOrNull()?.get(Trainings.idTraining)
                ?: throw Exception("Impossible d'insérer l'entraînement")
        } catch (e: Exception) {
            println("Détails de l'erreur : ${e.message}")
            throw Exception("Erreur lors de la création de l'entraînement : ${e.message}")
        }
    }
}