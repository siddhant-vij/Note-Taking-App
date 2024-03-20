import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CsvReader implements DataReader {
  private final List<Note> notes;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/uuuu");

  CsvReader() {
    this.notes = new ArrayList<>();
  }

  public List<Note> getNotes() {
    return Collections.unmodifiableList(notes);
  }

  public void readNotes(String csvFilePath) {
    try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.isBlank() || line.startsWith("title")) {
          continue;
        }
        String[] values = line.split(",");
        String title = values[0];
        String content = values[1];
        List<String> tags = new ArrayList<>();
        if (!values[2].equals("")) {
          String[] tagList = values[2].split(" ");
          tags = new ArrayList<>(List.of(tagList));
        }

        // Parse the date strings
        LocalDate createdOnLocalDate = LocalDate.parse(values[3].strip(), formatter);
        LocalDate lastUpdatedOnLocalDate = LocalDate.parse(values[4].strip(), formatter);

        // Convert to LocalDateTime at the start of the day (midnight)
        LocalDateTime createdOnLocalDateTime = createdOnLocalDate.atStartOfDay();
        LocalDateTime lastUpdatedOnLocalDateTime = lastUpdatedOnLocalDate.atStartOfDay();

        // Convert LocalDateTime to Date at UTC+0 (midnight UTC)
        Date createdOn = Date.from(createdOnLocalDateTime.toInstant(ZoneOffset.UTC));
        Date lastUpdatedOn = Date.from(lastUpdatedOnLocalDateTime.toInstant(ZoneOffset.UTC));

        Note note = new Note(title, content, tags, createdOn, lastUpdatedOn);
        this.notes.add(note);
      }
    } catch (IOException e) {
      System.out.println("Error reading CSV file: " + e.getMessage());
      e.printStackTrace();
    }
  }
}