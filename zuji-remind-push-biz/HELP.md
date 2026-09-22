# Docker Compose 部署

Compose 文件位于本目录的 `docker-compose.yml`，需要 Docker 和 Docker Compose v2。
仅启动提醒服务，连接已部署且数据库和表结构已就绪的 MySQL。保留原有容器名、端口、静态 IP、
`custom-network` 网络和服务器挂载目录；应用镜像默认使用项目发布的 GHCR 地址，标签为 `latest`。
服务器部署使用已发布镜像，无需源码、JDK 或 Maven，也无需执行 `docker compose build`。
如镜像尚未发布，需要先在源码环境完成构建和推送。

## 1. 配置环境变量

将 `docker-compose.yml` 放在服务器的部署目录，进入该目录执行后续 Compose 命令。
在源码仓库中部署时，该目录为 `zuji-remind-push-biz`。创建 `.env`，填写实际数据库密码、发件邮箱和邮箱授权码：

```dotenv
MYSQL_ROOT_PASSWORD='请替换为数据库密码'
SPRING_DATASOURCE_USERNAME=root
# 按实际 MySQL 地址修改；若 custom-network 内可解析 mysql，可省略此项使用默认地址
SPRING_DATASOURCE_URL='jdbc:mysql://10.10.10.220:3306/zuji_remind?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true&characterEncoding=utf8'
SPRING_MAIL_USERNAME='your-email@qq.com'
SPRING_MAIL_PASSWORD='请替换为邮箱授权码'

# 可选，以下为默认值
APP_PORT=18080
APP_LOG_DIR=/data/docker/service/zuji-remind-push-biz/logs
```

`.env` 已加入 Git 忽略规则。密码使用单引号包裹，可避免其中的 `$` 被 Compose 插值。
应用数据库用户名默认为 `root`，数据库密码取自 `MYSQL_ROOT_PASSWORD`。
将 `MYSQL_ROOT_PASSWORD` 设置为实际连接账号的密码；该变量仅用于应用连接，不会修改已有 MySQL 密码。
默认 JDBC 地址使用 `mysql:3306/zuji_remind`，要求已有 MySQL 在 `custom-network` 内可通过主机名 `mysql` 访问。
否则必须在 `.env` 中通过 `SPRING_DATASOURCE_URL` 配置应用容器可访问的数据库地址。
邮件服务器默认 `smtp.qq.com:465`，可用 `SPRING_MAIL_HOST`、`SPRING_MAIL_PORT` 覆盖。
JVM 默认参数为 `-Xmx512m -Dfile.encoding=UTF8 -Duser.timezone=GMT+08`，通过
`JAVA_TOOL_OPTIONS` 生效；需要调整时可在 `.env` 中覆盖该变量。

本机 macOS 部署时，在 `.env` 中将挂载目录改为当前用户可写且 Docker 可访问的路径，例如：

```dotenv
APP_LOG_DIR=/Users/jianjunwang/Downloads/docker/service/zuji-remind-push-biz/logs
```

## 2. 创建网络

首次部署且网络不存在时执行一次：

```shell
docker network create --subnet=172.20.0.0/16 custom-network
```

已有网络直接复用，需确认其网段为 `172.20.0.0/16`，且 `172.20.0.10`（提醒服务）未被其他容器占用。
如果已有旧 `docker run` 提醒服务容器，先处理同名容器、端口和 IP 冲突，再启动 Compose。

## 3. 启动应用

```shell
# 校验配置，不输出密码
docker compose config --quiet

# 拉取镜像并启动
docker compose pull remind-push
docker compose up -d --no-build remind-push

# 查看状态与应用日志
docker compose ps
docker compose logs -f remind-push
```

若 GHCR 镜像为私有包，先使用有该包读取权限的账号执行 `docker login ghcr.io`。
应用映射 `18080:8080`，访问前缀为 `/remind-push`，激活 `prd` 配置，日志写入挂载的 `/logs`。
启动前确认已有 MySQL 可访问，且数据库及表结构已就绪；无需上传 `db` 目录。
Compose 不创建 MySQL 容器，也不执行数据库或表结构初始化。

## 4. 从完整源码构建镜像（可选）

本节仅用于源码开发环境，需要完整仓库、JDK 25 和 Maven。
服务器只有部署文件时，使用第 3 节的 `pull`、`up` 命令。
Compose 不包含 `build` 配置；如需自行构建，在仓库根目录执行：

```shell
# builder.sh 沿用现有逻辑，跳过测试
sh ./builder.sh
docker build --progress=plain \
  -t ghcr.io/inkzuji/zuji-remind-push-biz:latest \
  -f zuji-remind-push-biz/docker/Dockerfile .

# 在本机使用刚构建的镜像，避免拉取远端 latest 覆盖本地标签
cd zuji-remind-push-biz
docker compose up -d --no-build --pull never remind-push
```

Dockerfile 使用仓库根目录作为构建上下文，复制
`zuji-remind-push-biz/target/zuji-remind-push-biz-*.jar`。
若仅复制部署文件后执行构建，可能出现 `lstat .../zuji-remind-push-biz: no such file or directory`。

如需推送到原 Docker Hub 仓库，在构建后执行：

```shell
docker tag ghcr.io/inkzuji/zuji-remind-push-biz:latest inkzuji/zuji-remind-push-biz:latest
docker login
docker push inkzuji/zuji-remind-push-biz:latest
```

使用 Docker Hub 镜像部署时，将 `docker-compose.yml` 中应用的 `image` 改为
`inkzuji/zuji-remind-push-biz:latest`。

## 5. 验证与日常维护

```shell
# 本机测试；远程部署将 localhost 替换为服务器地址
curl http://localhost:18080/remind-push/api/memorialDay/list

# 更新应用镜像
docker compose pull remind-push
docker compose up -d --no-build remind-push

# 停止应用
docker compose stop remind-push

# 停止并移除本 Compose 项目的容器
docker compose down
```

列表结果取决于已有数据库中的业务数据；`/api/memorialDay/detail/{id}` 需使用实际存在的 ID。
`down` 保留宿主机挂载的日志和外部 `custom-network` 网络。
