import java.util.List;

public interface DataReader {
  public List<Note> getNotes();

  public void readNotes(String filePath);
}
