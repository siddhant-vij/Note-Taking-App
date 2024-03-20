import com.mongodb.client.AggregateIterable;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.io.PrintWriter;
import java.io.FileOutputStream;

public class SqlQueriesMain {
  static Logger root = (Logger) LoggerFactory
      .getLogger(Logger.ROOT_LOGGER_NAME);

  static {
    root.setLevel(Level.OFF);
  }

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-M-d");

  public static void main(String[] args) {
    // URI and database/collection names
    String uri = "mongodb://localhost:27017";
    String dbName = "noteTakingApp";
    String collectionName = "notes";

    // Connect to MongoDB client
    MongoClient client = CrudOperations.connectToClient(uri);

    // Get the collection
    MongoCollection<Document> collection = CrudOperations.getCollection(client, dbName, collectionName);

    // Execute SQL queries
    String queryStr = null;
    String outputFilePath = "output.txt";

    queryStr = "Find All Notes: ";
    getAllNotes(collection, queryStr, outputFilePath);

    queryStr = "Find Notes Containing a Specific Tag (eg. \"tag5\"): ";
    getNotesByTag(collection, queryStr, outputFilePath, "tag5");

    queryStr = "Find Notes Created After a Specific Date (eg. \"2023-04-18\"): ";
    getNotesAfterDate(collection, queryStr, outputFilePath, "2023-04-18");

    queryStr = "Update the Tags for a Note (eg. for note with title: \"MongoDB Advanced\", change it to \"nosql\"): ";
    updateTags(collection, queryStr, outputFilePath, "MongoDB Advanced", List.of("nosql"));

    queryStr = "Update one of the many Tags for a Note (eg. for note with title: \"MongoDB Advanced\", change \"nosql\" to \"non-relational\"): ";
    updateSingleTag(collection, queryStr, outputFilePath, "MongoDB Advanced", "nosql", "non-relational");

    queryStr = "Add another tag for a Note (eg. for note with title: \"MongoDB Advanced\", add \"important\" tag): ";
    addTag(collection, queryStr, outputFilePath, "MongoDB Advanced", "important");

    queryStr = "Add a list of tags for a Note (eg. for note with title: \"mongodb intermediate\", add \"tag1\", \"tag2\", \"tag3\"): ";
    addTags(collection, queryStr, outputFilePath, "mongodb intermediate", "tag1", "tag2", "tag3");

    queryStr = "Update the title for a Note (eg. for note with title: \"mongodb intermediate\", change it to \"MongoDB Intermediate\"): ";
    updateTitle(collection, queryStr, outputFilePath, "mongodb intermediate", "MongoDB Intermediate");

    queryStr = "Remove a Tag from a Note (eg. for note with title: \"MongoDB Intermediate\", remove \"tag3\"): ";
    removeTag(collection, queryStr, outputFilePath, "MongoDB Intermediate", "tag3");

    queryStr = "Remove a list of Tags from a Note (eg. for note with title: \"MongoDB Intermediate\", remove \"tag1\" and \"tag7\"): ";
    removeTags(collection, queryStr, outputFilePath, "MongoDB Intermediate", "tag1", "tag7");

    queryStr = "Remove all Tags from a Note (eg. for note with title: \"MongoDB Intermediate\", remove all tags): ";
    removeAllTags(collection, queryStr, outputFilePath, "MongoDB Intermediate");

    queryStr = "Delete a Note (eg. for note with title: \"MongoDB Intermediate\", delete it): ";
    deleteNote(collection, queryStr, outputFilePath, "MongoDB Intermediate");

    queryStr = "Find Notes by Partial Title (Case Insensitive Search) (eg. for note with title containing \"MongoDB\", find it): ";
    getNotesByPartialTitle(collection, queryStr, outputFilePath, "MongoDB");

    queryStr = "Find Notes by Title (Case Sensitive Search) (eg. for note with title containing \"mongodb\", find it): ";
    getNotesByTitle(collection, queryStr, outputFilePath, "mongodb");

    queryStr = "List Unique Tags Across All Notes: ";
    getUniqueTags(collection, queryStr, outputFilePath);

    queryStr = "Delete Notes Newer Than a Specific Date (eg. for notes created after \"2023-12-02\", delete them): ";
    deleteNotesAfterDate(collection, queryStr, outputFilePath, "2023-12-02");

    queryStr = "Sort Notes by Creation Date (Newest First): ";
    sortNotesByCreationDateDesc(collection, queryStr, outputFilePath);

    queryStr = "Sort Notes by Title (Alphabetical Order): ";
    sortNotesByTitle(collection, queryStr, outputFilePath);

    queryStr = "Sort Notes by count of tags (Descending Order): ";
    sortNotesByCountOfTagsDesc(collection, queryStr, outputFilePath);

    queryStr = "Delete all duplicate notes from the collection: ";
    deleteDuplicateNotes(collection, queryStr, outputFilePath);

    queryStr = "Retrieve notes that have been updated after their creation date: ";
    getNotesUpdatedAfterCreationDate(collection, queryStr, outputFilePath);

    queryStr = "List all notes that contain at least 3 different tags: ";
    getNotesWithThreeTags(collection, queryStr, outputFilePath);

    queryStr = "Find notes that do not have any tags associated with them: ";
    getNotesWithoutTags(collection, queryStr, outputFilePath);

    queryStr = "Find notes that have been modified in the year 2024 so far: ";
    getNotesModifiedInYear2024(collection, queryStr, outputFilePath);

    queryStr = "Aggregate notes by the month of creation and count the number of notes created each month: ";
    getNotesCreatedEachMonth(collection, queryStr, outputFilePath);

    queryStr = "Identify notes with the word \"important\" in the title but without an \"important\" tag: ";
    getNotesWithImprtantTitleButNoImportantTag(collection, queryStr, outputFilePath);

    // Close the MongoDB client connection
    CrudOperations.closeClient(client);
  }

