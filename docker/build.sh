#!/bin/sh

branch=$(git rev-parse --abbrev-ref HEAD | sed -r 's/[//]+/-/g')

read -p "Enter an image tag [${branch}]: " tag

docker buildx build --platform linux/amd64 -f Dockerfile -t stepheneick/fsma-server2:"${tag:-$branch}" .
