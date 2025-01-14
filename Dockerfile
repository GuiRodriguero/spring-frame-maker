# Etapa 1: Construir a aplicação nativamente
FROM ghcr.io/graalvm/native-image:latest as build

WORKDIR /app

RUN microdnf install maven
COPY pom.xml .
RUN mvn -B dependency:resolve dependency:resolve-plugins

COPY src ./src

RUN mvn -Pnative native:compile

# Etapa 2: Executar a aplicação nativa
FROM debian:bullseye-slim as run

RUN apt-get update && apt-get install -y libz1 && apt-get clean

WORKDIR /app

COPY --from=build /app/target/framemaker /app/framemaker

EXPOSE 8080

CMD ["./framemaker"]