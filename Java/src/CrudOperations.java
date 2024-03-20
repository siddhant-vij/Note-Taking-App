import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.PrintWriter;
import java.util.List;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class CrudOperations {
  // Connect to MongoDB client
  public static MongoClient connectToClient(String uri) {
    try {
      return MongoClients.create(uri);
    } catch (Exception e) {
      System.out.println("Connection Error: " + e.getMessage());
      return null;
    }
  }

  // Close the MongoDB client connection
  public static void closeClient(MongoClient client) {
    try {
      client.close();
    } catch (Exception e) {
      System.out.println("Close Client Error: " + e.getMessage());
    }
  }

  // Drop the database
  public static void dropDatabase(MongoClient client, String dbName) {
    try {
      MongoDatabase database = client.getDatabase(dbName);
      database.drop();
    } catch (Exception e) {
      System.out.println("Error dropping database '" + dbName + "': " + e.getMessage());
    }
  }

  // Retrieve a collection
  public static MongoCollection<Document> getCollection(MongoClient client, String dbName, String collectionName) {
    try {
      MongoDatabase database = client.getDatabase(dbName);
      return database.getCollection(collectionName);
    } catch (Exception e) {
      System.out.println("Get Collection Error: " + e.getMessage());
      return null;
    }
  }

  // Insert a document
  public static void insertDocument(MongoCollection<Document> collection, Document document) {
    try {
      collection.insertOne(document);
    } catch (Exception e) {
      System.out.println("Insert Document Error: " + e.getMessage());
    }
  }

  // Execute a find query
  public static FindIterable<Document> executeQuery(MongoCollection<Document> collection, Bson filter, Bson sort,
      Bson projection, Integer limit) {
    try {
      FindIterable<Document> queryResult = collection.find();
      if (filter != null) {
        queryResult = queryResult.filter(filter);
      }
      if (sort != null) {
        queryResult.sort(sort);
      }
      if (projection != null) {
        queryResult.projection(projection);
      }
      if (limit != null && limit > 0) {
        queryResult.limit(limit);
      }
      return queryResult;
    } catch (Exception e) {
      System.out.println("Query Execution Error: " + e.getMessage());
      return null;
    }
  }

  // Execute an aggregate query
  public static AggregateIterable<Document> executeAggregateQuery(MongoCollection<Document> collection,
      List<Bson> pipeline) {
    try {
      AggregateIterable<Document> queryResult = collection.aggregate(pipeline);
      return queryResult;
    } catch (Exception e) {
      System.out.println("Aggregate Query Execution Error: " + e.getMessage());
      return null;
    }
  }

  // Update one document with more options
  public static UpdateResult updateDocument(MongoCollection<Document> collection, Bson filter, Bson update,
      UpdateOptions options) {
    try {
      if (options == null) {
        return collection.updateOne(filter, update);
      } else {
        return collection.updateOne(filter, update, options);
      }
    } catch (Exception e) {
      System.out.println("Update Document Error: " + e.getMessage());
      return null;
    }
  }

  // Update multiple documents with more options
  public static UpdateResult updateMultipleDocuments(MongoCollection<Document> collection, Bson filter, Bson update,
      UpdateOptions options) {
    try {
      if (options == null) {
        return collection.updateMany(filter, update);
      } else {
        return collection.updateMany(filter, update, options);
      }
    } catch (Exception e) {
      System.out.println("Update Multiple Documents Error: " + e.getMessage());
      return null;
    }
  }

  // Delete one document with options
  public static DeleteResult deleteDocument(MongoCollection<Document> collection, Bson filter, DeleteOptions options) {
    try {
      if (options == null) {
        return collection.deleteOne(filter);
      } else {
        return collection.deleteOne(filter, options);
      }
    } catch (Exception e) {
      System.out.println("Delete Document Error: " + e.getMessage());
      return null;
    }
  }

  // Delete multiple documents with options
  public static DeleteResult deleteMultipleDocuments(MongoCollection<Document> collection, Bson filter,
      DeleteOptions options) {
    try {
      if (options == null) {
        return collection.deleteMany(filter);
      } else {
        return collection.deleteMany(filter, options);
      }
    } catch (Exception e) {
      System.out.println("Delete Multiple Documents Error: " + e.getMessage());
      return null;
    }
  }

  private static void writeToOutputFile(String queryStr, String content, String filePath) {
    try (PrintWriter pw = new PrintWriter(new FileOutputStream(filePath, true))) {
      pw.println(queryStr);
      pw.println(content);
      pw.println("\n\n");
    } catch (FileNotFoundException e) {
      System.out.println("File Not Found Error: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("Printing Error: " + e.getMessage());
    }
  }

  // For count of documents
  public static void printQueryResultToFile(String queryStr, int countOfDocuments, String filePath) {
    writeToOutputFile(queryStr, "Count: " + countOfDocuments, filePath);
  }

  // For a list of documents
  public static void printQueryResultToFile(String queryStr, List<Document> documents, String filePath) {
    StringBuilder content = new StringBuilder();
    content.append("[").append(System.lineSeparator());
    for (Document doc : documents) {
      content.append("  {").append(System.lineSeparator());
      for (String key : doc.keySet()) {
        content.append(String.format("    %s: %s,", key, doc.get(key).toString()));
        content.append(System.lineSeparator());
      }
      content.append("  },").append(System.lineSeparator());
    }
    content.append("]");
    writeToOutputFile(queryStr, content.toString(), filePath);
  }

  // For FindIterable<Document>
  public static void printQueryResultToFile(String queryStr, FindIterable<Document> documents, String filePath) {
    StringBuilder content = new StringBuilder();
    for (Document doc : documents) {
      content.append(doc.toJson()).append(System.lineSeparator());
    }
    writeToOutputFile(queryStr, content.toString(), filePath);
  }

  // For UpdateResult
  public static void printQueryResultToFile(String queryStr, UpdateResult result, String filePath) {
    String content = String.format("{%n" +
        "  acknowledged: %s,%n" +
        "  insertedId: %s,%n" +
        "  matchedCount: %d,%n" +
        "  modifiedCount: %d,%n" +
        "  upsertedCount: %d,%n" +
        "}",
        result.wasAcknowledged(),
        result.getUpsertedId() == null ? "null" : result.getUpsertedId().toString(),
        result.getMatchedCount(),
        result.getModifiedCount(),
        0);
    writeToOutputFile(queryStr, content, filePath);
  }

  // For DeleteResult
  public static void printQueryResultToFile(String queryStr, DeleteResult result, String filePath) {
    writeToOutputFile(queryStr, result.toString(), filePath);
  }
}
