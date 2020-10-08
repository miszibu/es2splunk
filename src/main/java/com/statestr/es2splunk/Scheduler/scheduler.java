package com.statestr.es2splunk.Scheduler;

import com.statestr.es2splunk.Utils.ESRestfulUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

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

        // Get All Configuration entries from elasticsearch
        try {
            esRestfulUtils.getConfigEntries(esMetaDataIndexName);
        } catch (IOException e) {
            System.out.println("Failed to get es metaData");
            e.printStackTrace();
        }
        System.out.println("aaa");
    }

}
