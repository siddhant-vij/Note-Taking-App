package utils

import (
	"encoding/csv"
	"fmt"
	"os"
	"strings"

	"github.com/siddhant-vij/Note-Taking-App/models"
)

const CSV_FILE = "Problem/input_data.csv"

type CSVReader struct {
	notes []models.Note
}

func (cr *CSVReader) Notes() []models.Note {
	return cr.notes
}

func (cr *CSVReader) readData(csvFile string) ([]models.Note, error) {
	file, err := os.Open(csvFile)
	if err != nil {
		return nil, err
	}
	defer file.Close()

	reader := csv.NewReader(file)
	if _, err := reader.Read(); err != nil {
		return nil, err
	}

	records, err := reader.ReadAll()
	if err != nil {
		return nil, err
	}

	result := []models.Note{}
	for _, row := range records {
		tags := []string{}
		if strings.TrimSpace(row[2]) != "" {
			tags = strings.Split(row[2], " ")
		}
		createdOn, err := models.ParseDate(strings.TrimSpace(row[3]))
		if err != nil {
			return nil, fmt.Errorf("error parsing CreatedOn date: %v", err)
		}
		lastUpdatedOn, err := models.ParseDate(strings.TrimSpace(row[4]))
		if err != nil {
			return nil, fmt.Errorf("error parsing LastUpdatedOn date: %v", err)
		}
		
		result = append(result, models.Note{
			Title:         strings.TrimSpace(row[0]),
			Content:       strings.TrimSpace(row[1]),
			Tags:          tags,
			CreatedOn:     createdOn,
			LastUpdatedOn: lastUpdatedOn,
		})
	}

	return result, nil
}

func (cr *CSVReader) ReadNotes() error {
	notes, err := cr.readData(CSV_FILE)
	if err != nil {
		return err
	}
	cr.notes = notes
	return nil
}
