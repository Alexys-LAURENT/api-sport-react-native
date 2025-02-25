package Services

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import sport.models.Users
import sport.models.UsersDTO
import sport.models.UsersLoginDTO


class UserService {
    // Créer un utilisateur
    suspend fun create(dto: UsersDTO): UsersDTO = transaction {
        val insertStatement = Users.insert {
            it[idUser] = dto.idUser
            it[nom] = dto.nom
            it[prenom] = dto.prenom
            it[sexe] = dto.sexe
            it[email] = dto.email
            it[hashedPass] = dto.hashedPass
        }

        val row = insertStatement.resultedValues?.firstOrNull()
            ?: throw Exception("Échec de la création de l'utilisateur")

        UsersDTO(
            idUser = row[Users.idUser],
            nom = row[Users.nom],
            prenom = row[Users.prenom],
            sexe = row[Users.sexe],
            email = row[Users.email],
            hashedPass = row[Users.hashedPass],



            )
    }

    // Récupérer un utilisateur par ID
    suspend fun getById(id: Int): UsersLoginDTO? = transaction {
        Users
            .slice(Users.idUser, Users.hashedPass) // Sélectionne uniquement les colonnes nécessaires
            .select { Users.idUser eq id }
            .map { row ->
                UsersLoginDTO(
                    idUser = row[Users.idUser],
                    email = row[Users.email],
                    hashedPass = row[Users.hashedPass]
                )
            }
            .singleOrNull()
    }


    // Mettre à jour un utilisateur
    suspend fun update(id: Int, dto: UsersDTO): Boolean = transaction {
        Users.update({ Users.idUser eq id }) {
            it[nom] = dto.nom
            it [prenom] = dto.prenom
            it[email] = dto.email
            it [hashedPass] = dto.hashedPass
        } > 0
    }

    // Supprimer un utilisateur
    suspend fun delete(id: Int): Boolean = transaction {
        Users.deleteWhere { Users.idUser eq id } > 0
    }
}
