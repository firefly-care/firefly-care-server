# =================
# 1. Build Stage
# =================
# Gradle과 JDK가 포함된 이미지를 사용하여 프로젝트를 빌드합니다.
FROM gradle:8-jdk17 AS builder

# 작업 디렉토리 설정
WORKDIR /build

# Gradle 빌드 파일들을 먼저 복사하여 의존성을 캐시합니다.
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY gradlew ./

# 의존성을 먼저 다운로드하여 레이어 캐싱 효율을 높입니다.
RUN ./gradlew dependencies

# 나머지 소스 코드를 복사합니다.
COPY src ./src

# Gradle을 사용해 실행 가능한 JAR 파일을 빌드합니다.
RUN ./gradlew bootJar


# =================
# 2. Production Stage
# =================
# 자바 런타임(JRE)만 포함된 가벼운 이미지를 사용합니다.
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Build Stage에서 생성된 JAR 파일만 복사해옵니다.
COPY --from=builder /build/build/libs/*.jar app.jar

# 스프링 부트 애플리케이션의 기본 포트 8080을 노출합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 JAR 파일을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]