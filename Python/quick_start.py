from pymongo.mongo_client import MongoClient
from pymongo.server_api import ServerApi

uri = "mongodb://localhost:27017/"

client = MongoClient(uri, server_api=ServerApi('1'))

try:
    db = client["noteTakingApp"]
    collection = db["notes"]

    doc = collection.find_one({"title": "MongoDB Basics important Mong"})
    if doc:
        print(doc)
    else:
        print("No matching documents found.")

except Exception as e:
    print(e)
