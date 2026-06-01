# 빌드 단계: Spring Boot 애플리케이션을 실행 가능한 JAR로 컴파일
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# 소스 코드가 바뀌어도 의존성 레이어를 재사용할 수 있도록 Gradle 파일을 먼저 복사
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x ./gradlew && ./gradlew clean bootJar -x test --no-daemon

# 실행 단계: 더 작은 JRE 이미지에서 빌드된 JAR만 실행
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 루트가 아닌 사용자와 FILE_UPLOAD_DIR에서 사용할 이미지 업로드 디렉터리를 생성
RUN addgroup -S spring && adduser -S spring -G spring && mkdir -p /app/images && chown -R spring:spring /app

COPY --from=build /workspace/build/libs/*.jar /app/app.jar

USER spring

# prod 프로필은 RDS, Redis, JWT, 업로드 설정을 환경변수에서 읽기
ENV SPRING_PROFILES_ACTIVE=prod
ENV FILE_UPLOAD_DIR=/app/images

# 작은 EC2 인스턴스를 위한 보수적인 기본값이며, docker-compose에서 덮어쓰기 가능
ENV JAVA_OPTS="-Xms128m -Xmx384m -XX:MaxMetaspaceSize=192m"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
