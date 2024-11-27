# dw-open

数据开放服务平台


打包命令：mvn clean package -DskipTests

开发环境运行命令：java -jar 本项目打出来的包 --spring.profiles.active=local --server.port=8080 --spring.config.local=application-test.yml >/dev/null 2>&1 &
测试环境运行命令：java -jar 本项目打出来的包 --spring.profiles.active=test --server.port=8080 --spring.config.local=application-test.yml >/dev/null 2>&1 &
正式环境运行命令：java -jar 本项目打出来的包 --spring.profiles.active=idc --server.port=8080 --spring.config.local=application-test.yml >/dev/null 2>&1 &

升级版本号命令：mvn versions:set

回滚升级版本号：mvn versions:revert

提交升级版本号：mvn versions:commit
