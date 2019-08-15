## r2-generator(Android R2文件生成器)
![](https://jitpack.io/v/LevisLv/r2-generator.svg)

参考 [butterknife-gradle-plugin](https://github.com/JakeWharton/butterknife/tree/master/butterknife-gradle-plugin) 实现

## 一、介绍
* 在子模块的注解中使用 R 文件资源引用的时候，会提示 "Attribute value must be constant" 的错误，该插件就是解决这类问题的。
* 该插件仅支持 AndroidX。
* 编译后会在对应模块的 build/generated/source/r2/ 目录下生成相应的 R2 文件。
* 生成的 StatisticsR 类各 int 属性值与原 R 类对应的值并不相同，无法通过 Resources#getResourceName(int)、Resources#getResourceEntryName(int) 等方法获得预期数据，需结合插件和 SDK 做进一步处理。

## 二、配置
### 1、添加 maven 地址及 classpath（build.gradle in project）
```groovy
buildscript {
    repositories {
        ······
        maven { url 'https://www.jitpack.io' }
    }

    dependencies {
        ······
        // 要求gradle插件版本最低3.1.0
        classpath 'com.github.LevisLv:r2-generator:1.0.0'
    }
}
```

### 2、引用插件（build.gradle in every module）
```groovy
······
apply plugin: 'com.levislv.r2.generator'
```
