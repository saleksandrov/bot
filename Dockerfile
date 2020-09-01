FROM openjdk:11-jre-slim

ENV JAVA_OPTS="$JAVA_OPTS -Xmx300m -XX:+UseContainerSupport -XX:+AlwaysActAsServerClassMachine" LANG=en_US.UTF-8 TZ=Europe/Moscow

WORKDIR "/opt/app"
