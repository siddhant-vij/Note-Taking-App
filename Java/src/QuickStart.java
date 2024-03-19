import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class QuickStart {
  static Logger root = (Logger) LoggerFactory
      .getLogger(Logger.ROOT_LOGGER_NAME);

  static {
    root.setLevel(Level.OFF);
  }

  public static void main(String[] args) {

    String uri = "mongodb://localhost:27017";

    try (MongoClient mongoClient = MongoClients.create(uri)) {
      MongoDatabase database = mongoClient.getDatabase("noteTakingApp");
      MongoCollection<Document> collection = database.getCollection("notes");

      Document doc = collection.find(eq("title", "MongoDB Basics important Mong")).first();
      if (doc != null) {
        System.out.println(doc.toJson());
      } else {
        System.out.println("No matching documents found.");
      }
    }
  }
}