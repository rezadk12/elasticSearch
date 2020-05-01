package connection;

import config.ElasticConfig;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class ElasticConnection {
    private static RestHighLevelClient restHighLevelClient;
    public static synchronized RestHighLevelClient makeConnection() {
        if(restHighLevelClient == null) {
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(ElasticConfig.HOST, ElasticConfig.PORT_ONE, ElasticConfig.SCHEME),
                            new HttpHost(ElasticConfig.HOST, ElasticConfig.PORT_TWO, ElasticConfig.SCHEME)));
        }

        return restHighLevelClient;
    }

    public static synchronized void closeConnection() throws IOException {
        restHighLevelClient.close();
        restHighLevelClient = null;
    }

}
