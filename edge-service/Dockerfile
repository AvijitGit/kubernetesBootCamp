## If use only openjdk:8 then the image size will be 562MB if use alpine the image size will be 158MB 
FROM openjdk:8-alpine
## The ADD command basically take the jar from the target directory wrap inside the docker image and put inside the container.
## with the jar the docker image size is 562 MB and without the jar the docker file size is 510 MB.
## the jar file size is 50.2 MB
ADD target/edge-service.jar edge-service.jar
EXPOSE 8764
ENTRYPOINT ["java" , "-jar", "edge-service.jar"]