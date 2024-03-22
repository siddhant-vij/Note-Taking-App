from datetime import datetime
from pymongo.collection import Collection

from typing import Final, List

from services.file_reader import FileReader
from utils.crud_operations import CrudOperations


class SqlQueries:

    def __init__(self):
        # ---------- Remove this afterwards ----------
        FileReader("--c")

        # URI and database/collection names
        uri = "mongodb://localhost:27017"
        dbName = "noteTakingApp"
        collectionName = "notes"

        # Connect to MongoDB client & get Collection
        client = CrudOperations.connect_to_client(uri)
        collection = CrudOperations.get_collection(
            client, dbName, collectionName)

        self._output_file_name: Final[str] = "output.txt"
        self._query_str: str = ""

        # ---------- Query Operations ----------

        self._query_str = "Find All Notes: "
        self.get_all_notes(collection)

        self._query_str = "Find Notes Containing a Specific Tag (eg. \"tag5\"): "
        self.get_notes_by_tag(collection, "tag5")

        self._query_str = "Find Notes Created After a Specific Date (eg. \"2023-04-18\"): "
        self.get_notes_after_date(collection, "2023-04-18")

        self._query_str = "Update the Tags for a Note (eg. for note with title: \"MongoDB Advanced\", change it to \"nosql\"): "
        self.update_tags(collection, "MongoDB Advanced", ["nosql"])

        self._query_str = "Update one of the many Tags for a Note (eg. for note with title: \"MongoDB Advanced\", change \"nosql\" to \"non-relational\"): "
        self.update_single_tag(collection, "MongoDB Advanced",
                               "nosql", "non-relational")

        self._query_str = "Add another tag for a Note (eg. for note with title: \"MongoDB Advanced\", add \"important\" tag): "
        self.add_tag(collection, "MongoDB Advanced", "important")

        self._query_str = "Add a list of tags for a Note (eg. for note with title: \"mongodb intermediate\", add \"tag1\", \"tag2\", \"tag3\"): "
        self.add_tags(collection, "mongodb intermediate",
                      ["tag1", "tag2", "tag3"])

        self._query_str = "Update the title for a Note (eg. for note with title: \"mongodb intermediate\", change it to \"MongoDB Intermediate\"): "
        self.update_title(collection, "mongodb intermediate",
                          "MongoDB Intermediate")

        self._query_str = "Remove a Tag from a Note (eg. for note with title: \"MongoDB Intermediate\", remove \"tag3\"): "
        self.remove_tag(collection, "MongoDB Intermediate", "tag3")

        self._query_str = "Remove a list of Tags from a Note (eg. for note with title: \"MongoDB Intermediate\", remove \"tag1\" and \"tag7\"): "
        self.remove_tags(collection, "MongoDB Intermediate", ["tag1", "tag7"])

        self._query_str = "Remove all Tags from a Note (eg. for note with title: \"MongoDB Intermediate\", remove all tags): "
        self.remove_all_tags(collection, "MongoDB Intermediate")

        self._query_str = "Delete a Note (eg. for note with title: \"MongoDB Intermediate\", delete it): "
        self.delete_note(collection, "MongoDB Intermediate")

        self._query_str = "Find Notes by Partial Title (Case Insensitive Search) (eg. for note with title containing \"MongoDB\", find it): "
        self.get_notes_by_partial_title(collection, "MongoDB")

        self._query_str = "Find Notes by Title (Case Sensitive Search) (eg. for note with title containing \"mongodb\", find it): "
        self.get_notes_by_title(collection, "mongodb")

        self._query_str = "List Unique Tags Across All Notes: "
        self.get_unique_tags(collection)

        self._query_str = "Delete Notes Newer Than a Specific Date (eg. for notes created after \"2023-12-02\", delete them): "
        self.delete_notes_after_date(collection, "2023-12-02")

        self._query_str = "Sort Notes by Creation Date (Newest First): "
        self.sort_notes_by_creation_date_desc(collection)

        self._query_str = "Sort Notes by Title (Alphabetical Order): "
        self.sort_notes_by_title(collection)

        self._query_str = "Sort Notes by count of tags (Descending Order): "
        self.sort_notes_by_count_of_tags_desc(collection)

        self._query_str = "Delete all duplicate notes from the collection: "
        self.delete_duplicate_notes(collection)

        self._query_str = "Retrieve notes that have been updated after their creation date: "
        self.get_notes_updated_after_creation_date(collection)

        self._query_str = "List all notes that contain at least 3 different tags: "
        self.get_notes_with_three_tags(collection)

        self._query_str = "Find notes that do not have any tags associated with them: "
        self.get_notes_without_tags(collection)

        self._query_str = "Find notes that have been modified in the year 2024 so far: "
        self.get_notes_modified_in_year_2024(collection)

        self._query_str = "Aggregate notes by the month of creation and count the number of notes created each month: "
        self.get_notes_created_each_month(collection)

        self._query_str = "Identify notes with the word \"important\" in the title but without an \"important\" tag: "
        self.get_notes_with_important_title_but_no_important_tag(collection)

        # Close the MongoDB client connection
        CrudOperations.close_client(client)

    def get_all_notes(self, collection: Collection) -> None:
        find_result = CrudOperations.execute_query(collection)
        count = len(find_result)
        CrudOperations.print_query_result_to_file(
            self._query_str, count, self._output_file_name
        )

    def get_notes_by_tag(self, collection: Collection, tag: str) -> None:
        find_result = CrudOperations.execute_query(
            collection,
            filter={"tags": tag})
        count = len(find_result)
        CrudOperations.print_query_result_to_file(
            self._query_str, count, self._output_file_name
        )

    def get_notes_after_date(self, collection: Collection, date: str) -> None:
        date: datetime = datetime.strptime(date, "%Y-%m-%d")
        find_result = CrudOperations.execute_query(
            collection,
            filter={"created_on": {"$gt": date}})
        count = len(find_result)
        CrudOperations.print_query_result_to_file(
            self._query_str, count, self._output_file_name
        )

    def update_tags(self, collection: Collection, title: str, tags: List[str]) -> None:
        update_result = CrudOperations.update_document(
            collection,
            filter={"title": title},
            update={"$set": {"tags": tags}}
        )
        CrudOperations.print_query_result_to_file(
            self._query_str, update_result, self._output_file_name
        )

    def update_single_tag(self, collection: Collection, title: str, old_tag: str, new_tag: str) -> None:
        find_result = CrudOperations.execute_query(
            collection,
            filter={"title": title}
        )
        document: dict = find_result[0]
        tags: list = document["tags"]
        if old_tag in tags:
            tags.remove(old_tag)
        if new_tag not in tags:
            tags.append(new_tag)
        update_result = CrudOperations.update_document(
            collection,
            filter={"title": title},
            update={"$set": {"tags": tags}}
        )
        CrudOperations.print_query_result_to_file(
            self._query_str, update_result, self._output_file_name
        )

    def add_tag(self, collection: Collection, title: str, tag: str) -> None:
        update_result = CrudOperations.update_document(
            collection,
            filter={"title": title},
            update={"$addToSet": {"tags": tag}}
        )
        CrudOperations.print_query_result_to_file(
            self._query_str, update_result, self._output_file_name
        )

    def add_tags(self, collection: Collection, title: str, tags: List[str]) -> None:
        update_result = CrudOperations.update_document(
            collection,
            filter={"title": title},
            update={"$addToSet": {"tags": {"$each": tags}}}
        )
        CrudOperations.print_query_result_to_file(
            self._query_str, update_result, self._output_file_name
        )

    def update_title(self, collection: Collection, old_title: str, new_title: str) -> None:
        update_result = CrudOperations.update_document(
            collection,
            filter={"title": old_title},
            update={"$set": {"title": new_title}}
        )
        CrudOperations.print_query_result_to_file(
            self._query_str, update_result, self._output_file_name
        )

    def remove_tag(self, collection: Collection, title: str, tag: str) -> None:
        find_result = CrudOperations.execute_query(
            collection,
            filter={"title": title}
        )
        document: dict = find_result[0]
        tags: list = document["tags"]
        if tag in tags:
            tags.remove(tag)
        update_result = CrudOperations.update_document(
            collection,
            filter={"title": title},
            update={"$set": {"tags": tags}}
        )
        CrudOperations.print_query_result_to_file(
            self._query_str, update_result, self._output_file_name
        )

    def remove_tags(self, collection: Collection, title: str, tagsToDelete: List[str]) -> None:
        find_result = CrudOperations.execute_query(
            collection,
            filter={"title": title}
        )
        document: dict = find_result[0]
        tags: list = document["tags"]
        for tag in tagsToDelete:
            if tag in tags:
                tags.remove(tag)
        update_result = CrudOperations.update_document(
            collection,
            filter={"title": title},
            update={"$set": {"tags": tags}}
        )
        CrudOperations.print_query_result_to_file(
            self._query_str, update_result, self._output_file_name
        )

    def remove_all_tags(self, collection: Collection, title: str) -> None:
        update_result = CrudOperations.update_document(
            collection,
            filter={"title": title},
            update={"$set": {"tags": []}}
        )
        CrudOperations.print_query_result_to_file(
            self._query_str, update_result, self._output_file_name
        )

    def delete_note(self, collection: Collection, title: str) -> None:
        delete_result = CrudOperations.delete_document(
            collection,
            filter={"title": title}
        )
        CrudOperations.print_query_result_to_file(
            self._query_str, delete_result, self._output_file_name
        )

    def get_notes_by_partial_title(self, collection: Collection, search_term: str) -> None:
        find_result = CrudOperations.execute_query(
            collection,
            filter={"title": {"$regex": ".*" +
                              search_term + ".*", "$options": "i"}}
        )
        count = len(find_result)
        CrudOperations.print_query_result_to_file(
            self._query_str, count, self._output_file_name
        )

    def get_notes_by_title(self, collection: Collection, search_term: str) -> None:
        find_result = CrudOperations.execute_query(
            collection,
            filter={"title": {"$regex": ".*" + search_term + ".*"}}
        )
        count = len(find_result)
        CrudOperations.print_query_result_to_file(
            self._query_str, count, self._output_file_name
        )

    def get_unique_tags(self, collection: Collection) -> None:
        distinct_result = collection.distinct("tags")
        with open(self._output_file_name, "a") as f:
            f.write(self._query_str + "\n")
            f.write("[\n")
            for i in range(len(distinct_result)):
                if i % 2 == 0:
                    f.write("\t\"" + distinct_result[i] + "\",")
                else:
                    f.write("\t\"" + distinct_result[i] + "\"\n")
            f.write("]\n")
            f.write("\n\n\n")

    def delete_notes_after_date(self, collection: Collection, date: str) -> None:
        date: datetime = datetime.strptime(date, "%Y-%m-%d")
        delete_result = CrudOperations.delete_multiple_documents(
            collection,
            filter={"created_on": {"$gt": date}}
        )
        CrudOperations.print_query_result_to_file(
            self._query_str, delete_result, self._output_file_name
        )

    def sort_notes_by_creation_date_desc(self, collection: Collection) -> None:
        find_result = CrudOperations.execute_query(
            collection,
            sort={"created_on": -1},
            limit=15
        )
        CrudOperations.print_query_result_to_file(
            self._query_str, find_result, self._output_file_name
        )

    def sort_notes_by_title(self, collection: Collection) -> None:
        find_result = CrudOperations.execute_query(
            collection,
            sort={"title": 1},
            limit=15
        )
        CrudOperations.print_query_result_to_file(
            self._query_str, find_result, self._output_file_name
        )

    def sort_notes_by_count_of_tags_desc(self, collection: Collection) -> None:
        aggregate_result = CrudOperations.execute_aggregate_query(
            collection,
            pipeline=[
                {"$addFields": {"tag_count": {"$size": "$tags"}}},
                {"$sort": {"tag_count": -1}},
                {"$limit": 15}
            ]
        )
        CrudOperations.print_query_result_to_file(
            self._query_str, aggregate_result, self._output_file_name
        )

    def delete_duplicate_notes(self, collection: Collection) -> None:
        with open(self._output_file_name, "a") as f:
            count_before = collection.count_documents({})
            f.write(self._query_str + "\n")
            f.write("Count (Before deletion of duplicates):\n" +
                    str(count_before) + "\n")

            pipeline = [
                {
                    "$group": {
                        "_id": "$title",
                        "uniqueIds": {"$addToSet": "$_id"}, "count": {"$sum": 1}
                    }
                },
                {
                    "$match": {
                        "count": {"$gt": 1}
                    }
                }
            ]
            for document in collection.aggregate(pipeline):
                ids: list = document["uniqueIds"]
                if len(ids) > 1:
                    ids.pop()
                    collection.delete_many(
                        filter={"_id": {"$in": ids}})

            count_after = collection.count_documents({})
            f.write("Count (After deletion of duplicates):\n" +
                    str(count_after) + "\n")
            f.write("\n\n\n")

    def get_notes_updated_after_creation_date(self, collection: Collection) -> None:
        find_result = CrudOperations.execute_query(
            collection,
            filter={"$expr": {"$gt": ["$last_updated_on", "$created_on"]}},
            limit=15
        )
        CrudOperations.print_query_result_to_file(
            self._query_str, find_result, self._output_file_name
        )

    def get_notes_with_three_tags(self, collection: Collection) -> None:
        find_result = CrudOperations.execute_query(
            collection,
            filter={"tags": {"$size": 3}},
        )
        count = len(find_result)
        CrudOperations.print_query_result_to_file(
            self._query_str, count, self._output_file_name
        )

    def get_notes_without_tags(self, collection: Collection) -> None:
        find_result = CrudOperations.execute_query(
            collection,
            filter={"tags": []},
        )
        count = len(find_result)
        CrudOperations.print_query_result_to_file(
            self._query_str, count, self._output_file_name
        )

    def get_notes_modified_in_year_2024(self, collection: Collection) -> None:
        date: datetime = datetime.strptime("2024-01-01", "%Y-%m-%d")
        find_result = CrudOperations.execute_query(
            collection,
            filter={"last_updated_on": {"$gte": date}}
        )
        count = len(find_result)
        CrudOperations.print_query_result_to_file(
            self._query_str, count, self._output_file_name
        )

    def get_notes_created_each_month(self, collection: Collection) -> None:
        aggregate_result = CrudOperations.execute_aggregate_query(
            collection,
            pipeline=[
                {
                    "$group": {
                        "_id": {
                            "$dateToString": {
                                "format": "%Y-%m",
                                "date": "$created_on"
                            }
                        },
                        "count": {
                            "$sum": 1
                        }
                    }
                },
                {
                    "$sort": {
                        "_id": 1
                    }
                }
            ]
        )
        with open(self._output_file_name, "a") as f:
            f.write(self._query_str + "\n")
            f.write("[\n")
            for doc in aggregate_result:
                f.write("\t{")
                for key, value in doc.items():
                    f.write(f"\t\t{key}: {value}")
                    if key is not list(doc.keys())[-1]:
                        f.write(',')
                f.write("}\n")
            f.write("]\n")
            f.write("\n\n\n")

    def get_notes_with_important_title_but_no_important_tag(self, collection: Collection) -> None:
        find_result = CrudOperations.execute_query(
            collection,
            filter={
                "title": {"$regex": "important", "$options": "i"},
                "tags": {"$nin": ["important"]}
            }
        )
        count = len(find_result)
        CrudOperations.print_query_result_to_file(
            self._query_str, count, self._output_file_name
        )
