package com.nsm.common.elasticsearch;


import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

/**
 * Created by nieshuming on 2018/6/13.
 */
public class ElasticSearchClientTest {

    public static void main(String[] args) {

        TransportClient client = ElasticSearchUtil.getClient();
        SearchResponse response = client.prepareSearch("datacenter_org_page")
                .setTypes("type")
                .setQuery(QueryBuilders.matchQuery("pageName", "天天"))
                .setSize(10)
                .get();
        for(SearchHit hit : response.getHits().getHits()){
            System.out.println(hit.getSourceAsMap());
        }
    }
}
