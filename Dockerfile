# Dockerfile

# --- Étape 1: Build de l'application ---
# Utiliser une image JDK pour la compilation avec Maven
FROM eclipse-temurin:24-jdk-jammy AS builder

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier le wrapper Maven et le pom.xml pour télécharger les dépendances en premier (optimisation du cache Docker)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Télécharger les dépendances (si le pom.xml ou mvnw n'a pas changé, cette couche sera mise en cache)
RUN ./mvnw dependency:go-offline -B

# Copier le reste du code source de l'application
COPY src ./src

# Compiler l'application et la packager en JAR, en sautant les tests (ils devraient être exécutés dans le pipeline CI)
RUN ./mvnw package -DskipTests -B

# --- Étape 2: Création de l'image d'exécution ---
# Utiliser une image JRE plus légère pour l'exécution
FROM eclipse-temurin:24-jre-jammy

# Définir le répertoire de travail
WORKDIR /app

# Créer un utilisateur et un groupe non root pour l'application
ARG APP_USER=fooapiuser
ARG APP_GROUP=fooapigroup
RUN groupadd -r ${APP_GROUP} && useradd -r -g ${APP_GROUP} ${APP_USER}

# Copier le JAR construit depuis l'étape 'builder'
COPY --from=builder /app/target/*.jar app.jar

# Donner la propriété du répertoire de l'application à l'utilisateur non root
RUN chown -R ${APP_USER}:${APP_GROUP} /app

# Changer d'utilisateur pour l'exécution
USER ${APP_USER}

# Exposer le port sur lequel l'application Spring Boot s'exécute (défini dans application.properties)
EXPOSE 8080

# Commande pour lancer l'application
# Les options Java peuvent être ajoutées ici (ex: -Xmx, -Xms)
# Le profil Spring peut être activé ici si nécessaire, ou via des variables d'environnement Docker
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]