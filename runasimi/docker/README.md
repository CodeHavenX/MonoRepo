docker build -t cramsan/python:latest -f Dockerfile .
docker container run -P --rm -it cramsan/python:latest
