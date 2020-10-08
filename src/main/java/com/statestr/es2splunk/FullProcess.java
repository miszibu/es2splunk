package com.statestr.es2splunk;

import com.statestr.es2splunk.Utils.SplunkUtils;
import com.statestr.es2splunk.Utils.ESRestfulUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;

public class FullProcess {

    // Parameter initialization
    String dataFormat = "yyyy-MM-dd hh:mm:ss";
    String fromDate = "2020-09-15 23:59:00";
    String toDate = "2020-09-16 00:00:00";

    @Autowired
    private ESRestfulUtils esRestfulUtils;

    public static void main(String[] args) {


        FullProcess fullProcess = new FullProcess();
        // 参数校验
        if (fullProcess.validateParameter()) {
            System.out.println("Parameter validation failed, can not process current request");
        }
        // 从ES中获取数据
        ArrayList<SearchHit> searchHitArr = fullProcess.getDataFromES();
        //searchHitArr.stream().forEach(i->System.out.println(i.getSourceAsMap().get("@timestamp")));

        // 将数据发往Splunk
        fullProcess.sendDataToSplunk(searchHitArr.get(0).toString().getBytes());

    }

    private boolean validateParameter() {
        dataFormat = dataFormat.trim();
        fromDate = fromDate.trim();
        toDate = toDate.trim();
        if (dataFormat.length() < 1 || fromDate.length() < 1 || toDate.length() < 1) {
            System.out.println("Date range setting illegal");
            System.out.println("dataFormat" + dataFormat);
            System.out.println("fromDate" + fromDate);
            System.out.println("toDate" + toDate);
            return true;
        }

        return false;
    }


    private void sendDataToSplunk(byte[] payload) {
        SplunkUtils splunkUtils = new SplunkUtils();
        splunkUtils.sendSingleIndexRequest(payload);

    }

    // 从ES中获取数据
    public ArrayList<SearchHit> getDataFromES() {
        DateFormat df = new SimpleDateFormat(dataFormat);
        try {
            Date from = df.parse(fromDate);
            Date to = df.parse(toDate);

            // ==============================================Search API================================================
        /*try {
            esRestfulUtils.timeRangeSearch(from, to, "heart*");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
            // ===========================================Search Scroll API================================================
            ArrayList<SearchHit> searchHitsList = esRestfulUtils.timeRangeSearchScroll(from, to, "metricbeat*", 100);
            return searchHitsList;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


}
