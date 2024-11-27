FROM ccr.ccs.tencentyun.com/libriary/java8-custom-mini:v1.0
ADD target/*.jar app.jar
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
EXPOSE 8089
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTIONS} -jar /app.jar ${EXTENSION_PARAMS}"]
