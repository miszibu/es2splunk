package com.statestr.es2splunk.Scheduler;

import com.statestr.es2splunk.Utils.ESRestfulUtils;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

// 定时任务触发器
@PropertySource("classpath:application.properties")
@Component
public class scheduler {

    @Autowired
    private ESRestfulUtils esRestfulUtils;

    @Value("${es.metaDataIndexName}")
    private String esMetaDataIndexName;

    @Scheduled(cron = "${schedule.service.interval}")
    private void scheduled(){

        Date startTime = new Date(System.currentTimeMillis());
        // Get All Configuration entries from elasticsearch
        // 从ES中获取所有的数据源索引的条目信息
        try {
            SearchHit[] esDataSourceList = esRestfulUtils.getConfigEntries(esMetaDataIndexName);
            for (int i = 0; i < esDataSourceList.length; i++) {
                System.out.println("Failed to get es metaData");
                String indexName = (String) esDataSourceList[0].getSourceAsMap().get("indexName");
                Date fromDate = (Date) esDataSourceList[0].getSourceAsMap().get("fromDate");
                Date toDate = (Date) esDataSourceList[0].getSourceAsMap().get("toDate");
                // 如果todate 存在，且 fromDate>=toDate则返回
                if (false){
                    continue;
                }
                // 直到 Fromdate 大于startTime || 某一批次返回的数据不足10条，表示剩下的数据已经都读出来了
                while (true){
                    // 访问数据  每次从ES中拿10条数据
                    esRestfulUtils.timeRangeSearchScroll(fromDate, toDate, indexName);
                    // 将10条数据 写入 Splunk 中
                    // 更新ES数据源 metadata entries 的 fromDate
                }

            }
        } catch (IOException e) {
            System.out.println("Failed to get es metaData");
            e.printStackTrace();
        }

    }



}
