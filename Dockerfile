# Tomcat 10.1.10과 JDK 17을 기반으로 하는 이미지 사용
FROM openjdk:17-oracle

# Tomcat 포트를 노출합니다.
EXPOSE 8080

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=accesscontrol,prod,key,buildingId,proddddd

# Web Application Archive 복사
COPY target/*.jar /
ENTRYPOINT ["java", "-jar", "/ROOT.jar"]
