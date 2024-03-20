import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class JsonReader implements DataReader {
  private final List<Note> notes;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/uuuu");

  JsonReader() {
    this.notes = new ArrayList<>();
  }

  public List<Note> getNotes() {
    return Collections.unmodifiableList(notes);
  }

  public void readNotes(String jsonFilePath) {
    ObjectMapper mapper = new ObjectMapper();

    try {
      // Read JSON file and map to list of Note-like objects
      List<NoteTemplate> noteTemplates = mapper.readValue(new File(jsonFilePath),
          new TypeReference<List<NoteTemplate>>() {
          });

      // Convert NoteTemplate objects to Note objects
      for (NoteTemplate template : noteTemplates) {
        LocalDate createdOnLocalDate = LocalDate.parse(template.created_on, formatter);
        LocalDate lastUpdatedOnLocalDate = LocalDate.parse(template.last_updated_on, formatter);

        LocalDateTime createdOnLocalDateTime = createdOnLocalDate.atStartOfDay();
        LocalDateTime lastUpdatedOnLocalDateTime = lastUpdatedOnLocalDate.atStartOfDay();

        Date createdOn = Date.from(createdOnLocalDateTime.toInstant(ZoneOffset.UTC));
        Date lastUpdatedOn = Date.from(lastUpdatedOnLocalDateTime.toInstant(ZoneOffset.UTC));

        Note note = new Note(template.title, template.content, template.tags, createdOn, lastUpdatedOn);
        this.notes.add(note);
      }
    } catch (IOException e) {
      System.out.println("Error reading JSON file: " + e.getMessage());
      e.printStackTrace();
    }
  }

  // NoteTemplate class to temporarily hold JSON data
  private static class NoteTemplate {
    public String title;
    public String content;
    public List<String> tags;
    public String created_on;
    public String last_updated_on;
  }
}
