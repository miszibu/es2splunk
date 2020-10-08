## ES2Splunk

目标: 定时从ES获取数据送往 Splunk。

需要考虑的问题：
1. 服务宕机的问题，当服务突然宕机，如何保证下次服务启动时，延续之前的偏移量

Solution: 每一定数量的文档写入完成就更新本地数据库，一批次文档写入完成也更新数据库。

2. 数据初始化的问题，参考 SplunkESPlugin 的所有参数

3. 新增或删除Entry 如何实现

将数据存储到ES中去，每次定时器触发时，都去 ES 中获取最新的metadata.



## ES MetaData Index
```JSON
PUT es2splunk_meta_data
{
  "settings": {
    "number_of_replicas": 1,
    "number_of_shards": 1
  },
  "mappings": {
    "properties": {
      "indexName": {
        "type": "keyword"
      },
      "fromDate": {
        "type": "date"
      },
      "toDate": {
        "type": "date"
      }
    }
  }
}

POST es2splunk_meta_data/_doc
{
  "indexName": "zibu"
}

```