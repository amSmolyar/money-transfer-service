FROM openjdk:8-jdk-alpine
VOLUME /log
EXPOSE 5500
ADD CardDataBase.txt .
ADD TestCardDataBase.txt .
ADD ./log transfer.log
ADD target/demo-0.0.1-SNAPSHOT.jar mytransferapp.jar
ENTRYPOINT ["java","-jar","/mytransferapp.jar"]