# MemorialDayTaskController 接口文档

生成日期：2026-06-12

## 1. 基本信息

- Controller：`com.zuji.remind.biz.controller.MemorialDayTaskController`
- 源码位置：`zuji-remind-push-biz/src/main/java/com/zuji/remind/biz/controller/MemorialDayTaskController.java`
- 基础路径：`/api/memorialDay`
- 返回格式：`application/json`
- Controller 内未声明 Swagger/OpenAPI 注解，也未看到显式鉴权注解。

## 2. 统一返回结构

所有接口返回 `CommonResult<T>`。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `code` | `int` | 状态码 |
| `message` | `String` | 状态消息 |
| `data` | `T` | 业务数据；无业务数据时为 `null` |

常见状态码：

| code | message | 说明 |
| --- | --- | --- |
| `200` | `操作成功` | 成功 |
| `500` | `操作失败` | 失败 |
| `404` | `参数检验失败` | 参数校验失败 |
| `401` | `暂未登录或token已经过期` | 未登录或 token 过期 |
| `403` | `没有相关权限` | 无权限 |

## 3. 接口概览

| 接口 | 方法 | 路径 | 返回 data |
| --- | --- | --- | --- |
| 查询所有任务 | `GET` | `/api/memorialDay/list` | `TaskVO[]` |
| 查询详情 | `GET` | `/api/memorialDay/detail/{id}` | `TaskVO` |
| 新增任务 | `POST` | `/api/memorialDay/add` | `null` |
| 修改任务 | `PUT` | `/api/memorialDay/{id}` | `null` |
| 删除任务 | `DELETE` | `/api/memorialDay/{id}` | `null` |

## 4. 请求模型

`POST /api/memorialDay/add` 和 `PUT /api/memorialDay/{id}` 使用请求体 `MemorialDayTaskDTO.SaveTaskDTO`。

| 字段 | 类型 | 是否必填 | 校验/取值 | 说明 |
| --- | --- | --- | --- | --- |
| `eventType` | `Integer` | 是 | `1..3` | 事件类型：`1=生日`，`2=纪念日`，`3=倒计时` |
| `name` | `String` | 是 | 非空字符串 | 名称 |
| `taskDesc` | `String` | 否 | 无 | 描述 |
| `dateType` | `Integer` | 是 | `1..2` | 日期类型：`1=阳历`，`2=农历` |
| `isLeapMonth` | `Integer` | 否 | 约定值 `0/1` | 是否闰月：`0=否`，`1=是` |
| `memorialDate` | `String` | 是 | 非空字符串 | 日期；具体格式代码中未约束 |
| `statusRemind` | `Integer` | 是 | `0..1` | 是否提醒；含义见「注意事项」 |
| `remindTimes` | `String` | 否 | 无 | 提醒频率；具体格式代码中未约束 |
| `remindWay` | `String` | 否 | 代码注释约定 `1/2/3` | 提醒方式：`1=邮箱`，`2=钉钉`，`3=微信` |

请求示例：

```json
{
  "eventType": 1,
  "name": "生日提醒",
  "taskDesc": "家人生日",
  "dateType": 1,
  "isLeapMonth": 0,
  "memorialDate": "2026-06-12",
  "statusRemind": 0,
  "remindTimes": "1",
  "remindWay": "1"
}
```

## 5. 响应模型

`TaskVO` 字段结构：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | `Long` | 主键；当前转换方法未赋值，实际响应可能为 `null` |
| `eventType` | `Integer` | 事件类型：`1=生日`，`2=纪念日`，`3=倒计时` |
| `name` | `String` | 名称 |
| `taskDesc` | `String` | 描述 |
| `dateType` | `Integer` | 日期类型：`1=阳历`，`2=农历` |
| `isLeapMonth` | `Integer` | 是否闰月：`0=否`，`1=是` |
| `memorialDate` | `String` | 日期 |
| `statusRemind` | `Integer` | 是否提醒；含义见「注意事项」 |
| `remindTimes` | `String` | 提醒频率 |
| `remindWay` | `String` | 提醒方式：`1=邮箱`，`2=钉钉`，`3=微信` |

## 6. 接口详情

### 6.1 查询所有任务

- 方法：`GET`
- 路径：`/api/memorialDay/list`
- 请求参数：无
- 业务行为：查询全部任务，转换为 `TaskVO` 列表后返回。

成功响应示例：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": null,
      "eventType": 1,
      "name": "生日提醒",
      "taskDesc": "家人生日",
      "dateType": 1,
      "isLeapMonth": 0,
      "memorialDate": "2026-06-12",
      "statusRemind": 0,
      "remindTimes": "1",
      "remindWay": "1"
    }
  ]
}
```

### 6.2 查询详情

- 方法：`GET`
- 路径：`/api/memorialDay/detail/{id}`
- 路径参数：

| 参数 | 类型 | 是否必填 | 说明 |
| --- | --- | --- | --- |
| `id` | `Long` | 是 | 查询 ID；为空时提示 `查询ID不能为空` |

- 业务行为：按 `id` 查询单条任务并返回 `TaskVO`。如果查询结果为空，转换时会触发参数校验失败。

### 6.3 新增任务

- 方法：`POST`
- 路径：`/api/memorialDay/add`
- 请求体：`SaveTaskDTO`
- 业务行为：将请求体转换为 `MemorialDayTask` 后插入；影响行数为 `1` 时返回成功，否则返回失败。

成功响应示例：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 6.4 修改任务

- 方法：`PUT`
- 路径：`/api/memorialDay/{id}`
- 路径参数：

| 参数 | 类型 | 是否必填 | 说明 |
| --- | --- | --- | --- |
| `id` | `Long` | 是 | 更新 ID；为空时提示 `更新ID不能为空` |

- 请求体：`SaveTaskDTO`
- 业务行为：按 `id` 更新任务；影响行数为 `1` 时返回成功，否则返回失败。

### 6.5 删除任务

- 方法：`DELETE`
- 路径：`/api/memorialDay/{id}`
- 路径参数：

| 参数 | 类型 | 是否必填 | 说明 |
| --- | --- | --- | --- |
| `id` | `Long` | 是 | 删除 ID；为空时提示 `删除ID不能为空` |

- 业务行为：按 `id` 删除任务；影响行数为 `1` 时返回成功，否则返回失败。
- `MemorialDayTask.isDelete` 使用 MyBatis-Plus `@TableLogic(value = "0", delval = "1")`，因此删除行为通常表现为逻辑删除。

## 7. 注意事项

1. `statusRemind` 注释存在冲突：DTO/VO 写的是 `0=提醒;1=不提醒`，Entity 写的是 `0=不提醒;1=提醒`。接口文档暂不把该字段含义定死，建议统一代码注释或补充枚举。
2. `TaskVO` 声明了 `id` 字段，但 `TaskVO.from(MemorialDayTask)` 当前没有设置 `id`，实际响应中的 `id` 可能为 `null`。
3. `memorialDate`、`remindTimes`、`remindWay` 的具体格式没有在 Controller/DTO 校验注解中约束。
4. 当前 Controller 内没有显式鉴权注解；如果项目通过网关、Filter、Interceptor 或 Spring Security 全局处理鉴权，需要以全局配置为准。
5. 本文档路径未包含可能存在的 servlet context path、网关前缀或版本前缀。
