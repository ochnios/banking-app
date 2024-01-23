FROM eclipse-temurin:17
WORKDIR /banking-be
COPY ./banking-be/build/libs/banking-be-0.0.1-SNAPSHOT.jar /banking-be
CMD ["java", "-jar", "banking-be-0.0.1-SNAPSHOT.jar"]