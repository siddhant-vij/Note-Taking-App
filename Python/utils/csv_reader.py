from typing import List
from datetime import datetime
import csv

from models.note import Note


CSV_FILE = "Problem/input_data.csv"


class CsvReader:
    def __init__(self):
        self._notes: List[Note] = []

    @property
    def notes(self) -> List[Note]:
        return self._notes

    def _read_data(self, csv_file: str) -> List[Note]:
        with open(csv_file, "r", newline='') as f:
            reader = csv.reader(f)
            next(reader)  # skip the header
            result = []
            for row in reader:
                data = Note(
                    row[0].strip(),
                    row[1].strip(),
                    row[2].strip().split(" ")
                    if row[2].strip()
                    else [],
                    datetime.strptime(
                        row[3].strip(),
                        "%m/%d/%Y"),
                    datetime.strptime(
                        row[4].strip(),
                        "%m/%d/%Y"),
                )
                result.append(data)
            return result

    def read_notes(self) -> None:
        self._notes = self._read_data(CSV_FILE)
