package services

import (
	"bufio"
	"context"
	"fmt"
	"log"
	"os"
	"time"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"

	"github.com/siddhant-vij/Note-Taking-App/utils"
)

func SqlQueries(uri string) {
	// Connect to MongoDB client
	client, _ := utils.ConnectToClient(uri)

	// Get the collection
	collection := utils.GetCollection(client, dbName, collectionName)

	// Close the MongoDB client connection
	defer utils.CloseClient(client)

	// SQL Queries Operations
	queryStr := ""
	outputFilePath := "output.txt"

	queryStr = "Find All Notes: "
	getAllNotes(collection, queryStr, outputFilePath)

	queryStr = "Find Notes Containing a Specific Tag (eg. \"tag5\"): "
	getNotesByTag(collection, queryStr, outputFilePath, "tag5")

	queryStr = "Find Notes Created After a Specific Date (eg. \"2023-04-18\"): "
	getNotesAfterDate(collection, queryStr, outputFilePath, "2023-04-18")

	queryStr = "Update the Tags for a Note (eg. for note with title: \"MongoDB Advanced\", change it to \"nosql\"): "
	updateTags(collection, queryStr, outputFilePath, "MongoDB Advanced", []string{"nosql"})

	queryStr = "Update one of the many Tags for a Note (eg. for note with title: \"MongoDB Advanced\", change \"nosql\" to \"non-relational\"): "
	updateSingleTag(collection, queryStr, outputFilePath, "MongoDB Advanced", "nosql", "non-relational")

	queryStr = "Add another tag for a Note (eg. for note with title: \"MongoDB Advanced\", add \"important\" tag): "
	addTag(collection, queryStr, outputFilePath, "MongoDB Advanced", "important")

	queryStr = "Add a list of tags for a Note (eg. for note with title: \"mongodb intermediate\", add \"tag1\", \"tag2\", \"tag3\"): "
	addTags(collection, queryStr, outputFilePath, "mongodb intermediate", []string{"tag1", "tag2", "tag3"})

	queryStr = "Update the title for a Note (eg. for note with title: \"mongodb intermediate\", change it to \"MongoDB Intermediate\"): "
	updateTitle(collection, queryStr, outputFilePath, "mongodb intermediate", "MongoDB Intermediate")

	queryStr = "Remove a Tag from a Note (eg. for note with title: \"MongoDB Intermediate\", remove \"tag3\"): "
	removeTag(collection, queryStr, outputFilePath, "MongoDB Intermediate", "tag3")

	queryStr = "Remove a list of Tags from a Note (eg. for note with title: \"MongoDB Intermediate\", remove \"tag1\" and \"tag7\"): "
	removeTags(collection, queryStr, outputFilePath, "MongoDB Intermediate", []string{"tag1", "tag7"})

	queryStr = "Remove all Tags from a Note (eg. for note with title: \"MongoDB Intermediate\", remove all tags): "
	removeAllTags(collection, queryStr, outputFilePath, "MongoDB Intermediate")

	queryStr = "Delete a Note (eg. for note with title: \"MongoDB Intermediate\", delete it): "
	deleteNote(collection, queryStr, outputFilePath, "MongoDB Intermediate")

	queryStr = "Find Notes by Partial Title (Case Insensitive Search) (eg. for note with title containing \"MongoDB\", find it): "
	getNotesByPartialTitle(collection, queryStr, outputFilePath, "MongoDB")

	queryStr = "Find Notes by Title (Case Sensitive Search) (eg. for note with title containing \"mongodb\", find it): "
	getNotesByTitle(collection, queryStr, outputFilePath, "mongodb")

	queryStr = "List Unique Tags Across All Notes: "
	getUniqueTags(collection, queryStr, outputFilePath)

	queryStr = "Delete Notes Newer Than a Specific Date (eg. for notes created after \"2023-12-02\", delete them): "
	deleteNotesAfterDate(collection, queryStr, outputFilePath, "2023-12-02")

	queryStr = "Sort Notes by Creation Date (Newest First): "
	sortNotesByCreationDateDesc(collection, queryStr, outputFilePath)

	queryStr = "Sort Notes by Title (Alphabetical Order): "
	sortNotesByTitle(collection, queryStr, outputFilePath)

	queryStr = "Sort Notes by count of tags (Descending Order): "
	sortNotesByCountOfTagsDesc(collection, queryStr, outputFilePath)

	queryStr = "Delete all duplicate notes from the collection: "
	deleteDuplicateNotes(collection, queryStr, outputFilePath)

	queryStr = "Retrieve notes that have been updated after their creation date: "
	getNotesUpdatedAfterCreationDate(collection, queryStr, outputFilePath)

	queryStr = "List all notes that contain at least 3 different tags: "
	getNotesWithThreeTags(collection, queryStr, outputFilePath)

	queryStr = "Find notes that do not have any tags associated with them: "
	getNotesWithoutTags(collection, queryStr, outputFilePath)

	queryStr = "Find notes that have been modified in the year 2024 so far: "
	getNotesModifiedInYear2024(collection, queryStr, outputFilePath)

	queryStr = "Aggregate notes by the month of creation and count the number of notes created each month: "
	getNotesCreatedEachMonth(collection, queryStr, outputFilePath)

	queryStr = "Identify notes with the word \"important\" in the title but without an \"important\" tag: "
	getNotesWithImprtantTitleButNoImportantTag(collection, queryStr, outputFilePath)
}

