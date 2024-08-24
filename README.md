# fsma-server
Test code for FSMA Server

# To compile: ./gradlew clean build -x test  
# -x test does not run the unit tests

# to run inside a docker container edit application.yml
# for running in IntelliJ or with docker compose
# Make sure to build after making a change with:  ./gradlew clean build
datasource:
    url: jdbc:postgresql://localhost:5432/postgres
# for running in a separate docker container
datasource:
    url: jdbc:postgresql://host.docker.internal:5432/postgres

# to create docker image

# to start postgres in a docker container
docker run --name fsma-pg -e POSTGRES_PASSWORD=pw -p 5432:5432 -d postgres:16.2

# to run inside docker container accessing postgres in a docker container
# Remember to edit datasource in application.yml 
docker build -t fsma-spring .
docker run --name fsma-spring -p 8080:8080 fsma-spring

#to run both postgres & fsma-server together
# first stop fsma-pg to free up port 5432
docker compose up

# to identify processes running on port 8080
sudo lsof -i :8080
