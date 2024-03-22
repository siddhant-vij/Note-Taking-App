from utils.csv_reader import CsvReader
from utils.json_reader import JsonReader
from services.create_insert import create_insert


class FileReader:
    def __init__(self, flag: str):
        if flag == "--c":
            self.reader: CsvReader = CsvReader()
            self.reader.read_notes()
        else:
            self.reader: JsonReader = JsonReader()
            self.reader.read_notes()
        create_insert(self.reader.notes)
