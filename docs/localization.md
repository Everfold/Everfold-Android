# 修改翻译

资源目录：`shared/src/commonMain/composeResources/`。

- `values/`：中文，也是未支持语言的回退文本。
- `values-en/`：英文。
- 并排打开两个目录下的同名 XML；分组注释、资源键及顺序一致。

| 文件 | 内容 |
| --- | --- |
| `common.xml` | 通用操作、相对时间 |
| `settings.xml` | 主导航、语言设置、关于 |
| `theme.xml` | 明暗模式、预设配色、自定义颜色 |
| `sessions.xml` | 会话列表、搜索、列表状态 |
| `conversation.xml` | 对话、输入框、示例回复 |
| `connections.xml` | 连接设置页面 |
| `onboarding.xml` | 初始化步骤、功能介绍、使用模式 |
| `cluster.xml` | 集群连接、验证、文件导入、连接测试 |

修改文案时编辑 `<string>` 标签内的文字。资源键 `name` 用于代码引用，移动文件无需修改代码，同一语言内不能重名。

新增条目时，在中英文同名文件的同一分组、同一位置添加相同资源键。
保留 `%1$d`、`%2$d` 等占位符；对应参数含义写在分组注释中。
换行使用 `\n`，段落分隔使用 `\n\n`；XML 中的 `&` 写成 `&amp;`，`<` 写成 `&lt;`。

可运行 `./gradlew :shared:jvmTest` 检查资源生成、编译及现有语言测试。
