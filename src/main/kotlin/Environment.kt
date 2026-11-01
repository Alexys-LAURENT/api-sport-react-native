package sport

import io.github.cdimascio.dotenv.Dotenv

object Environment {
    private val dotenv by lazy { Dotenv.configure().ignoreIfMissing().load() }

    fun required(name: String): String =
        dotenv[name]?.takeIf { it.isNotBlank() }
            ?: error("Missing required environment variable: $name")
}
