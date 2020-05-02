FROM aulang/jdk

ADD oauth-1.0.0.jar app.jar

RUN sh -c 'touch /app.jar'

EXPOSE 8080

ENV JAVA_OPTS="$JAVA_OPTS -Duser.timezone=Asia/Shanghai -Djava.security.egd=file:/dev/./urandom -server -Xms128m -Xmx512m -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=128m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]