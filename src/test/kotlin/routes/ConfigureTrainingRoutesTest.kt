package routes

import io.ktor.client.request.*
import io.ktor.server.testing.*
import kotlin.test.Test

class ConfigureTrainingRoutesTest {

    @Test
    fun testGetApiTrainings() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.get("/api/trainings").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostApiTrainings() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.post("/api/trainings").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testDeleteApiTrainingsId() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.delete("/api/trainings/{id}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetApiTrainingsId() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.get("/api/trainings/{id}").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPutApiTrainingsId() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.put("/api/trainings/{id}").apply {
            TODO("Please write your test here")
        }
    }
}