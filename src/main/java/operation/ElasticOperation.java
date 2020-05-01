package operation;

import basic.HtmlPage;
import basic.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ElasticConfig;
import connection.ElasticConnection;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.plugins.SearchPlugin;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchModule;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;


public class ElasticOperation {
    private static RestHighLevelClient restHighLevelClient;
    private static  RequestOptions requestOptions=RequestOptions.DEFAULT;
    private static  IndexRequest indexRequest;
    private static   Map<String, Object> dataMap;
    private static    IndexResponse response;
    private static ObjectMapper objectMapper = new ObjectMapper();
    public  synchronized RestHighLevelClient makeConnection() {
        if(restHighLevelClient == null) {
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(ElasticConfig.HOST, ElasticConfig.PORT_ONE, ElasticConfig.SCHEME),
                            new HttpHost(ElasticConfig.HOST, ElasticConfig.PORT_TWO, ElasticConfig.SCHEME)));
        }

        return restHighLevelClient;
    }

    public  synchronized void closeConnection() throws IOException {
        restHighLevelClient.close();
        restHighLevelClient = null;
    }
    public  Person insertPerson(Person person){
        person.setPersonId(UUID.randomUUID().toString());
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("personId", person.getPersonId());
        dataMap.put("name", person.getName());
        IndexRequest indexRequest = new IndexRequest(ElasticConfig.INDEX, ElasticConfig.TYPE, person.getPersonId())
                .source(dataMap);
        try {
            RequestOptions requestOptions=RequestOptions.DEFAULT;
            IndexResponse response = restHighLevelClient.index(indexRequest,requestOptions);
        } catch(ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (java.io.IOException ex){
            ex.getLocalizedMessage();
        }
        return person;
    }



    public  void insertHtml(Document doc){
        dataMap = new HashMap<String, Object>();
        dataMap.put("url", doc.baseUri());
        dataMap.put("title", doc.title());
        dataMap.put("body", doc.body().text());
         indexRequest = new IndexRequest(ElasticConfig.INDEX, ElasticConfig.TYPE, doc.baseUri())
                .source(dataMap);
        try {
            response = restHighLevelClient.index(indexRequest,requestOptions);
            System.out.println(doc.baseUri()+" insert!!!!");
        } catch(ElasticsearchException e) {
            e.printStackTrace();
        } catch (java.io.IOException ex){
            ex.printStackTrace();
        }

    }

    public List<HtmlPage> searchQuery(String q){

        String query="{\"query\":{\"bool\":{\"must\":{\"multi_match\":{\"query\":"+'"'+q+'"'+",\"fields\":[\"title^3\",\"body\"],\"minimum_should_match\":\"70%\"}},\"should\":{\"multi_match\":{\"query\":"+'"'+q+'"'+",\"fields\":[\"title^3\",\"body\"],\"type\":\"phrase\"}}}}}";
        List<SearchPlugin> lb=new ArrayList<>();
        List<HtmlPage> htmlPages=new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SearchModule searchModule = new SearchModule(Settings.EMPTY, false, lb);
        try (XContentParser parser = XContentFactory.xContent(XContentType.JSON).createParser(new NamedXContentRegistry(searchModule
                .getNamedXContents()),null, query)) {
            searchSourceBuilder.parseXContent(parser);
            searchRequest.source(searchSourceBuilder);
            SearchResponse s=  restHighLevelClient.search(searchRequest,requestOptions);
            System.out.println(s.getHits().getMaxScore());
            SearchHit[] results = s.getHits().getHits();
            for (SearchHit hit:results){

                if (hit.getSourceAsMap() != null) {
                    System.out.println(hit.getScore());
                 htmlPages.add(   objectMapper.convertValue(hit.getSourceAsMap(), HtmlPage.class));
                }
            }
            System.out.println(222);
        }catch (Exception e){
            e.printStackTrace();
        }
        return htmlPages;
    }








    public  Person getPersonById(String id){
        GetRequest getPersonRequest = new GetRequest(ElasticConfig.INDEX, ElasticConfig.TYPE, id);
        GetResponse getResponse = null;
        try {
            RequestOptions requestOptions=RequestOptions.DEFAULT;
            getResponse = restHighLevelClient.get(getPersonRequest,requestOptions);
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }
        return getResponse != null ?
                objectMapper.convertValue(getResponse.getSourceAsMap(), Person.class) : null;
    }

    public  Person updatePersonById(String id, Person person){
        UpdateRequest updateRequest = new UpdateRequest(ElasticConfig.INDEX, ElasticConfig.TYPE, id)
                .fetchSource(true);    // Fetch Object after its update
        try {
            RequestOptions requestOptions=RequestOptions.DEFAULT;
            String personJson = objectMapper.writeValueAsString(person);
            updateRequest.doc(personJson, XContentType.JSON);
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest,requestOptions);
            return objectMapper.convertValue(updateResponse.getGetResult().sourceAsMap(), Person.class);
        }catch (JsonProcessingException e){
            e.getMessage();
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }
        System.out.println("Unable to update person");
        return null;
    }

    public  void deletePersonById(String id) {
        DeleteRequest deleteRequest = new DeleteRequest(ElasticConfig.INDEX, ElasticConfig.TYPE, id);
        try {
            RequestOptions requestOptions=RequestOptions.DEFAULT;
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest,requestOptions);
        } catch (java.io.IOException e){
            e.getLocalizedMessage();
        }
    }
}