  private static void getAllNotes(MongoCollection<Document> collection, String queryStr, String outputFilePath) {
    FindIterable<Document> queryResult = CrudOperations.executeQuery(collection,
        null,
        null,
        null,
        null);
    int count = queryResult.into(new ArrayList<>()).size();
    CrudOperations.printQueryResultToFile(queryStr, count, outputFilePath);
  }

  private static void getNotesByTag(MongoCollection<Document> collection, String queryStr, String outputFilePath,
      String tag) {
    FindIterable<Document> queryResult = CrudOperations.executeQuery(collection,
        Filters.all("tags", tag),
        null,
        null,
        null);
    int count = queryResult.into(new ArrayList<>()).size();
    CrudOperations.printQueryResultToFile(queryStr, count, outputFilePath);
  }

  private static void getNotesAfterDate(MongoCollection<Document> collection, String queryStr, String outputFilePath,
      String date) {
    LocalDate localDate = LocalDate.parse(date.strip(), formatter);
    LocalDateTime localDateTime = localDate.atStartOfDay();
    Date dateFmt = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    FindIterable<Document> queryResult = CrudOperations.executeQuery(collection,
        Filters.gt("created_on", dateFmt),
        null,
        null,
        null);
    int count = queryResult.into(new ArrayList<>()).size();
    CrudOperations.printQueryResultToFile(queryStr, count, outputFilePath);
  }

  private static void updateTags(MongoCollection<Document> collection, String queryStr, String outputFilePath,
      String title, List<String> tags) {
    UpdateResult result = CrudOperations.updateDocument(collection,
        Filters.eq("title", title),
        Updates.combine(Updates.set("tags", tags)),
        null);
    CrudOperations.printQueryResultToFile(queryStr, result, outputFilePath);
  }

