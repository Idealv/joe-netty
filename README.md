# Netty-IM

## 技术栈

- Netty：NIO的封装实现，支持多种网络协议，实现高性能的网络应用
- ProtoBuf：协议通信，相比JSON格式更加高性能，轻量化
- Redis：分布式缓存
- ZooKeeper：实现命名服务，提供分布式的IM，通过zookeeper集群来实现负载均衡

## 功能

- 实现了基于客户端的单聊

- 分布式IM，zookeeper作为命名服务，通过负载均衡器返回服务端实例

- 心跳检测
