package sport.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val idUser = integer("id_user").autoIncrement()
    val nom = varchar("nom", 255)
    val prenom = varchar("prenom", 255)
    val sexe = varchar("sexe", 10)
    val email = varchar("email", 255).uniqueIndex()
    val hashedPass = varchar("hashed_pass", 255)

    override val primaryKey = PrimaryKey(idUser)
}

@Serializable
data class UsersDTO(
    val idUser: Int,
    val nom: String,
    val prenom: String,
    val sexe: String,
    val email: String,
    val hashedPass: String
)

@Serializable
data class SignInDTO(
    val nom: String,
    val prenom: String,
    val sexe: String,
    val email: String,
    val hashedPass: String
)

@Serializable
data class UsersLoginDTO(
    val idUser: Int = 0,
    val email: String,
    val hashedPass: String
)

@Serializable
data class LoginDTO(
    val email: String,
    val hashedPass: String
)

@Serializable
data class LoginResponseDTO(
    val message: String,
    val email: String,
    val token: String,
    val idUser: Int,
    val firstName: String,
    val lastName: String,
    val gender: String
)

@Serializable
data class SignInResponseDTO(
    val message: String,
    val token: String
)