  private static void updateSingleTag(MongoCollection<Document> collection, String queryStr, String outputFilePath,
      String title, String oldTag, String newTag) {
    FindIterable<Document> findRes = CrudOperations.executeQuery(collection,
        Filters.eq("title", title),
        null,
        null,
        null);
    Document doc = findRes.first();
    List<String> tags = doc.getList("tags", String.class);
    tags.removeIf(tag -> tag.equals(oldTag));
    if (!tags.contains(newTag)) {
      tags.add(newTag);
    }
    UpdateResult result = CrudOperations.updateDocument(collection,
        Filters.eq("title", title),
        Updates.combine(Updates.set("tags", tags)),
        null);
    CrudOperations.printQueryResultToFile(queryStr, result, outputFilePath);
  }

  private static void addTag(MongoCollection<Document> collection, String queryStr, String outputFilePath,
      String title,
      String tag) {
    UpdateResult result = CrudOperations.updateDocument(collection,
        Filters.eq("title", title),
        Updates.addToSet("tags", tag),
        null);
    CrudOperations.printQueryResultToFile(queryStr, result, outputFilePath);
  }

  private static void addTags(MongoCollection<Document> collection, String queryStr, String outputFilePath,
      String title, String... tags) {
    UpdateResult result = CrudOperations.updateDocument(collection,
        Filters.eq("title", title),
        Updates.addToSet("tags", new Document("$each", Arrays.asList(tags))),
        null);
    CrudOperations.printQueryResultToFile(queryStr, result, outputFilePath);
  }

  private static void updateTitle(MongoCollection<Document> collection, String queryStr, String outputFilePath,
      String title, String newTitle) {
    UpdateResult result = CrudOperations.updateDocument(collection,
        Filters.eq("title", title),
        Updates.set("title", newTitle),
        null);
    CrudOperations.printQueryResultToFile(queryStr, result, outputFilePath);
  }

  private static void removeTag(MongoCollection<Document> collection, String queryStr, String outputFilePath,
      String title, String tag) {
    FindIterable<Document> findTitleRes = CrudOperations.executeQuery(collection,
        Filters.eq("title", title),
        null,
        null,
        null);
    Document doc = findTitleRes.first();
    List<String> tags = doc.getList("tags", String.class);
    tags.removeIf(t -> t.equals(tag));

    UpdateResult result = CrudOperations.updateDocument(collection,
        Filters.eq("title", title),
        Updates.set("tags", tags),
        null);
    CrudOperations.printQueryResultToFile(queryStr, result, outputFilePath);
  }

  private static void removeTags(MongoCollection<Document> collection, String queryStr, String outputFilePath,
      String title, String... tags) {
    FindIterable<Document> findTitleRes = CrudOperations.executeQuery(collection,
        Filters.eq("title", title),
        null,
        null,
        null);
    Document doc = findTitleRes.first();
    List<String> tagsList = doc.getList("tags", String.class);
    for (String tag : tags) {
      tagsList.removeIf(t -> t.equals(tag));
    }

    UpdateResult result = CrudOperations.updateDocument(collection,
        Filters.eq("title", title),
        Updates.set("tags", tagsList),
        null);
    CrudOperations.printQueryResultToFile(queryStr, result, outputFilePath);
  }

  private static void removeAllTags(MongoCollection<Document> collection, String queryStr, String outputFilePath,
      String title) {
    UpdateResult result = CrudOperations.updateDocument(collection,
        Filters.eq("title", title),
        Updates.unset("tags"),
        null);
    CrudOperations.printQueryResultToFile(queryStr, result, outputFilePath);
  }

  private static void deleteNote(MongoCollection<Document> collection, String queryStr, String outputFilePath,
      String title) {
    DeleteResult result = CrudOperations.deleteDocument(collection,
        Filters.eq("title", title),
        null);
    CrudOperations.printQueryResultToFile(queryStr, result, outputFilePath);
  }

  private static void getNotesByPartialTitle(MongoCollection<Document> collection, String queryStr,
      String outputFilePath, String searchItem) {
    FindIterable<Document> queryResult = CrudOperations.executeQuery(collection,
        Filters.regex("title", Pattern.compile(".*" + searchItem + ".*", Pattern.CASE_INSENSITIVE)),
        null,
        null,
        null);
    int count = queryResult.into(new ArrayList<>()).size();
    CrudOperations.printQueryResultToFile(queryStr, count, outputFilePath);
  }

