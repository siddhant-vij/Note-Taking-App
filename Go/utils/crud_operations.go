package utils

import (
	"bufio"
	"context"
	"fmt"
	"log"
	"os"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

func ConnectToClient(uri string) (*mongo.Client, error) {
	client, err := mongo.Connect(context.TODO(), options.Client().ApplyURI(uri))
	if err != nil {
		return nil, fmt.Errorf("connection error: %v", err)
	}
	return client, nil
}

func CloseClient(client *mongo.Client) {
	if err := client.Disconnect(context.TODO()); err != nil {
		log.Printf("Error disconnecting from MongoDB: %v", err)
	}
}

func DropDatabase(client *mongo.Client, dbName string) {
	db := client.Database(dbName)
	if err := db.Drop(context.TODO()); err != nil {
		log.Printf("Drop database error: %v", err)
	}
}

func GetCollection(client *mongo.Client, dbName string, collectionName string) *mongo.Collection {
	return client.Database(dbName).Collection(collectionName)
}

func InsertDocument(collection *mongo.Collection, document interface{}) {
	_, err := collection.InsertOne(context.TODO(), document)
	if err != nil {
		log.Printf("Insert document error: %v", err)
	}
}

func writeToOutputFile(queryStr, content, filePath string) {
	file, err := os.OpenFile(filePath, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		fmt.Println("Error opening file:", err)
		return
	}
	defer file.Close()

	writer := bufio.NewWriter(file)
	_, _ = writer.WriteString(fmt.Sprintf("%s\n%s\n\n\n\n", queryStr, content))
	writer.Flush()
}

func PrintQueryResultToFile(queryStr string, result interface{}, filePath string) {
	var content string

	switch r := result.(type) {
	case int64:
		content = fmt.Sprintf("Count: %d", r)
	case *mongo.Cursor:
		content = "[\n"
		for r.Next(context.TODO()) {
			content += "\t{\n"
			var result bson.M
			err := r.Decode(&result)
			if err != nil {
				log.Fatal(err)
			}
			for k, v := range result {
				content += fmt.Sprintf("\t\t\"%s\": \"%v\",\n", k, v)
			}
			content += "\t},\n"
		}
		content += "]"
	case *mongo.UpdateResult:
		content = fmt.Sprintf("{\n\t\"acknowledged\": %v,\n\t\"insertedId\": \"%v\",\n\t\"matchedCount\": %d,\n\t\"modifiedCount\": %d,\n\t\"upsertedCount\": 0\n}",
			r.MatchedCount > 0, r.UpsertedID, r.MatchedCount, r.ModifiedCount)
	case *mongo.DeleteResult:
		content = fmt.Sprintf("AcknowledgedDeleteResult{deletedCount=%d}", r.DeletedCount)
	default:
		content = "Unsupported result type"
	}

	writeToOutputFile(queryStr, content, filePath)
}
