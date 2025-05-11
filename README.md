# invoice-pdf-parse

电子发票（PDF格式）解析工具，基于 [sanluan/einvoice](https://github.com/sanluan/einvoice.git) 重构优化，主要提升代码可读性与发票信息识别准确率。支持解析增值税普通发票、专用发票等常见电子发票。

## [在线体验](https://invoice-web-lb.xj1988.top/)

## 功能特性

- 多维度信息解析 ：支持解析发票头部信息（机器编号、发票代码/号码、开票日期、校验码）、价税合计（大写/小写）、税额、发票类型（普通/专用）、密码区、购买方/销售方信息及商品明细等核心字段。
- 正则表达式驱动 ：通过抽象类 AbstractRegularParse 定义正则解析模板，具体解析逻辑（如 HeadRegularParse 、 TotalAmountRegularParse ）通过重写正则表达式实现灵活扩展。
- 区域文本提取 ：结合PDFBox的 PDFTextStripperByArea 实现区域文本定位，提升复杂版式发票的识别准确率。
- 错误容错 ：关键解析步骤添加日志警告（如字段赋值失败），避免解析过程中断。

## 核心依赖

- Apache PDFBox 2.0.8：PDF文本提取与区域定位。
- Hutool 5.4.6：反射工具类（ ReflectUtil ）简化字段赋值操作。
- Commons Lang3 3.12.0：字符串处理（如空值校验、正则匹配）。
- Lombok 1.16.20：通过 @Data 注解简化 ParseRequest 等模型类代码。
- SLF4J 1.7.32：日志接口（实际实现需自行引入，如Logback）。

## 目录结构

```
invoice-pdf-parse/
├── src/
│   ├── main/
│   │   ├── java/               
│   │   │   ├── com/invoice/
│   │   │   │   ├── domain/     # 领域模型（Invoice、ParseRequest）
│   │   │   │   ├── extractor/  # 解析器执行器（ParseExtractor）
│   │   │   │   └── parse/      # 解析器接口与实现（抽象类、正则解析器）
│   │   └── resources/          # 服务配置（META-INF/services）
│   └── test/                   # 测试代码
├── .gitignore                  # Git忽略规则
├── LICENSE                     # MIT许可证
├── pom.xml                     # Maven依赖配置
└── README.md                   # 项目说明
```

## 快速开始

```java
File pdfFile = new File("path/to/invoice.pdf");
Invoice invoice = ParseExtractor.getInvoice(pdfFile);
```
