import json
from pymongo import MongoClient
from pymongo.collection import Collection
from pymongo.results import UpdateResult, DeleteResult
from bson.raw_bson import RawBSONDocument
from typing import Optional, List, Union


class CrudOperations:
    @staticmethod
    def connect_to_client(uri: str) -> Optional[MongoClient]:
        try:
            return MongoClient(uri)
        except Exception as e:
            print(f"Connection Error: {e}")
            return None

    @staticmethod
    def close_client(client: MongoClient) -> None:
        try:
            client.close()
        except Exception as e:
            print(f"Close Client Error: {e}")

    @staticmethod
    def drop_database(client: MongoClient, db_name: str) -> None:
        try:
            client.drop_database(db_name)
        except Exception as e:
            print(f"Error dropping database '{db_name}': {e}")

    @staticmethod
    def get_collection(client: MongoClient, db_name: str, collection_name: str) -> Optional[Collection]:
        try:
            return client[db_name][collection_name]
        except Exception as e:
            print(f"Get Collection Error: {e}")
            return None

    @staticmethod
    def insert_document(collection: Collection, document: RawBSONDocument) -> None:
        try:
            collection.insert_one(document)
        except Exception as e:
            print(f"Insert document Error: {e}")

    @staticmethod
    def execute_query(collection: Collection, filter: Optional[dict] = None, sort: Optional[list] = None, projection: Optional[dict] = None, limit: Optional[int] = None) -> Optional[List[RawBSONDocument]]:
        try:
            query = collection.find(filter, projection)
            if sort is not None:
                query.sort(sort)
            if limit is not None and limit > 0:
                query.limit(limit)
            return list(query)
        except Exception as e:
            print(f"Query Execution Error: {e}")
            return None

    @staticmethod
    def execute_aggregate_query(collection: Collection, pipeline: List[dict]) -> Optional[List[RawBSONDocument]]:
        try:
            return list(collection.aggregate(pipeline))
        except Exception as e:
            print(f"Aggregate Query Execution Error: {e}")
            return None

    @staticmethod
    def update_document(collection: Collection, filter: dict, update: dict, options: Optional[dict] = None) -> Optional[UpdateResult]:
        try:
            return collection.update_one(filter, update, **options) if options else collection.update_one(filter, update)
        except Exception as e:
            print(f"Update document Error: {e}")
            return None

    @staticmethod
    def update_multiple_document(collection: Collection, filter: dict, update: dict, options: Optional[dict] = None) -> Optional[UpdateResult]:
        try:
            return collection.update_many(filter, update, **options) if options else collection.update_many(filter, update)
        except Exception as e:
            print(f"Update Multiple document Error: {e}")
            return None

    @staticmethod
    def delete_document(collection: Collection, filter: dict, options: Optional[dict] = None) -> Optional[DeleteResult]:
        try:
            return collection.delete_one(filter, **options) if options else collection.delete_one(filter)
        except Exception as e:
            print(f"Delete document Error: {e}")
            return None

    @staticmethod
    def delete_multiple_documents(collection: Collection, filter: dict, options: Optional[dict] = None) -> Optional[DeleteResult]:
        try:
            return collection.delete_many(filter, **options) if options else collection.delete_many(filter)
        except Exception as e:
            print(f"Delete Multiple documents Error: {e}")
            return None

    @staticmethod
    def write_to_output_file(query_str: str, content: str, file_path: str) -> None:
        with open(file_path, 'a') as f:
            f.write(f"{query_str}\n")
            f.write(f"{content}\n")
            f.write("\n\n\n")

    @staticmethod
    def print_query_result_to_file(query_str: str, result: Union[int, List[dict], UpdateResult, DeleteResult], file_path: str) -> None:
        if isinstance(result, int):
            # For count of documents
            content = f"Count: {result}"
        elif isinstance(result, list):
            # For a list of documents
            content = "[\n"
            for doc in result:
                content += "\t{\n"
                for key, value in doc.items():
                    content += f"\t\t{key}: {value}"
                    if doc[key] is not result[-1][key]:
                        content += ','
                    content += '\n'
                content += "\t},\n" if doc is not result[-1] else "\t}\n"
            content += "]"
        elif isinstance(result, UpdateResult):
            # For UpdateResult            
            content = "{\n"
            content += f"\t\"acknowledged\": {result.acknowledged},\n"
            content += f"\t\"insertedId\": {str(result.upserted_id) if result.upserted_id else 'null'},\n"
            content += f"\t\"matchedCount\": {result.matched_count},\n"
            content += f"\t\"modifiedCount\": {result.modified_count},\n"
            content += f"\t\"upsertedCount\": 0\n"
            content += "}"

        elif isinstance(result, DeleteResult):
            # For DeleteResult
            content = f"AcknowledgedDeleteResult"
            content += "{deletedCount="
            content += f"{result.deleted_count}"
            content += "}"
        else:
            content = "Unsupported result type"

        CrudOperations.write_to_output_file(query_str, content, file_path)
