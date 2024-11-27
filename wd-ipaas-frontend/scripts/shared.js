const pkg = require('../package.json');

// 行业
const PRODUCTION = process.env.STAGE === 'PRODUCTION';
const NOW = new Date();
const BUILD_ID =
  process.env.BUILD_ID ??
  `${NOW.getFullYear()}${NOW.getMonth() + 1}${NOW.getDate()}${NOW.getHours()}${NOW.getMinutes()}`;

// 镜像名称
const DOCKER_IMAGE_NAME = pkg.imageName;

// V季度 R月度
const RELEASETYPE = process.env.RELEASETYPE ?? 'V';

// 镜像版本  主版本号-冲刺版本号-迭代版本号
let DOCKER_VERSION = `${RELEASETYPE}${pkg.version}-S${pkg.sprint}`;

if (PRODUCTION) {
  DOCKER_VERSION = DOCKER_VERSION + `-RC${BUILD_ID}`;
} else {
  // 非正式版本使用 `-snapshot-BUILD`
  DOCKER_VERSION = `${pkg.version}-snapshot-${BUILD_ID}`;
}

module.exports = {
  DOCKER_IMAGE_NAME,
  DOCKER_VERSION,
  PRODUCTION,
};
