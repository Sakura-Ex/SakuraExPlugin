# SakuraExPlugin

<p>
<a href="https://github.com/Sakura-Ex/SakuraExPlugin">
<img alt="GitHub top language" src="https://img.shields.io/github/languages/top/Sakura-Ex/SakuraExPlugin?style=plastic">
</a>
</p>

一个简单的机器人插件，日常的管理和配置都可以通过和机器人私聊完成。

## 使用说明

### 安装

目前暂无`release`，若需要使用需要将项目克隆到本地。

### 说明

为了避免打扰群友，插件默认不对任何群启用功能。
通过私聊机器人或手动修改配置文件可进行白名单的管理（对所有机器人的好友启用配置功能）。

## 管理配置指令

**特别提醒：由于 mirai 的配置文件自动保存机制，通过机器人指令完成的修改需要等待一段时间才会保存至配置文件。
如果控制台还没自动保存就关闭了 mirai-console，会导致修改无效。一般配置后等待几分钟即可完成保存。**

| 指令名                                      | 作用                                |
|------------------------------------------|-----------------------------------|
| /op \[qq number\]                        | 给予特定 qq 所有群指令权限，省略表示自己的 qq        |
| /deop \[qq number\]                      | 与 /op 作用相反                        |
| /check \[info\]                          | 查看对应配置文件信息，留空表示查看信息类别             |
| /img add \<type\> \<api link\>           | 往对应类别 API 库中添加一个 API。若类别不存在，则自动创建 |
| /img remove \<type\> \[api link\]        | 删除对应 API 库中对应的 API，留空表示删除类别       |
| /group add \<group id\> \[qq number\]    | 给予某 qq 群特定成员所有群指令权限，留空 qq 表示所有群成员 |
| /group remove \<group id\> \[qq number\] | 剥夺某 qq 群特定成员所有群指令权限，留空 qq 表示所有群成员 |

格式：\\指令名 \<必填参数\> \[可选参数\]

添加 API 链接时仅检查链接格式，不会检查链接的可用性，需自行确保可用，
并且 api 链接应该是直接返回图片而非返回包含图片地址等信息的 json

### 配置文件结构

```yaml
# 默认的图片 API
imageAPIs: 
  comic: # 图片种类
    - 'https://imgapi.cn/api.php?fl=dongman' # API
  girl: 
    - 'https://imgapi.cn/api.php?fl=meizi'
  landscape: 
    - 'https://imgapi.cn/api.php?fl=fengjing'
# 群聊白名单
whitelist: 
  # 含有成员 qq 的样式
  1234567890: # 群号
    - 987654321 # qq 号
  # 不含成员 qq 的样式，表示对所有成员开放权限
  2222222222: [] 
# 始终有权限的 qq
whiteQQList: 
  - 123123123
  - 729929339
# 图片获取失败重试的次数
imgRetryCount: 5
# 两次获取图片的间隔 (ms)
imgCD: 10000
```