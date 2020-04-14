package learn.test.containers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Table(name = "report")
@Data
@Accessors(chain = true)
public class Report {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "updated_date_time")
  private LocalDateTime updatedDatetime;

  @Column(name = "updated_date")
  private LocalDate updatedDate;

  public LocalDate getUpdatedDateUsingHack() {
    return updatedDatetime == null ? null : LocalDate.from(updatedDatetime);
  }

  public Report setUpdatedDateUsingHack(LocalDate localDate) {
    if (localDate != null) {
      updatedDatetime = LocalDateTime.of(localDate, LocalTime.of(0, 0, 0));
    }
    return this;
  }

}
