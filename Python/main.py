import sys

from services.file_reader import FileReader
from services.sql_queries import SqlQueries


def main(args):
    if len(args) == 0:
        print("No arguments provided. Please use:\n--c (for inserting data from CSV file)\n--j (for inserting data from JSON file)\n--q (for sql queries)")
    elif len(args) == 1:
        if args[0] == "--c" or args[0] == "--j":
            FileReader(args[0])
        elif args[0] == "--q":
            SqlQueries()
        else:
            print("Invalid argument.")
    else:
        print("Invalid number of arguments.")


if __name__ == "__main__":
    main(sys.argv[1:])
