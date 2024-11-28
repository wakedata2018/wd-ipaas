
# ✨ 快速开始

## 组件说明

| 名称   | 版本 | 描述  |
| --- | --- | --- | 
| MySQL |  8.0.x |  数据库存储 |
| Redis | 4.0+ | 缓存 |
| Nacos | 2.3.x | 配置中心与注册中心 |
| wd_permission | N/A | 权限服务 |
| wd_ipaas | N/A | 集成云服务|



## 数据库初始化


### 1、创建用户

```
 CREATE USER 'wdclouduser'@'%' IDENTIFIED BY 'wdclouduser';
 
GRANT ALL PRIVILEGES ON *.* TO 'wdclouduser'@'%' WITH GRANT OPTION;
 
 FLUSH PRIVILEGES;
```

### 2、执行数据库初始化脚本

```
 CREATE DATABASE IF NOT EXISTS wd_permission CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

 CREATE DATABASE IF NOT EXISTS wd_ipaas CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

### 3、执行数据库导入脚本
```
 cd scripts
 chmod +x script.sh
 ./script.sh
```

注意数据库需要移除参数 `ONLY_FULL_GROUP_BY`

```
SELECT @@GLOBAL.sql_mode;

SET GLOBAL sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
```

## 后端部署

### 1、部署Nacos、MySQL、Redis中间件


根据前面的版本，可自行部署，部署完成后将 nacos/nacos_config_export_xx.zip 配置导入nacos

根据MySQL、Redis、域名自行修改相关配置


### 2、打包& 部署服务

```
cd wd-ipaas-backend

mvn clean package -DskipTests


docker build -t wd-ipaas --file=bdp-open-web/src/main/docker/Dockerfile bdp-open-web


# 执行 docker命令启动服务，记得命令中nacos地址、账密修改为你的nacos相关

docker run -d --name wd-ipaas --restart unless-stopped -p 8082:8082 -e JAVA_OPTIONS='-server -verbose:gc -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -XX:+PrintTenuringDistribution -XX:+PrintCommandLineFlags -XX:+DisableExplicitGC -XX:+PrintPromotionFailure -XX:+HeapDumpOnOutOfMemoryError -XX:+UseG1GC -XX:SurvivorRatio=6 -XX:G1ReservePercent=12 -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=30 -XX:MaxDirectMemorySize=512M -XX:G1HeapRegionSize=2M -XX:HeapDumpPath=/tmp/heapdump.hprof -XX:ErrorFile=/tmp/hs_err_log.log -Xloggc:/tmp/gc.log -XX:ParallelGCThreads=2 -Duser.timezone='Asia/Shanghai' -Dserver.port=8082 -Xmx2G -Xms2G -Dspring.cloud.nacos.server-addr=127.0.0.1:8848 -Dspring.cloud.nacos.config.namespace=wdcloud -Dspring.cloud.nacos.discovery.namespace=wdcloud -Dspring.cloud.nacos.username=nacos -Dspring.cloud.nacos.password=nacospassword' wd-ipaas

#启动权限服务镜像
docker run -d --name WD-PERMISSION-OP-20241126 --restart unless-stopped -p 8083:8083 -e JAVA_OPTIONS='-server -verbose:gc -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -XX:+PrintTenuringDistribution -XX:+PrintCommandLineFlags -XX:+DisableExplicitGC -XX:+PrintPromotionFailure -XX:+HeapDumpOnOutOfMemoryError -XX:+UseG1GC -XX:SurvivorRatio=6 -XX:G1ReservePercent=12 -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=30 -XX:MaxDirectMemorySize=512M -XX:G1HeapRegionSize=2M -XX:HeapDumpPath=/tmp/heapdump.hprof -XX:ErrorFile=/tmp/hs_err_log.log -Xloggc:/tmp/gc.log -XX:ParallelGCThreads=2 -Duser.timezone='Asia/Shanghai' -Dserver.port=8083 -Xmx2G -Xms2G -Dspring.cloud.nacos.server-addr=192.168.0.110:8848 -Dspring.cloud.nacos.config.namespace=wdcloud -Dspring.cloud.nacos.discovery.namespace=wdcloud -Dspring.cloud.nacos.config.username=nacos -Dspring.cloud.nacos.config.password=hsauf#H514 -Dspring.cloud.nacos.discovery.username=nacos -Dspring.cloud.nacos.discovery.password=hsauf#H514' crpi-ldamce7tccsu91ry.cn-shenzhen.personal.cr.aliyuncs.com/wakedata_public/wakedata:WD-PERMISSION-OP-20241126
```



## 前端部署

### 1、执行yarn打包构建
```
cd wd-ipaas-frontend
yarn install
yarn build
```
build成功后目录下会有个dist文件

### 2、启动nginx镜像

* 将nginx下配置文件复制到/data/nginx目录

* 修改nginx配置文件里面的域名，以及前面启动的ipaas与permission服务的地址

* 将步骤1打包的dist文件，解压到/data/nginx/html/ipaas下面

* 启动nginx服务
```
docker run -d --name nginx80 -p 80:80 -p 443:443 -v /data/nginx/nginx.conf:/etc/nginx/nginx.conf -v /data/nginx/logs:/var/log/nginx -v /data/nginx/html:/usr/share/nginx/html -v /data/nginx/conf:/etc/nginx/conf.d -v /data/nginx/conf/cert:/usr/share/nginx/cert --privileged=true nginx:stable-alpine
```

## 开始访问

到这里你就可以通过nginx里面配置的域名访问服务了，你也可以访问在线demo。

demo地址：https://open-ipaas.wakedata.com

默认账密： wakedata/TE&YHFWAT134