func getAllNotes(collection *mongo.Collection, queryStr, outputFilePath string) {
	count, err := collection.CountDocuments(
		context.TODO(),
		bson.M{})
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, count, outputFilePath)
}

func getNotesByTag(collection *mongo.Collection, queryStr, outputFilePath, tag string) {
	count, err := collection.CountDocuments(
		context.TODO(),
		bson.M{"tags": tag})
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, count, outputFilePath)
}

func getNotesAfterDate(collection *mongo.Collection, queryStr, outputFilePath, date string) {
	dateFmt, err := ParseDate(date)
	if err != nil {
		log.Fatal(err)
	}
	count, err := collection.CountDocuments(
		context.TODO(),
		bson.M{"created_on": bson.M{"$gt": dateFmt}})
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, count, outputFilePath)
}

func updateTags(collection *mongo.Collection, queryStr, outputFilePath, title string, tags []string) {
	update_result, err := collection.UpdateOne(
		context.TODO(),
		bson.M{"title": title},
		bson.M{"$set": bson.M{"tags": tags}},
		nil)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, update_result, outputFilePath)
}

func updateSingleTag(collection *mongo.Collection, queryStr, outputFilePath, title, oldTag, newTag string) {
	_, err := collection.UpdateOne(
		context.TODO(),
		bson.M{"title": title},
		bson.M{"$pull": bson.M{"tags": oldTag}},
	)
	if err != nil {
		log.Fatal(err)
	}

	updateResult, err := collection.UpdateOne(
		context.TODO(),
		bson.M{"title": title},
		bson.M{"$addToSet": bson.M{"tags": newTag}},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, updateResult, outputFilePath)
}

func addTag(collection *mongo.Collection, queryStr, outputFilePath, title, tag string) {
	updateResult, err := collection.UpdateOne(
		context.TODO(),
		bson.M{"title": title},
		bson.M{"$addToSet": bson.M{"tags": tag}},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, updateResult, outputFilePath)
}

func addTags(collection *mongo.Collection, queryStr, outputFilePath, title string, tags []string) {
	updateResult, err := collection.UpdateOne(
		context.TODO(),
		bson.M{"title": title},
		bson.M{"$addToSet": bson.M{"tags": bson.M{"$each": tags}}},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, updateResult, outputFilePath)
}

func updateTitle(collection *mongo.Collection, queryStr, outputFilePath, oldTitle, newTitle string) {
	updateResult, err := collection.UpdateOne(
		context.TODO(),
		bson.M{"title": oldTitle},
		bson.M{"$set": bson.M{"title": newTitle}},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, updateResult, outputFilePath)
}

func removeTag(collection *mongo.Collection, queryStr, outputFilePath, title, tag string) {
	updateResult, err := collection.UpdateOne(
		context.TODO(),
		bson.M{"title": title},
		bson.M{"$pull": bson.M{"tags": tag}},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, updateResult, outputFilePath)
}

func removeTags(collection *mongo.Collection, queryStr, outputFilePath, title string, tags []string) {
	updateResult, err := collection.UpdateOne(
		context.TODO(),
		bson.M{"title": title},
		bson.M{"$pull": bson.M{"tags": bson.M{"$in": tags}}},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, updateResult, outputFilePath)
}

func removeAllTags(collection *mongo.Collection, queryStr, outputFilePath, title string) {
	updateResult, err := collection.UpdateOne(
		context.TODO(),
		bson.M{"title": title},
		bson.M{"$set": bson.M{"tags": []string{}}},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, updateResult, outputFilePath)
}

