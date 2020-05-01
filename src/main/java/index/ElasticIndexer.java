package index;

import basic.Person;
import operation.ElasticOperation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class ElasticIndexer {

    public static void main(String[] args) throws IOException {

        ElasticOperation elasticOperation=new ElasticOperation();
        elasticOperation.makeConnection();

        File input ;
        Document doc;


        elasticOperation.searchQuery("");


        for(int k=1971;k<2003;k++) {

            input = new File("D:\\eclipse java EE oxyjen version\\eclipse\\exe\\Searcher\\Repository\\"+k+".html");
            doc = Jsoup.parse(input, "UTF-8", k+".html");
            elasticOperation.insertHtml(doc);
        }












/*        System.out.println("Inserting a new Person with name Shubham...");
        Person person = new Person();
        person.setName("shabnam");
        person = elasticOperation.insertPerson(person);
        System.out.println("Person inserted --> " + person);*/

/*        System.out.println("Changing name to `Shubham Aggarwal`...");
        person.setName("Shubham Aggarwal");
        elasticOperation.updatePersonById(person.getPersonId(), person);
        System.out.println("Person updated  --> " + person);

        System.out.println("Getting Shubham...");
        Person personFromDB = elasticOperation.getPersonById(person.getPersonId());
        System.out.println("Person from DB  --> " + personFromDB);

        System.out.println("Deleting Shubham...");
        elasticOperation.deletePersonById(personFromDB.getPersonId());
        System.out.println("Person Deleted");*/

        elasticOperation.closeConnection();
    }
}
