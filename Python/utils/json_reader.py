from datetime import datetime
import json
from typing import List

from models.note import Note


JSON_FILE = "Problem/input_data.json"


class JsonReader:
    def __init__(self):
        self._notes: List[Note] = []

    @property
    def notes(self) -> List[Note]:
        return self._notes

    def _read_data(self, json_file: str) -> List[Note]:
        with open(json_file, "r") as f:
            data = json.load(f)
            result = []
            for note in data:
                data = Note(
                    note["title"],
                    note["content"],
                    note["tags"],
                    datetime.strptime(
                        note["created_on"],
                        "%m/%d/%Y"),
                    datetime.strptime(
                        note["last_updated_on"],
                        "%m/%d/%Y"),
                )
                result.append(data)
            return result

    def read_notes(self) -> None:
        self._notes = self._read_data(JSON_FILE)
