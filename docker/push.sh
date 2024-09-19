#!/bin/sh

# for kscope
#aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 095724342970.dkr.ecr.us-east-1.amazonaws.com
#branch=$(git rev-parse --abbrev-ref HEAD | sed -r 's/[//]+/-/g')
#read -p "Enter an image tag [${branch}]: " tag
#docker tag kscopeinc/server:"${tag:-branch}" 095724342970.dkr.ecr.us-east-1.amazonaws.com/kscope-backend:"${tag:-branch}"
#docker push 095724342970.dkr.ecr.us-east-1.amazonaws.com/kscope-backend:"${tag:-branch}"

#************** fsma ***************
#docker tag fsma-spring:latest 381492154593.dkr.ecr.us-east-2.amazonaws.com/fsma:latest
#docker push 381492154593.dkr.ecr.us-east-2.amazonaws.com/fsma:latest

branch=$(git rev-parse --abbrev-ref HEAD | sed -r 's/[//]+/-/g')
read -p "Enter an image tag [${branch}]: " tag
docker push stepheneick/fsma-server2:latest