func deleteNote(collection *mongo.Collection, queryStr, outputFilePath, title string) {
	deleteResult, err := collection.DeleteOne(
		context.TODO(),
		bson.M{"title": title},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, deleteResult, outputFilePath)
}

func getNotesByPartialTitle(collection *mongo.Collection, queryStr, outputFilePath, searchItem string) {
	count, err := collection.CountDocuments(
		context.TODO(),
		bson.M{"title": bson.M{"$regex": ".*" + searchItem + ".*", "$options": "i"}},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, count, outputFilePath)
}

func getNotesByTitle(collection *mongo.Collection, queryStr, outputFilePath, searchItem string) {
	count, err := collection.CountDocuments(
		context.TODO(),
		bson.M{"title": bson.M{"$regex": ".*" + searchItem + ".*"}},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, count, outputFilePath)
}

func getUniqueTags(collection *mongo.Collection, queryStr, outputFilePath string) {
	distinctResult, err := collection.Distinct(
		context.TODO(),
		"tags",
		bson.M{},
	)
	if err != nil {
		log.Fatal(err)
	}
	// ------- Special Pretty Printing -------
	file, err := os.OpenFile(outputFilePath, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		fmt.Println("Error opening file:", err)
		return
	}
	defer file.Close()

	writer := bufio.NewWriter(file)
	writer.WriteString(fmt.Sprintf("%s\n", queryStr))
	writer.WriteString("[\n")
	for i := 0; i < len(distinctResult); i++ {
		if i%2 == 0 {
			writer.WriteString(fmt.Sprintf("\t\"%s\",", distinctResult[i]))
		} else {
			writer.WriteString(fmt.Sprintf("\t\"%s\"\n", distinctResult[i]))
		}
	}
	writer.WriteString("]")
	writer.WriteString("\n\n\n\n")
	writer.Flush()
}

func deleteNotesAfterDate(collection *mongo.Collection, queryStr, outputFilePath, date string) {
	dateFmt, err := ParseDate(date)
	if err != nil {
		log.Fatal(err)
	}
	deleteResult, err := collection.DeleteMany(
		context.TODO(),
		bson.M{"created_on": bson.M{"$gt": dateFmt}},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, deleteResult, outputFilePath)
}

func sortNotesByCreationDateDesc(collection *mongo.Collection, queryStr, outputFilePath string) {
	cursor, err := collection.Find(
		context.TODO(),
		bson.M{},
		options.Find().SetSort(bson.D{{Key: "created_on", Value: -1}}).SetLimit(15),
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, cursor, outputFilePath)
}

func sortNotesByTitle(collection *mongo.Collection, queryStr, outputFilePath string) {
	cursor, err := collection.Find(
		context.TODO(),
		bson.M{},
		options.Find().SetSort(bson.D{{Key: "title", Value: 1}}).SetLimit(15),
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, cursor, outputFilePath)
}

func sortNotesByCountOfTagsDesc(collection *mongo.Collection, queryStr, outputFilePath string) {
	aggregate_result, err := collection.Aggregate(
		context.TODO(),
		mongo.Pipeline{
			{{Key: "$addFields", Value: bson.M{"tag_count": bson.M{"$size": "$tags"}}}},
			{{Key: "$sort", Value: bson.M{"tag_count": -1}}},
			{{Key: "$limit", Value: 15}},
		},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, aggregate_result, outputFilePath)
}

func deleteDuplicateNotes(collection *mongo.Collection, queryStr, outputFilePath string) {
	file, err := os.OpenFile(outputFilePath, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		fmt.Println("Error opening file:", err)
		return
	}
	defer file.Close()

	countB, err := collection.CountDocuments(
		context.TODO(),
		bson.M{},
	)
	if err != nil {
		log.Fatal(err)
	}

	writer := bufio.NewWriter(file)
	writer.WriteString(fmt.Sprintf("%s\n", queryStr))
	writer.WriteString(fmt.Sprintf("Count (Before deletion of duplicates): \n%d\n", countB))

	pipeline := mongo.Pipeline{
		{{Key: "$group",
			Value: bson.M{
				"_id": "$title",
				"uniqueIds": bson.M{
					"$addToSet": "$_id",
				},
				"count": bson.M{"$sum": 1},
			}}},
		{{Key: "$match",
			Value: bson.M{"count": bson.M{"$gt": 1}},
		}},
	}

	cursor, err := collection.Aggregate(context.TODO(), pipeline)
	if err != nil {
		log.Fatal(err)
	}

	var results []bson.M
	if err = cursor.All(context.TODO(), &results); err != nil {
		fmt.Println("Error decoding aggregation results:", err)
		return
	}

	for _, doc := range results {
		uniqueIds, ok := doc["uniqueIds"].(primitive.A)
		if !ok || len(uniqueIds) <= 1 {
			continue
		}

		idsToDelete := uniqueIds[:len(uniqueIds)-1]

		_, err := collection.DeleteMany(
			context.TODO(),
			bson.M{"_id": bson.M{"$in": idsToDelete}})
		if err != nil {
			fmt.Println("Error deleting duplicates:", err)
			continue
		}
	}

	countA, err := collection.CountDocuments(
		context.TODO(),
		bson.M{},
	)
	if err != nil {
		log.Fatal(err)
	}

	writer.WriteString(fmt.Sprintf("Count (Before deletion of duplicates): \n%d\n", countA))
	writer.WriteString("\n\n\n")
	writer.Flush()
}

