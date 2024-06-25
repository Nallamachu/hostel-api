FROM openjdk:17-jdk-slim
ADD target/hostel-management-1.0.0-RELEASE.jar hostel-management.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "hostel-management.jar"]