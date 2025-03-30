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
    suspend fun getById(id: Int): UsersDTO? = transaction {
        Users
            .select { Users.idUser eq id }
            .map { row ->
                UsersDTO(
                    idUser = row[Users.idUser],
                    nom = row[Users.nom],
                    prenom = row[Users.prenom],
                    sexe = row[Users.sexe],
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
            it [email] = dto.email
            it [hashedPass] = dto.hashedPass
            it [sexe] = dto.sexe
        } > 0
    }

    // Supprimer un utilisateur
    suspend fun delete(id: Int): Boolean = transaction {
        Users.deleteWhere { Users.idUser eq id } > 0
    }

    suspend fun login(email: String, password: String): LoginResponseDTO? = transaction {
        val userQuery = Users
            .select { Users.email eq email }
            .map { row ->
                // Store the row data in a variable before password verification
                val userData = LoginResponseDTO(
                    message = "Connexion réussie",
                    email = row[Users.email],
                    token = "", // This will be generated in the route
                    idUser = row[Users.idUser],
                    firstName = row[Users.prenom],
                    lastName = row[Users.nom],
                    gender = row[Users.sexe]
                )

                // Verify password
                val passwordVerified = BCrypt.verifyer().verify(
                    password.toCharArray(),
                    row[Users.hashedPass]
                )

                // Return userData only if password is verified
                if (passwordVerified.verified) userData else null
            }
            .singleOrNull()

        userQuery
    }


}