func getNotesUpdatedAfterCreationDate(collection *mongo.Collection, queryStr, outputFilePath string) {
	cursor, err := collection.Find(
		context.TODO(),
		bson.M{"$expr": bson.M{"$gt": bson.A{"$last_updated_on", "$created_on"}}},
		options.Find().SetLimit(15),
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, cursor, outputFilePath)
}

func getNotesWithThreeTags(collection *mongo.Collection, queryStr, outputFilePath string) {
	count, err := collection.CountDocuments(
		context.TODO(),
		bson.M{"tags": bson.M{"$size": 3}},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, count, outputFilePath)
}

func getNotesWithoutTags(collection *mongo.Collection, queryStr, outputFilePath string) {
	count, err := collection.CountDocuments(
		context.TODO(),
		bson.M{"tags": bson.M{"$size": 0}},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, count, outputFilePath)
}

func getNotesModifiedInYear2024(collection *mongo.Collection, queryStr, outputFilePath string) {
	count, err := collection.CountDocuments(
		context.TODO(),
		bson.M{"last_updated_on": bson.M{"$gte": time.Date(2024, 1, 1, 0, 0, 0, 0, time.UTC)}},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, count, outputFilePath)
}

func getNotesCreatedEachMonth(collection *mongo.Collection, queryStr, outputFilePath string) {

	pipeline := mongo.Pipeline{
		{
			{
				Key: "$group",
				Value: bson.M{
					"_id": bson.M{
						"$dateToString": bson.M{
							"format": "%Y-%m",
							"date":   "$created_on",
						},
					},
					"count": bson.M{"$sum": 1},
				},
			},
		},
		{
			{
				Key:   "$sort",
				Value: bson.M{"_id": 1},
			},
		},
	}

	cursor, err := collection.Aggregate(context.TODO(), pipeline)
	if err != nil {
		log.Fatal(err)
	}
	defer cursor.Close(context.TODO())

	file, err := os.OpenFile(outputFilePath, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		fmt.Println("Error opening file:", err)
		return
	}
	defer file.Close()

	writer := bufio.NewWriter(file)
	writer.WriteString(fmt.Sprintf("%s\n", queryStr))
	content := "[\n"
	for cursor.Next(context.TODO()) {
		content += "\t{"
		var result bson.M
		err := cursor.Decode(&result)
		if err != nil {
			log.Fatal(err)
		}
		for k, v := range result {
			content += fmt.Sprintf("\t\t\"%s\": \"%v\",", k, v)
		}
		content += "\t},\n"
	}
	content += "]"
	writer.WriteString(content)
	writer.WriteString("\n\n\n\n")
	writer.Flush()
}

func getNotesWithImprtantTitleButNoImportantTag(collection *mongo.Collection, queryStr, outputFilePath string) {
	count, err := collection.CountDocuments(
		context.TODO(),
		bson.M{
			"$and": bson.A{
				bson.M{"title": bson.M{"$regex": "important", "$options": "i"}},
				bson.M{"tags": bson.M{"$nin": bson.A{"important"}}},
			},
		},
	)
	if err != nil {
		log.Fatal(err)
	}
	utils.PrintQueryResultToFile(queryStr, count, outputFilePath)
}

func ParseDate(dateString string) (time.Time, error) {
	layouts := []string{
		"2006-01-02",
		"2006-01-2",
		"2006-1-02",
		"2006-1-2",
	}
	for _, layout := range layouts {
		if date, err := time.Parse(layout, dateString); err == nil {
			return date, nil
		}
	}
	return time.Time{}, fmt.Errorf("invalid date format: %s", dateString)
}
