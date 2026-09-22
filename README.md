# 重要日期提醒服务

> 本服务当前主要是为了记录重要日期，进行提醒，当前已实现功能为生日提醒、纪念日提醒、倒计时提醒

| 模块                      | 描述                            |
|-------------------------|-------------------------------|
| zuji-remind-push-common | 该模块为公共模块，提供一些列公共方法            |
| zuji-remind-push-biz    | 该模块为业务实现模块，读取提醒任务，进行推送消息，具体实现 |

## 发布 Docker 镜像与 GitHub Release

推送 `vX.Y.Z` 正式版本标签后，GitHub Actions 使用 Temurin JDK 25 从干净源码构建，测试通过后发布：

- GHCR 镜像 `ghcr.io/inkzuji/zuji-remind-push-biz:X.Y.Z` 和 `latest`，支持 `linux/amd64`、`linux/arm64`。
- 同名 GitHub Release，包含自动生成的更新说明、镜像拉取命令及 digest。
- Release 附件 `zuji-remind-push-biz-X.Y.Z.jar` 和 `SHA256SUMS`，JAR 与两种架构镜像内的 `/app/app.jar` 一致。

工作流仅响应版本标签推送，不会自动升版、创建标签或部署服务器。标签必须与根 `pom.xml` 的版本完全一致，不接受 SNAPSHOT、预发布版本或前导零。所有发布串行执行，不取消正在运行的发布；`latest` 表示最近成功推送的正式版本镜像，不保证是版本号最大的版本。

请逐个推送发布标签，并等待本次发布完成后再发布下一个版本。GitHub 默认并发组只保留一个等待中的任务，连续触发多个版本时，较早的等待任务可能被替换，需要重跑。

### 发布步骤

先确保发布工作流和待发布代码已提交，并同步根项目及子模块版本。以下以 `0.4.1` 为例；后续发布替换为新的版本号：

```bash
# 使用项目已有脚本更新 Maven 多模块版本
sh ./set-versions.sh 0.4.1
# 若版本文件发生变更，将它们提交到当前分支
git add pom.xml zuji-remind-push-common/pom.xml zuji-remind-push-biz/pom.xml
git diff --cached
# 确认变更后提交（无版本变更时跳过）
git commit -m "release: v0.4.1"
git push origin HEAD

git tag -a v0.4.1 -m "Release v0.4.1"
git push origin v0.4.1
```

在仓库 Actions 的 `Release` 工作流中查看执行结果。工作流使用内置 `GITHUB_TOKEN`，只授予发布所需的 `contents: write` 和 `packages: write`，无需配置个人令牌；仓库或组织策略需允许这些权限。

GHCR 首次发布的包默认私有。如需匿名拉取，在包设置中将可见性改为 Public；已有同名包时，需要允许本仓库的 Actions 写入。详见 [GitHub Container registry 文档](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry)。

### 测试与本地构建

CI 执行下面的命令，仅排除依赖完整 Spring 上下文和真实数据库环境的 `BaseTests`、`AnniversarySchedulerTest`，其余测试全部执行。任一测试失败都会阻止镜像和 Release 发布，Surefire 报告保存在工作流附件中。

```bash
mvn --batch-mode --no-transfer-progress clean verify \
  -pl zuji-remind-push-biz -am \
  '-Dtest=!BaseTests,!AnniversarySchedulerTest' \
  -Dsurefire.failIfNoSpecifiedTests=false

# Docker 构建上下文必须是仓库根目录，复用刚生成的 JAR
docker build -f zuji-remind-push-biz/docker/Dockerfile \
  -t zuji-remind-push-biz:0.4.1 .
```

### 获取与校验产物

```bash
docker pull ghcr.io/inkzuji/zuji-remind-push-biz:0.4.1

# 将 Release 中的 JAR 和 SHA256SUMS 下载到同一目录
sha256sum --check SHA256SUMS
# macOS 可使用：shasum -a 256 -c SHA256SUMS
```

可使用 Release 说明中的 `image@sha256:...` 地址按 digest 拉取。JAR 需要 Java 25，启动配置沿用现有项目约定。

### 失败重跑

若失败时尚未创建 Release，可在 Actions 中选择 **Re-run all jobs**，针对原标签重新执行。镜像推送成功、Release 创建失败时，重跑会重新构建并推送该版本及 `latest`。已存在同名 Release（包括草稿）时，工作流会在构建和推送镜像前停止，避免覆盖；如有不完整草稿，应先人工检查并处理。已正式发布的版本需使用新版本号，不应移动旧标签或覆盖已有附件。

工作流在镜像推送后检查双架构清单及镜像内 JAR，再创建 Release。若推送后的核验或 Release 创建失败，已推送的镜像标签不会自动回滚，可结合日志和 digest 判断状态后重跑。
