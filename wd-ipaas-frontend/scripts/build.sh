#!/usr/bin/env bash

set -e
set -x

# 容器构建
# 需要提供以下参数
# DOCKER_USER docker 用户
# DOCKER_PASSWORD docker 用户密码

if [ "$STAGE" = 'PRODUCTION' ]; then
  export DOCKER_SERVER=ccr.ccs.tencentyun.com
else
  export DOCKER_SERVER=zh-harbor.wakedata.net
fi

env
node -v

npm i -g pnpm@7
pnpm install

# 构建静态资源
pnpm build

# 构建镜像
node ./scripts/docker-build.js

# 发布
node ./scripts/docker-publish.js

# 触发 Rancher 更新
node ./scripts/rancher-update.js
