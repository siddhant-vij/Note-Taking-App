from typing import List

from utils.crud_operations import CrudOperations
from models.note import Note


def create_insert(list_of_notes: List[Note]) -> None:
    # URI and database/collection names
    uri = "mongodb://localhost:27017"
    dbName = "noteTakingApp"
    collectionName = "notes"

    # Connect to MongoDB client
    client = CrudOperations.connect_to_client(uri)

    # Drop the database - before starting
    CrudOperations.drop_database(client, dbName)

    # Get the collection
    collection = CrudOperations.get_collection(client, dbName, collectionName)

    # Insert documents into the collection
    for note in list_of_notes:
        document = {
            "title": note.title,
            "content": note.content,
            "tags": note.tags,
            "created_on": note.created_on,
            "last_updated_on": note.last_updated_on
        }
        CrudOperations.insert_document(collection, document)

    # Close the MongoDB client connection
    CrudOperations.close_client(client)

    # Temp: Make the output file empty
    outputFilePath = "output.txt"
    try:
        with open(outputFilePath, 'w') as f:
            f.write('')
    except Exception as e:
        print("Error writing output file: ", e)
