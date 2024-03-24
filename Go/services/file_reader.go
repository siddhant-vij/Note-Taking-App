package services

import (
	"fmt"

	"github.com/siddhant-vij/Note-Taking-App/models"
	"github.com/siddhant-vij/Note-Taking-App/utils"
)

type Reader interface {
	ReadNotes() error
	Notes() []models.Note
}

func NewFileReader(flag string, uri string) error {
	var r Reader
	switch flag {
	case "--c":
		r = &utils.CSVReader{}
	case "--j":
		r = &utils.JSONReader{}
	default:
		return fmt.Errorf("unsupported flag: %s", flag)
	}
	err := r.ReadNotes()
	if err != nil {
		return err
	}
	CreateInsert(uri, r.Notes())
	return nil
}
