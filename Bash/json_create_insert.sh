#!/bin/bash

MONGODB_URI="mongodb://127.0.0.1:27017"
MONGODB_DATABASE="noteTakingApp"
MONGODB_COLLECTION="notes"

MONGOSH="mongosh ${MONGODB_URI}/${MONGODB_DATABASE} --quiet --eval"

# Drop the entire database before proceeding.
$MONGOSH " db.dropDatabase()"

# Read and parse JSON file
jq -c '.[]' "./Problem/input_data.json" | while read -r line
do
  # Extract fields using jq
  TITLE=$(jq -r '.title' <<< "$line")
  CONTENT=$(jq -r '.content' <<< "$line")
  # Ensure tags are properly quoted as strings
  TAGS_JSON=$(jq -r '.tags | map("\"" + . + "\"") | join(",") | "[" + . + "]"' <<< "$line")
  CREATED_ON=$(jq -r '.created_on' <<< "$line")
  LAST_UPDATED_ON=$(jq -r '.last_updated_on' <<< "$line")

  # Convert MM/DD/YYYY to YYYY-MM-DD for ISODate
  CREATED_ON_ISO=$(date -d"$CREATED_ON" "+%Y-%m-%dT00:00:00Z")
  LAST_UPDATED_ON_ISO=$(date -d"$LAST_UPDATED_ON" "+%Y-%m-%dT00:00:00Z")

  # Prepare the MongoDB insert command
  INSERT_COMMAND="db.$MONGODB_COLLECTION.insertOne({title:'$TITLE', content:'$CONTENT', tags:$TAGS_JSON, created_on:ISODate('$CREATED_ON_ISO'), last_updated_on:ISODate('$LAST_UPDATED_ON_ISO')})"

  # Execute the MongoDB insert command
  $MONGOSH "$INSERT_COMMAND"
done
