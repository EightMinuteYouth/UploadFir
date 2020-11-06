# gradle一键打包上传fir



长期以来做移动端都有一个困扰, 测试或者后台时不时会叫你打个包放在fir
有时候不是修改某些bug, 有可能只是修改一个版本号什么的又不需要提交git
这个时候手动的去打包然后打完之后又的手动拖到fir上面,这样做乏味且无趣.

无意间看到某群有人讨论gradle的脚本, 然后百度一波之后决定做了这个gradle
一键打包插件

###  简单的使用姿势
1.在根目录的build.gradle里面添加

``` classpath xxxxxxx ```

2.在app目录下的build.gradle里面添加

```
apply plugin: 'com.badyouth.uploadFirPlugin'

uploadConfig {
    /// fir token
    apiToken = "这里填自己fir上面的token"

    /// 上传描述
    description = "脚本上传测试"
}
```