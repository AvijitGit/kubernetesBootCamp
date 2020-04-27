## If use only openjdk:8 then the image size will be 562MB if use alpine the image size will be 158MB 
FROM openjdk:8-alpine
ADD target/item-catalog.jar item-catalog.jar
EXPOSE 8763
ENTRYPOINT ["java" , "-jar", "item-catalog.jar"]