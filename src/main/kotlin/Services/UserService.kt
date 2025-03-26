package Services

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import sport.models.*
import at.favre.lib.crypto.bcrypt.BCrypt


class UserService {
    // Créer un utilisateur
    suspend fun create(dto: SignInDTO): UsersDTO = transaction {
        println(dto)
        val hashedPassword = BCrypt.withDefaults().hashToString(12, dto.hashedPass.toCharArray())
        val insertStatement = Users.insert {
            it[nom] = dto.nom
            it[prenom] = dto.prenom
            it[sexe] = dto.sexe
            it[email] = dto.email
            it[hashedPass] = hashedPassword
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

    suspend fun login(email: String, password: String): LoginDTO? = transaction {
        val user = Users
            .select { Users.email eq email }
            .map { row ->
                UsersLoginDTO(
                    email = row[Users.email],
                    hashedPass = row[Users.hashedPass],
                )
            }
            .singleOrNull() ?: return@transaction null
        val passwordVerified = BCrypt.verifyer().verify(password.toCharArray(), user.hashedPass)
        if (passwordVerified.verified) {
            LoginDTO(user.email, user.hashedPass) // Retourne un DTO si l'authentification est valide
        } else {
            null // Retourne null si l'utilisateur n'existe pas ou si le mot de passe est incorrect
        }
    }


}
