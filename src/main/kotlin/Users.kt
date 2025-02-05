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
data class User(
    val idUser: Int,
    val nom: String,
    val prenom: String,
    val sexe: String,
    val email: String,
    val hashedPass: String
)