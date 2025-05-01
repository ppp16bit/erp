FROM fedora:41 AS builder
WORKDIR /app

RUN dnf update -y && dnf install -y java-21-openjdk-devel maven

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM fedora:41
WORKDIR /app

RUN dnf update -y && dnf install -y java-21-openjdk

COPY --from=builder /app/target/*.jar erp.jar

EXPOSE 8080
CMD ["java", "-jar", "erp.jar"]