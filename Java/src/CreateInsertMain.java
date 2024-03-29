import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import java.nio.file.*;
import java.io.IOException;

public class CreateInsertMain {
  static Logger root = (Logger) LoggerFactory
      .getLogger(Logger.ROOT_LOGGER_NAME);

  static {
    root.setLevel(Level.OFF);
  }

  public static void main(String[] args) {
    String flag = null;
    if (args.length > 1) {
      System.out.println("Usage: Add only one flag: --c or --j\n\t--c for CSV\n\t--j for JSON");
      System.exit(1);
    } else if (args.length == 0) {
      flag = "--c";
    } else {
      flag = args[0];
    }

    DataReader reader = null;
    if (flag.equals("--c")) {
      // Read notes from Csv file
      reader = new CsvReader();
      reader.readNotes("Problem/input_data.csv");
    } else {
      // Read notes from Json file
      reader = new JsonReader();
      reader.readNotes("Problem/input_data.json");
    }

    // URI and database/collection names
    String uri = "mongodb://localhost:27017";
    String dbName = "noteTakingApp";
    String collectionName = "notes";

    // Connect to MongoDB client
    MongoClient client = CrudOperations.connectToClient(uri);

    // Drop the database - before starting
    CrudOperations.dropDatabase(client, dbName);

    // Get the collection
    MongoCollection<Document> collection = CrudOperations.getCollection(client, dbName, collectionName);

    // Insert documents into the collection
    for (Note note : reader.getNotes()) {
      Document document = new Document();
      document.append("title", note.getTitle());
      document.append("content", note.getContent());
      document.append("tags", note.getTags());
      document.append("created_on", note.getCreatedOn());
      document.append("last_updated_on", note.getLastUpdatedOn());

      // Insert document
      CrudOperations.insertDocument(collection, document);
    }

    // Close the MongoDB client connection
    CrudOperations.closeClient(client);

    // Temp: Make the output file empty
    String outputFilePath = "output.txt";
    try {
      Files.writeString(Paths.get(outputFilePath), "");
    } catch (IOException e) {
      System.out.println("Error writing output file: " + e.getMessage());
    }
  }
}
