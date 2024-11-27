const { build } = require('@wakeadmin/docker-build');
const { DOCKER_IMAGE_NAME } = require('./shared');

build(DOCKER_IMAGE_NAME, {}, '--pull');
