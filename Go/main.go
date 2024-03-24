package main

import (
	"fmt"
	"os"

	"github.com/joho/godotenv"

	"github.com/siddhant-vij/Note-Taking-App/services"
)

func loadURI() string {
	if err := godotenv.Load(); err != nil {
		fmt.Println("No .env file found")
	}

	uri := os.Getenv("MONGODB_URI")
	if uri == "" {
		fmt.Println("You must set your 'MONGODB_URI' environment variable.")
	}

	return uri
}

func main() {
	uri := loadURI()

	args := os.Args[1:]
	if len(args) == 0 {
		fmt.Println("No arguments provided. Please use:\n--c (for inserting data from CSV file)\n--j (for inserting data from JSON file)\n--q (for SQL queries)")
		return
	}
	switch args[0] {
	case "--c", "--j":
		err := services.NewFileReader(args[0], uri)
		if err != nil {
			fmt.Printf("Failed to process file: %s\n", err)
		}
	case "--q":
		services.SqlQueries(uri)
	default:
		fmt.Println("Invalid argument.")
	}
}
