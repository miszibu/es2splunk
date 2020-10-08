package com.statestr.es2splunk.Utils;

import java.io.IOException;
import java.util.Arrays;


import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;

@PropertySource("classpath:application.properties")
@Component
public class ESRestfulUtils {


    // RestHighLevelClient Initialization
    public RestHighLevelClient client;

    public ESRestfulUtils(@Value("${es.metadataAvailableNode}") String esAvailableNode,
                          @Value("${es.user}") String esUser,
                          @Value("${es.password}") String esPassword) {

        if (esAvailableNode != null && esUser != null && esPassword != null) {
            String[] esAvailableNodesEle = esAvailableNode.replace("//", "").split(":");
            final CredentialsProvider credentialsProvider =
                    new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(esUser, esPassword));

            client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(esAvailableNodesEle[1],
                                    Integer.valueOf(esAvailableNodesEle[2]),
                                    esAvailableNodesEle[0])).setHttpClientConfigCallback(httpClientBuilder -> {
                        httpClientBuilder.disableAuthCaching();
                        return httpClientBuilder
                                .setDefaultCredentialsProvider(credentialsProvider);
                    }));
        } else {
            System.out.println("ESRestful Client failed to initialize ");
        }


    }


    public void timeRangeSearchScroll(Date from, Date to, String indexName) throws IOException {
        this.timeRangeSearchScroll(from, to, indexName, 10);
    }

    public ArrayList<SearchHit> timeRangeSearchScroll(Date from, Date to, String indexName, int batchSize) throws IOException {
        ArrayList<SearchHit> searchHitsList = new ArrayList<SearchHit>();
        // Create SearchRequest
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.scroll(scroll);
        // Make a time range query
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(batchSize);
        searchSourceBuilder.sort(new FieldSortBuilder("@timestamp").order(SortOrder.ASC));
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.from(from, Boolean.TRUE).to(to);
        searchSourceBuilder.query(rangeQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();
        SearchHit[] searchHits = searchResponse.getHits().getHits();

        while (searchHits != null && searchHits.length > 0) {

            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
            searchHits = searchResponse.getHits().getHits();
            searchHitsList.addAll(Arrays.asList(searchHits));
        }

        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        boolean succeeded = clearScrollResponse.isSucceeded();
        if (succeeded) {
            System.out.println("============================Scroll search finished================================");
            System.out.println("Scroll search finished");

        }
        return searchHitsList;
    }

    public void timeRangeSearch(Date from, Date to, String indexName) throws IOException {

        // Create SearchRequest
        SearchRequest searchRequest = new SearchRequest();

        // Make a time range query
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.from(from, Boolean.TRUE).to(to);
        searchSourceBuilder.query(rangeQueryBuilder);
        // Sort by timestamp in ASC
        searchSourceBuilder.sort(new FieldSortBuilder("@timestamp").order(SortOrder.ASC));

        // sourceBuilder.from(0);
        // sourceBuilder.size(5);
        // sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        // Assemble query request
        searchRequest.indices(indexName);
        searchRequest.source(searchSourceBuilder);

        // Send query to es
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        RestStatus status = searchResponse.status();
        TimeValue took = searchResponse.getTook();
        Boolean terminatedEarly = searchResponse.isTerminatedEarly();
        boolean timedOut = searchResponse.isTimedOut();
        System.out.println("==============Response of time range query to index(" + indexName + ")========================");
        System.out.println("Status:              " + status.toString());
        System.out.println("Request time cost:   " + took.toString());
        System.out.println("Matched docs num:    " + hits.getTotalHits());

        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            // do something with the SearchHit
            System.out.println(hit.toString());
        }
    }

    public SearchHit[] getConfigEntries(String indexName) throws IOException {
        // Create MatchAll SearchRequest
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        // Send query to es
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        RestStatus status = searchResponse.status();
        TimeValue took = searchResponse.getTook();
        Boolean terminatedEarly = searchResponse.isTerminatedEarly();
        boolean timedOut = searchResponse.isTimedOut();
        System.out.println("Status:              " + status.toString());
        System.out.println("Request time cost:   " + took.toString());
        System.out.println("Matched docs num:    " + hits.getTotalHits());

        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            // do something with the SearchHit
            System.out.println(hit.toString());
        }
        return searchHits;
    }
}