  private static void getNotesByTitle(MongoCollection<Document> collection, String queryStr, String outputFilePath,
      String searchItem) {
    FindIterable<Document> queryResult = CrudOperations.executeQuery(collection,
        Filters.regex("title", Pattern.compile(".*" + searchItem + "*.")),
        null,
        null,
        null);
    int count = queryResult.into(new ArrayList<>()).size();
    CrudOperations.printQueryResultToFile(queryStr, count, outputFilePath);
  }

  private static void getUniqueTags(MongoCollection<Document> collection, String queryStr, String outputFilePath) {
    DistinctIterable<String> queryResult = collection.distinct("tags", String.class);
    List<String> tags = queryResult.into(new ArrayList<>());
    // ------- Special Pretty Printing -------
    try (PrintWriter pw = new PrintWriter(new FileOutputStream(outputFilePath, true))) {
      pw.println(queryStr);
      pw.println("[");
      for (int i = 0; i < tags.size(); i++) {
        if (i % 2 == 0) {
          pw.print("\t\"" + tags.get(i) + "\",");
        } else {
          pw.println("\t\"" + tags.get(i) + "\"");
        }
      }
      pw.println("]");
      pw.println("\n\n");
    } catch (Exception e) {
      System.out.println("Printing Error: " + e.getMessage());
    }
  }

  private static void deleteNotesAfterDate(MongoCollection<Document> collection, String queryStr,
      String outputFilePath,
      String date) {
    LocalDate localDate = LocalDate.parse(date.strip(), formatter);
    LocalDateTime localDateTime = localDate.atStartOfDay();
    Date dateFmt = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    DeleteResult result = CrudOperations.deleteMultipleDocuments(collection,
        Filters.gt("created_on", dateFmt),
        null);
    CrudOperations.printQueryResultToFile(queryStr, result, outputFilePath);
  }

  private static void sortNotesByCreationDateDesc(MongoCollection<Document> collection, String queryStr,
      String outputFilePath) {
    FindIterable<Document> queryResult = CrudOperations.executeQuery(collection,
        null,
        Sorts.descending("created_on"),
        null,
        15);
    List<Document> docs = queryResult.into(new ArrayList<>());
    CrudOperations.printQueryResultToFile(queryStr, docs, outputFilePath);
  }

  private static void sortNotesByTitle(MongoCollection<Document> collection, String queryStr, String outputFilePath) {
    FindIterable<Document> queryResult = CrudOperations.executeQuery(collection,
        null,
        Sorts.ascending("title"),
        null,
        15);
    List<Document> docs = queryResult.into(new ArrayList<>());
    CrudOperations.printQueryResultToFile(queryStr, docs, outputFilePath);
  }

  private static void sortNotesByCountOfTagsDesc(MongoCollection<Document> collection, String queryStr,
      String outputFilePath) {
    AggregateIterable<Document> result = CrudOperations.executeAggregateQuery(collection,
        Arrays.asList(
            Aggregates.addFields(new Field<Document>("tagsCount", new Document("$size", "$tags"))),
            Aggregates.sort(Sorts.descending("tagsCount")),
            Aggregates.limit(15)));
    List<Document> docs = result.into(new ArrayList<>());
    CrudOperations.printQueryResultToFile(queryStr, docs, outputFilePath);
  }

  private static void deleteDuplicateNotes(MongoCollection<Document> collection, String queryStr,
      String outputFilePath) {
    try (PrintWriter pw = new PrintWriter(new FileOutputStream(outputFilePath, true))) {
      long countBefore = collection.countDocuments();
      pw.println(queryStr);
      pw.println("Count (Before deletion of duplicates):\n" + countBefore);

      // Aggregate query to find duplicates
      List<Bson> pipeline = Arrays.asList(
          Aggregates.group("$title",
              Accumulators.addToSet("uniqueIds", "$_id"),
              Accumulators.sum("count", 1)),
          Aggregates.match(Filters.gt("count", 1)));

      collection.aggregate(pipeline).forEach(document -> {
        List<Object> ids = document.getList("uniqueIds", Object.class);
        if (ids.size() > 1) {
          // Remove one id to keep
          ids.remove(ids.size() - 1);
          // Delete the remaining duplicates
          collection.deleteMany(Filters.in("_id", ids));
        }
      });

      long countAfter = collection.countDocuments();
      pw.println("Count (After deletion of duplicates):\n" + countAfter);
      pw.println("\n\n");
    } catch (Exception e) {
      System.out.println("Error during deletion of duplicate notes: " + e.getMessage());
    }
  }

