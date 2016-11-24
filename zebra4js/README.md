## Introduction 介绍
[![NPM version](https://img.shields.io/npm/v/thinkjs.svg?style=flat-square)](http://badge.fury.io/js/thinkjs)
[![travis-ci](https://img.shields.io/travis/75team/thinkjs.svg?style=flat-square)](https://travis-ci.org/75team/thinkjs)
[![Coverage Status](https://img.shields.io/coveralls/75team/thinkjs.svg?style=flat-square)](https://coveralls.io/github/75team/thinkjs)
[![Dependency Status](https://david-dm.org/75team/thinkjs.svg)](https://david-dm.org/75team/thinkjs)

`Zebra4js`是`落雨`基于 [ThinkJS](http://www.thinkjs.org) 开发的一套Spring-boot网关层,配合`Zebra4j`微服务框架，作为桥梁用来接收各个客户端的访问请求,并调用`Zookeeper`获取服务地址，提供高并发的各端`RESTful`服务调用。

本项目使用Es6 JavaScript语法，并自动编译，可以利用Webstom进行调试。

线上生产环境,建议使用pm2作为守护进程。

使用方法: 入口程序暂时放在www/testing.js中

## Frameworks and Tools 构建
* JavaScript & IDE: ES6 / Webstorm 16
* Backend: Thinkjs 2.x
* Database: no
* Cache:Redis
* Web Server: Nginx proxy 80端口转发->8360
* Build Tool: Webpack
* Other: 
* Port-nginx:http://localhost:80 
* Port-node:http://localhost:8360 


### Install dependencies 安装依赖

```
npm install
```

### Start server 开启服务

```
npm start
```

### Deploy with pm2 部署上线

Use pm2 to deploy app on production enviroment.

```
pm2 startOrReload pm2.json
```