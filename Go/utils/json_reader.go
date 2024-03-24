package utils

import (
	"encoding/json"
	"os"

	"github.com/siddhant-vij/Note-Taking-App/models"
)

const JSON_FILE = "Problem/input_data.json"

type JSONReader struct {
	notes []models.Note
}

func (jr *JSONReader) Notes() []models.Note {
	return jr.notes
}

func (jr *JSONReader) readData(jsonFile string) ([]models.Note, error) {
	file, err := os.Open(jsonFile)
	if err != nil {
		return nil, err
	}
	defer file.Close()

	decoder := json.NewDecoder(file)
	var data []models.Note
	if err := decoder.Decode(&data); err != nil {
		return nil, err
	}

	return data, nil
}

func (jr *JSONReader) ReadNotes() error {
	notes, err := jr.readData(JSON_FILE)
	if err != nil {
		return err
	}
	jr.notes = notes
	return nil
}
