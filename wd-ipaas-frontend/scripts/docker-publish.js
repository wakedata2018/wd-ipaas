const { publish, clean } = require('@wakeadmin/docker-build');
const { DOCKER_IMAGE_NAME, DOCKER_VERSION } = require('./shared');

// 发布需要提供 DOCKER_USER、DOCKER_PASSWORD、DOCKER_SERVER 等环境变量

publish(DOCKER_IMAGE_NAME, DOCKER_VERSION, false);
clean(DOCKER_IMAGE_NAME);
