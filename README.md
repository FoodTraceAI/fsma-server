# fsma-server
Test code for FSMA Server

# To compile: ./gradlew clean build -x test  
# -x test does not run the unit tests

# to run inside a docker container edit application.yml
# for running in IntelliJ or with docker compose
# Make sure to build after making a change with:  ./gradlew clean build
datasource:
    url: jdbc:postgresql://localhost:5432/postgres
# for running in a separate docker container on same computer
datasource:
    url: jdbc:postgresql://host.docker.internal:5432/postgres

# to create docker image

# to start postgres in a docker container
docker run --name fsma-pg -e POSTGRES_PASSWORD=pw -p 5432:5432 -d postgres:16.2

# to run inside docker container accessing postgres in a docker container
# Remember to edit datasource in application.yml 
docker build -t fsma-spring .
docker run --name fsma-spring -p 8080:8080 fsma-spring

# to run both postgres & fsma-server together
# first stop fsma-pg to free up port 5432
docker compose up

# to identify processes running on port 8080
sudo lsof -i :8080

# Running in IntelliJ with Postgres running in Docker
1. docker pull postgres:16.4
2. docker run --name fsma-db -e POSTGRES_PASSWORD=pw -d -p 5432:5432 postgres:16.4
3. ./gradlew clean build
4. Run FsmaApplicationKt in IntelliJ

# Running Spring Boot and Postgres in separate Docker containers
1. Change application.yml
- datasource:
    url: jdbc:postgresql://host.docker.internal:5432/postgres
2. ./gradlew clean build
3. docker build -t fsma-spring .
4. docker run --name fsma-spring -p 8080:8080 fsma-spring

# Docker Compose: launching both fsma-spring and docker with docker-compose.yml
1. Select image to run.  For local development uncomment as follows
   build:
     context: .
2. ./gradlew clean build
3. Stop any local postgres's
4. docker compose up

# Docker Compose: Launch an Image with Docker Compose
1. Change docker-compose as follows
   # build:
   #   context: .
# To run the version on AWS uncomment the next line
    image: fsma-spring:latest

# Running on AWS
1. Configure SpringBoot to run as an IDE - not docker compose
- update datasource application.yml:
  url: jdbc:postgresql://localhost:5432/postgres
2. Build and verify that running in IntelliJ works
- ./gradlew clean build
- Run FsmaApplicationKt in IntelliJ
3. Create a docker image for fsma-spring
4. Tag the image to 
