package models

import (
	"encoding/json"
	"fmt"
	"time"
)

type Note struct {
	Title         string    `json:"title" bson:"title"`
	Content       string    `json:"content" bson:"content"`
	Tags          []string  `json:"tags" bson:"tags"`
	CreatedOn     time.Time `json:"created_on" bson:"created_on"`
	LastUpdatedOn time.Time `json:"last_updated_on" bson:"last_updated_on"`
}

func ParseDate(dateString string) (time.Time, error) {
	layouts := []string{
		"01/02/2006",
		"01/2/2006",
		"1/02/2006",
		"1/2/2006",
	}
	for _, layout := range layouts {
		if date, err := time.Parse(layout, dateString); err == nil {
			return date, nil
		}
	}
	return time.Time{}, fmt.Errorf("invalid date format: %s", dateString)
}

func (n *Note) UnmarshalJSON(data []byte) error {
	var aux struct {
		Title         string   `json:"title"`
		Content       string   `json:"content"`
		Tags          []string `json:"tags"`
		CreatedOn     string   `json:"created_on"`
		LastUpdatedOn string   `json:"last_updated_on"`
	}

	if err := json.Unmarshal(data, &aux); err != nil {
		return err
	}

	n.Title = aux.Title
	n.Content = aux.Content
	n.Tags = aux.Tags

	var err error
	n.CreatedOn, err = ParseDate(aux.CreatedOn)
	if err != nil {
		return err
	}
	n.LastUpdatedOn, err = ParseDate(aux.LastUpdatedOn)
	if err != nil {
		return err
	}

	return nil
}
