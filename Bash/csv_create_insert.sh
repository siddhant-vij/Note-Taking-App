#!/bin/bash

MONGODB_URI="mongodb://127.0.0.1:27017"
MONGODB_DATABASE="noteTakingApp"
MONGODB_COLLECTION="notes"

MONGOSH="mongosh ${MONGODB_URI}/${MONGODB_DATABASE} --quiet --eval"

# Drop the entire database before proceeding.
$MONGOSH " db.dropDatabase()"

# Read from CSV and skip the header line
cat "./Problem/input_data.csv" | while IFS="," read -r TITLE CONTENT TAGS CREATED_ON LAST_UPDATED_ON
do
  # Strip carriage return characters from all fields
  TITLE="${TITLE//$'\r'/}"
  CONTENT="${CONTENT//$'\r'/}"
  TAGS="${TAGS//$'\r'/}"
  CREATED_ON="${CREATED_ON//$'\r'/}"
  LAST_UPDATED_ON="${LAST_UPDATED_ON//$'\r'/}"

  if [[ $TITLE != "title" ]]
  then
    # Convert MM/DD/YYYY to YYYY-MM-DD for ISODate
    CREATED_ON_ISO=$(date -d"$CREATED_ON" "+%Y-%m-%dT00:00:00Z")
    LAST_UPDATED_ON_ISO=$(date -d"$LAST_UPDATED_ON" "+%Y-%m-%dT00:00:00Z")

    # Prepare the tags as a JSON array
    TAGS_ARRAY=($TAGS)
    TAGS_JSON="["
    if [[ -n $TAGS ]]; then
      for tag in ${TAGS_ARRAY[@]}
      do
        TAGS_JSON+="\"$tag\","
      done
      TAGS_JSON="${TAGS_JSON%,}" # Remove trailing comma
    fi
    TAGS_JSON+="]"

    # Properly escape and construct the MongoDB insert command
    INSERT_COMMAND="db.$MONGODB_COLLECTION.insertOne({title:'$TITLE', content:'$CONTENT', tags:$TAGS_JSON, created_on:ISODate('$CREATED_ON_ISO'), last_updated_on:ISODate('$LAST_UPDATED_ON_ISO')})"

    # Execute the MongoDB insert command
    $MONGOSH "$INSERT_COMMAND"
  fi
done
