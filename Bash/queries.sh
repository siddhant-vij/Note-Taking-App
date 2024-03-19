#!/bin/bash
# Run after create_insert.sh

MONGODB_URI="mongodb://127.0.0.1:27017"
MONGODB_DATABASE="noteTakingApp"
MONGODB_COLLECTION="notes"

MONGOSH="mongosh ${MONGODB_URI}/${MONGODB_DATABASE} --quiet --eval"

# Query Operations

echo -e "Find All Notes:" > output.txt
QUERY="db.$MONGODB_COLLECTION.countDocuments()"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nFind Notes Containing a Specific Tag (eg. "tag5"): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.countDocuments({tags:'tag5'})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nFind Notes Created After a Specific Date (eg. "2023-04-18"): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.countDocuments({created_on:{\$gt: ISODate('2023-04-18T00:00:00Z')}})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nUpdate the Tags for a Note (eg. for note with title: "MongoDB Advanced", change it to "nosql"): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.updateOne({title:'MongoDB Advanced'},{\$set:{tags:['nosql']}})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nUpdate one of the many Tags for a Note (eg. for note with title: "MongoDB Advanced", change "nosql" to "non-relational"): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.updateOne({title: 'MongoDB Advanced'}, {\$pull: {tags: 'nosql'}}); db.$MONGODB_COLLECTION.updateOne({title: 'MongoDB Advanced'}, {\$addToSet: {tags: 'non-relational'}})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nAdd another tag for a Note (eg. for note with title: "MongoDB Advanced", add "important" tag): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.updateOne({title:'MongoDB Advanced'},{\$push:{tags:'important'}})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nAdd a list of tags for a Note (eg. for note with title: "mongodb intermediate", add "tag1", "tag2", "tag3"): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.updateOne({title:'mongodb intermediate'},{\$addToSet:{tags:{\$each:['tag2','tag3','tag4']}}})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nUpdate the title for a Note (eg. for note with title: "mongodb intermediate", change it to "MongoDB Intermediate"): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.updateOne({title:'mongodb intermediate'},{\$set:{title:'MongoDB Intermediate'}})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nRemove a Tag from a Note (eg. for note with title: "MongoDB Intermediate", remove "tag3"): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.updateOne({title:'MongoDB Intermediate'},{\$pull:{tags:'tag3'}})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nRemove a list of Tags from a Note (eg. for note with title: "MongoDB Intermediate", remove "tag1" and "tag7"): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.updateOne({title:'MongoDB Intermediate'},{\$pull:{tags:{\$in:['tag1','tag7']}}})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nRemove all Tags from a Note (eg. for note with title: "MongoDB Intermediate", remove all tags): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.updateOne({title:'MongoDB Intermediate'},{\$set:{tags:[]}})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nDelete a Note (eg. for note with title: "MongoDB Intermediate", delete it): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.deleteOne({title:'MongoDB Intermediate'})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nFind Notes by Partial Title (Case Insensitive Search) (eg. for note with title containing "MongoDB", find it): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.countDocuments({title: /MongoDB/i})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nFind Notes by Title (Case Sensitive Search) (eg. for note with title containing "mongodb", find it): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.countDocuments({title: /mongodb/})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nList Unique Tags Across All Notes: " >> output.txt
QUERY="db.$MONGODB_COLLECTION.distinct('tags',{})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nDelete Notes Newer Than a Specific Date (eg. for notes created after "2023-12-02", delete them): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.deleteMany({created_on:{\$gt: ISODate('2023-12-02T00:00:00Z')}})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nSort Notes by Creation Date (Newest First): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.find().sort({created_on: -1}).limit(15)"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nSort Notes by Title (Alphabetical Order): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.find().sort({title: 1}).limit(15)"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nSort Notes by count of tags (Descending Order): " >> output.txt
QUERY="db.$MONGODB_COLLECTION.aggregate([{\$addFields: {tagsCount: {\$size: '\$tags'}}}, {\$sort: {tagsCount: -1}}, {\$limit: 15}])"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nDelete all duplicate notes from the collection: " >> output.txt
QUERY_COUNT="db.$MONGODB_COLLECTION.countDocuments()"
echo "Count (Before deletion of duplicates): " >> output.txt
$MONGOSH "$QUERY_COUNT" >> output.txt
QUERY="db.$MONGODB_COLLECTION.aggregate([
  { \$group: {
    _id: '\$title',
    uniqueIds: { \$addToSet: '\$_id' },
    count: { \$sum: 1 }
  }},
  { \$match: {
    count: { \$gt: 1 }
  }}
]).forEach(function(doc) {
  doc.uniqueIds.pop();
  db.$MONGODB_COLLECTION.deleteMany({_id: { \$in: doc.uniqueIds }});
})"
QUERY_COUNT="db.$MONGODB_COLLECTION.countDocuments()"
$MONGOSH "$QUERY"
echo "Count (After deletion of duplicates): " >> output.txt
$MONGOSH "$QUERY_COUNT" >> output.txt




echo -e "\n\n\nRetrieve notes that have been updated after their creation date: " >> output.txt
QUERY="db.$MONGODB_COLLECTION.find({\$expr: {\$gt: [\"\$last_updated_on\", \"\$created_on\"]}}).limit(15)"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nList all notes that contain at least 3 different tags: " >> output.txt
QUERY="db.$MONGODB_COLLECTION.countDocuments({tags: {\$size: 3}})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nFind notes that do not have any tags associated with them: " >> output.txt
QUERY="db.$MONGODB_COLLECTION.countDocuments({tags: []})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nFind notes that have been modified in the year 2024 so far: " >> output.txt
QUERY="db.$MONGODB_COLLECTION.countDocuments({last_updated_on: {\$gte: ISODate('2024-01-01T00:00:00Z')}})"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nAggregate notes by the month of creation and count the number of notes created each month: " >> output.txt
QUERY="db.$MONGODB_COLLECTION.aggregate([{\$group: {_id: {\$dateToString: {format: '%Y-%m', date: '\$created_on'}}, count: {\$sum: 1}}}, {\$sort: {_id: 1}}])"
$MONGOSH "$QUERY" >> output.txt




echo -e "\n\n\nIdentify notes with the word "important" in the title but without an "important" tag: " >> output.txt
QUERY="db.$MONGODB_COLLECTION.countDocuments({title: /important/, tags: {\$nin: ['important']}})"
$MONGOSH "$QUERY" >> output.txt
