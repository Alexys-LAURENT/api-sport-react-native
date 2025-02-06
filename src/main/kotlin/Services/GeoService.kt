package Services
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import sport.models.Geo
import sport.models.GeoDTO
import java.time.Instant


class GeoService {
    // Créer un point géographique
    suspend fun create(dto: GeoDTO): GeoDTO = transaction {
        val insertStatement = Geo.insert {
            it[idTraining] = dto.idTraining
            it[date] = Instant.parse(dto.date)
            it[localization] = dto.localization
        }

        val row = insertStatement.resultedValues?.firstOrNull()
            ?: throw Exception("Échec de la création du point géographique")

        GeoDTO(
            idGeo = row[Geo.idGeo],
            idTraining = row[Geo.idTraining],
            date = row[Geo.date].toString(),
            localization = row[Geo.localization]
        )
    }

    // Récupérer un point géographique par ID
    suspend fun getById(id: Int): GeoDTO? = transaction {
        Geo.select { Geo.idGeo eq id }
            .map { row ->
                GeoDTO(
                    idGeo = row[Geo.idGeo],
                    idTraining = row[Geo.idTraining],
                    date = row[Geo.date].toString(),
                    localization = row[Geo.localization]
                )
            }
            .singleOrNull()
    }

    // Récupérer tous les points d'un entraînement
    suspend fun getByTrainingId(trainingId: Int): List<GeoDTO> = transaction {
        Geo.select { Geo.idTraining eq trainingId }
            .map { row ->
                GeoDTO(
                    idGeo = row[Geo.idGeo],
                    idTraining = row[Geo.idTraining],
                    date = row[Geo.date].toString(),
                    localization = row[Geo.localization]
                )
            }
    }

    // Mettre à jour un point géographique
    suspend fun update(id: Int, dto: GeoDTO): Boolean = transaction {
        Geo.update({ Geo.idGeo eq id }) {
            it[idTraining] = dto.idTraining
            it[date] = Instant.parse(dto.date)
            it[localization] = dto.localization
        } > 0
    }

    // Supprimer un point géographique
    suspend fun delete(id: Int): Boolean = transaction {
        Geo.deleteWhere { Geo.idGeo eq id } > 0
    }
}