
:: Setting Java path if not configured in environmental variables
set JAVA_HOME=C:\Program Files\Java\jdk-15.0.2


:: Building OCI image (the same format as one created by docker build) using Cloud Native Buildpacks -- Springboot plugins
mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=weather-spring-boot-docker

:: Running the local image in Docker container
:: docker run -p 8080:8080 weather-spring-boot-docker