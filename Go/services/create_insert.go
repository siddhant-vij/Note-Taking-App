package services

import (
	"os"

	"github.com/siddhant-vij/Note-Taking-App/models"
	"github.com/siddhant-vij/Note-Taking-App/utils"
)

const (
	dbName         = "noteTakingApp"
	collectionName = "notes"
)

func CreateInsert(uri string, notes []models.Note) {
	// Connect to MongoDB client
	client, _ := utils.ConnectToClient(uri)

	// Drop the database - before starting
	utils.DropDatabase(client, dbName)

	// Get the collection
	collection := utils.GetCollection(client, dbName, collectionName)

	// Insert documents into the collection
	for _, note := range notes {
		utils.InsertDocument(collection, note)
	}

	// Close the MongoDB client connection
	utils.CloseClient(client)

	// Temp: Make the output file empty
	outputFile, _ := os.Create("output.txt")
	outputFile.Close()
}
