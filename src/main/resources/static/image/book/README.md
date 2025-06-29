# 图书图片存储目录

这个目录用于存储用户上传的图书图片。

## 说明
- 此目录位于服务端的resources/static文件夹下
- 图片文件会通过Spring Boot的静态资源映射对外提供访问
- 访问路径：`http://localhost:8080/static/image/book/filename.jpg`
- 兼容路径：`http://localhost:8080/images/book/filename.jpg`

## 注意事项
- 文件名使用UUID生成，避免重复
- 支持常见图片格式：jpg, png, gif等
- 文件大小限制：50MB（在application-dev.yml中配置）
