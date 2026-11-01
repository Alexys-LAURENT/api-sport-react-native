> **Republication du projet.** Première activité Git conservée : **2025-02-04**. Ce projet a été republié le **2 novembre 2026**, après retrait des identifiants suivis, revue des données de démonstration et création d’un nouvel historique Git. La date de ce dépôt ne représente pas le début du projet.

# api

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need
  to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

## Features

Here's a list of features included in this project:

| Name                                                                   | Description                                                                        |
| ------------------------------------------------------------------------|------------------------------------------------------------------------------------ |
| [Routing](https://start.ktor.io/p/routing)                             | Provides a structured routing DSL                                                  |
| [Content Negotiation](https://start.ktor.io/p/content-negotiation)     | Provides automatic content conversion according to Content-Type and Accept headers |
| [kotlinx.serialization](https://start.ktor.io/p/kotlinx-serialization) | Handles JSON serialization using kotlinx.serialization library                     |
| [Static Content](https://start.ktor.io/p/static-content)               | Serves static files from defined locations                                         |

## Building & Running

To build or run the project, use one of the following tasks:

| Task                          | Description                                                          |
| -------------------------------|---------------------------------------------------------------------- |
| `./gradlew test`              | Run the tests                                                        |
| `./gradlew build`             | Build everything                                                     |
| `buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `buildImage`                  | Build the docker image to use with the fat JAR                       |
| `publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `run`                         | Run the server                                                       |
| `runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```


## Local configuration

Copy `.env.example` (or `.env.exemple`) to `.env` in the project root. Set `DB_USER`, `DB_PASSWORD`, and a unique, random `JWT_SECRET` before running the API from this directory. `DB_URL` must point to your PostgreSQL database. The application reads `.env` with dotenv-java; actual environment variables take precedence, so a deployed server can use environment variables without a file. Missing or blank required values produce a configuration error.

The same `JWT_SECRET` is used for token signing and verification. Replacing it invalidates existing tokens. Generate a secret locally, for example with PowerShell: `[Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(48))` (PowerShell 7). Never commit `.env`; the committed templates intentionally contain no passwords or signing keys.

## Historique du projet et contributions

État de l’ancien dépôt relevé le 2 novembre 2026 : **38 commits** sur la branche locale de référence, **0 étoile(s)** et **0 fork(s)**. Ces compteurs décrivent l’ancien dépôt, pas le nouvel historique.

Contributeurs GitHub de l’ancien dépôt : [Alexys-LAURENT](https://github.com/Alexys-LAURENT), [Louis-2204](https://github.com/Louis-2204), [neal92](https://github.com/neal92), [Plazcom](https://github.com/Plazcom), [bania78](https://github.com/bania78). Leur travail est conservé dans le code republié ; le nouvel historique ne reproduit pas leurs anciens commits.
