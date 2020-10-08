package com.statestr.es2splunk.Utils;


import com.google.gson.JsonObject;
import com.google.gson.Gson;

public class SplunkUtils {

    // Test Index Name: test_logs
    // Token: 6ab31c26-c76a-41e1-9ad8-dfd8b69e82de

    //curl -XPOST -k -H "Authorization: Splunk 6ab31c26-c76a-41e1-9ad8-dfd8b69e82de" https://jabdlpc0020.it.statestr.com:8088/services/collector/event  -d '{"event":"hello world1"}'

    //curl -k -H "Authorization: Splunk 6ab31c26-c76a-41e1-9ad8-dfd8b69e82de" https://jabdlpc0020.it.statestr.com:8088/services/collector/event -d '[{"event":"hello world1"},{"event":"hello world2"}]'
    private HttpClientUtil httpClient;

    public SplunkUtils() {
        httpClient = new HttpClientUtil("https://jabdlpc0020.it.statestr.com:8088/services/collector/event");
    }

    public void sendBulkIndexRequest() {

    }

    public void sendSingleIndexRequest(JsonObject payload) {
        try {
            String reqResponse = httpClient.POST("Splunk 6ab31c26-c76a-41e1-9ad8-dfd8b69e82de", payload.toString().getBytes());
            System.out.println(reqResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSingleIndexRequest(byte[] payload) {
        try {
            String reqResponse = httpClient.POST("Splunk 6ab31c26-c76a-41e1-9ad8-dfd8b69e82de", payload);
            System.out.println(reqResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SplunkUtils splunkUtils = new SplunkUtils();
        JsonObject queryBody = new Gson().fromJson("{'event': 'hello world1'}", JsonObject.class);
        splunkUtils.sendSingleIndexRequest(queryBody);
    }
}

//    String str =  "some string goes here";
//    byte[] outputInBytes = str.getBytes("UTF-8");
//    OutputStream os = conn.getOutputStream();
//os.write( outputInBytes );
//        os.close();