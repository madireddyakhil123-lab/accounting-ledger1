FROM eclipse-temurin:21-jdk
WORKDIR /app
ADD target/accounting-ledger1-0.0.1-SNAPSHOT.jar accounting-ledger.jar
ENTRYPOINT ["java", "-jar", "/app/accounting-ledger.jar"]