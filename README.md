# fsma-server
Test code for FSMA Server

# To compile: ./gradlew clean build -x test  
# -x test does not run the unit tests

# to run inside a docker container edit application.yml
# for running in IntelliJ
datasource:
    url: jdbc:postgresql://localhost:5432/postgres
# for running in a separate docker container
datasource:
    url: jdbc:postgresql://host.docker.internal:5432/postgres

# to create docker image
docker build -t springio/gs-spring-boot-docker .

# to run inside docker container
docker run -p 8080:8080 springio/gs-spring-boot-docker
