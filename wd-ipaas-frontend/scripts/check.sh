#!/usr/bin/env bash

set -e
set -x

# 规范检查

env
node -v

npm i -g pnpm@7
pnpm -v
pnpm install

# 构建
pnpm run --if-present build

# 规范检查
pnpm wkstd gerrit-check

# 单元测试
pnpm run --if-present test
