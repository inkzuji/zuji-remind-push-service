# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

重要日期提醒推送服务（Milestone Reminder & Push Service）。记录生日、纪念日、倒计时等重要日期，定时计算并通过邮件/钉钉推送提醒消息。

## Build & Run

```bash
# 构建（跳过测试，仅构建 biz 模块及其依赖）
sh ./builder.sh
# 等效于: mvn -T 4 -U clean package -DskipTests -pl ./zuji-remind-push-biz -am

# 运行测试
mvn test

# 运行单个测试类
mvn test -pl zuji-remind-push-biz -Dtest=AnniversarySchedulerTest

# 本地启动（dev profile，默认激活）
mvn spring-boot:run -pl zuji-remind-push-biz

# Docker 构建（版本号随 pom.xml 更新）
docker build --build-arg DEBUG=true --progress=plain \
  -t inkzuji/zuji-remind-push-biz:$(grep '<version>' pom.xml | head -1 | sed 's/.*<version>\(.*\)<\/version>.*/\1/') \
  -f ./zuji-remind-push-biz/docker/Dockerfile .
```

## Tech Stack

- Java 11, Spring Boot 2.7.16, MyBatis-Plus 3.5.3.2, MySQL 8
- Hutool 5.8, Lombok, Guava 33, OkHttp 4
- Alibaba DingTalk SDK 2.0（钉钉机器人推送）
- Spring Boot Mail（QQ SMTP 邮件推送）

## Architecture

**Maven 多模块结构：**

- `zuji-remind-push-common` — 公共模块（异常处理、AOP 日志、MDC 链路追踪、线程池配置）
- `zuji-remind-push-biz` — 业务模块（启动类 `BizApplication`，REST API + 定时任务）

**注意：** `biz` 模块下有包名拼写错误 `com.zuji.remind.biz.utils`（应为 utils），新增工具类请放到正确路径，不要沿用。

**核心流程（定时任务驱动）：**

```
Scheduler (cron) → AbstractEventFactory (策略模式)
  → AbstractDateFactory (阳历/农历日期计算)
  → AbstractNotifyFactory (通知频率计算)
  → MessageNotifyComponent (推送渠道分发)
    → EmailPushClient / DingDingPushClient
```

**三层工厂体系（策略模式 + 模板方法）：**

1. `AbstractEventFactory` — 事件处理骨架，三个实现：`BirthdayEventFactory`、`AnniversaryEventFactory`、
   `CountdownEventFactory`，通过 Spring Map 注入按 bean name 查找
2. `AbstractDateFactory` — 日期计算，`SolarCalendarDateFactory` / `LunarCalendarDateFactory`，通过 `DateTypeEnum` 静态选择
3. `AbstractNotifyFactory` — 通知策略，`FrequencyNotifyFactory` / `CountdownNotifyFactory`，通过 `EventTypeEnum` 静态选择

**推送渠道：** `AbstractMessageNotifyFactory` 有三个实现（邮件、钉钉、微信），注册到 `MessageNotifyComponent` 按
`RemindWayEnum` 路由。微信渠道当前为空实现。

**消息重试：** `PushMessageScheduler` 每 5 分钟扫描待发送消息，最多重试 20 次后标记永久失败。`ClearMessageScheduler` 每晚清理
7 天以上的旧消息。

**MDC 链路追踪：** `WebTraceFilter` 注入 traceId → `MdcTaskDecorator` 传播到异步线程 → 日志统一输出 `[%X{traceId}]`。

## REST API

基础路径：`/remind-push/api/memorialDay`，入口控制器 `MemorialDayTaskController`。

| 方法     | 路径             | 说明     |
|--------|----------------|--------|
| GET    | `/list`        | 查询全部任务 |
| GET    | `/detail/{id}` | 查询任务详情 |
| POST   | `/add`         | 新增任务   |
| PUT    | `/{id}`        | 更新任务   |
| DELETE | `/{id}`        | 删除任务   |

## Key Configuration

- 服务端口 8080，context path `/remind-push`
- 默认激活 `dev` profile，生产环境使用 `prd` profile（凭据通过环境变量注入）
- 异步线程池：5 核心线程 / 5 最大 / 200 队列
- 定时任务：纪念日检查 09:00，消息推送 */5min，消息清理 21:00

## Database

三张表，DDL 在 `zuji-remind-push-biz/db/`：

- `memorial_day_task` — 纪念日事件（类型、日期、提醒配置）
- `msg_push_task` — 推送消息队列（状态、重试次数）
- `msg_push_way` — 推送渠道配置（收件人、webhook 地址）