  private static void getNotesUpdatedAfterCreationDate(MongoCollection<Document> collection, String queryStr,
      String outputFilePath) {
    FindIterable<Document> queryResult = CrudOperations.executeQuery(collection,
        Filters.expr(new Document("$gt",
            Arrays.asList("$last_updated_on",
                "$created_on"))),
        null,
        null,
        15);
    List<Document> docs = queryResult.into(new ArrayList<>());
    CrudOperations.printQueryResultToFile(queryStr, docs, outputFilePath);
  }

  private static void getNotesWithThreeTags(MongoCollection<Document> collection, String queryStr,
      String outputFilePath) {
    FindIterable<Document> queryResult = CrudOperations.executeQuery(collection,
        Filters.size("tags", 3),
        null,
        null,
        null);
    int count = queryResult.into(new ArrayList<>()).size();
    CrudOperations.printQueryResultToFile(queryStr, count, outputFilePath);
  }

  private static void getNotesWithoutTags(MongoCollection<Document> collection, String queryStr,
      String outputFilePath) {
    FindIterable<Document> queryResult = CrudOperations.executeQuery(collection,
        Filters.size("tags", 0),
        null,
        null,
        null);
    int count = queryResult.into(new ArrayList<>()).size();
    CrudOperations.printQueryResultToFile(queryStr, count, outputFilePath);
  }

  private static void getNotesModifiedInYear2024(MongoCollection<Document> collection, String queryStr,
      String outputFilePath) {
    String date = "2024-01-01";
    LocalDate localDate = LocalDate.parse(date.strip(), formatter);
    LocalDateTime localDateTime = localDate.atStartOfDay();
    Date dateFmt = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    FindIterable<Document> queryResult = CrudOperations.executeQuery(collection,
        Filters.gte("last_updated_on", dateFmt),
        null,
        null,
        null);
    int count = queryResult.into(new ArrayList<>()).size();
    CrudOperations.printQueryResultToFile(queryStr, count, outputFilePath);
  }

  private static void getNotesCreatedEachMonth(MongoCollection<Document> collection, String queryStr,
      String outputFilePath) {
    AggregateIterable<Document> result = CrudOperations.executeAggregateQuery(collection,
        Arrays.asList(
            Aggregates.group(
                new Document("_id",
                    new Document("$dateToString", new Document("format", "%Y-%m").append("date", "$created_on"))),
                Accumulators.sum("count", 1)),
            Aggregates.sort(Sorts.ascending("_id"))));
    List<Document> results = result.into(new ArrayList<>());
    try (PrintWriter pw = new PrintWriter(new FileOutputStream(outputFilePath, true))) {
      pw.println(queryStr);
      pw.println("[");
      for (Document doc : results) {
        pw.println("\t" + doc.toJson());
      }
      pw.println("]");
      pw.println("\n\n");
    } catch (Exception e) {
      System.out.println("Printing Error: " + e.getMessage());
    }
  }

  private static void getNotesWithImprtantTitleButNoImportantTag(MongoCollection<Document> collection,
      String queryStr,
      String outputFilePath) {
    FindIterable<Document> queryResult = CrudOperations.executeQuery(collection,
        Filters.and(
            Filters.regex("title", "important",
                "i"),
            Filters.nin("tags",
                "important")),
        null,
        null,
        null);
    int count = queryResult.into(new ArrayList<>()).size();
    CrudOperations.printQueryResultToFile(queryStr, count, outputFilePath);
  }
}
