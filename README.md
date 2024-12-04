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
docker run --name fsma-pg -e POSTGRES_PASSWORD=pw -p 5432:5432 -d postgres:16.4

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
1. Change application.yml
- datasource:
  url: jdbc:postgresql://host.docker.internal:5432/postgres
2. In docker-compose.yml select image to run or for local development 
uncomment as follows
   build:
     context: .
   #image: fsma-spring:latest
3. ./gradlew clean build
4. Stop any local postgres's
5. docker compose up

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
- Run FsmaApplicationKt in IntelliJ to verify that the build works
3. If you want to create a docker image for dockerhub.com
   # Build the docker image and tag it
   docker build -t fsma-spring .
   docker tag fsma-spring stepheneick/fsma-spring 
   # Push image to dockerhub.com
   docker push stepheneick/fsma-spring
4. Create a docker image for Amazon ECR
   docker tag fsma-spring:latest 381492154593.dkr.ecr.us-east-2.amazonaws.com/fsma/fsma-spring:latest 
   # Authenticate Docker client to Amazon ECR 
   aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 381492154593.dkr.ecr.us-east-2.amazonaws.com
   # Push image to AWS ECR
   docker push 381492154593.dkr.ecr.us-east-2.amazonaws.com/fsma/fsma-spring:latest
5. Create a new task revision
   # On Amazon go to ECS>Task definitions>Revision X>Containers
   # Select Create new revision & take all defaults & Create 
6. Update fsma-service to launch new task
    # On the Revision drop-down select new revision
    # Change the number of active instances from 0 to 1
    # Update the service
7. To shutdown service
    # go to fsma-service>Update
    # Set desired tasks to 0

Running on AWS
1. Setup RDS Postgres database on AWS
2. Update localhost to be aws database endpoint