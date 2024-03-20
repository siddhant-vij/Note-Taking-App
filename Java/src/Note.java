import java.util.List;
import java.util.Date;

public class Note {
  private String title;
  private String content;
  private List<String> tags;
  private Date createdOn;
  private Date lastUpdatedOn;

  public Note(String title, String content, List<String> tags, Date createdOn, Date lastUpdatedOn) {
    this.title = title;
    this.content = content;
    this.tags = tags;
    this.createdOn = createdOn;
    this.lastUpdatedOn = lastUpdatedOn;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public List<String> getTags() {
    return tags;
  }

  public Date getCreatedOn() {
    return createdOn;
  }

  public Date getLastUpdatedOn() {
    return lastUpdatedOn;
  }
}
