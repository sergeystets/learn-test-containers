package learn.test.containers;

import java.time.LocalDate;
import java.util.TimeZone;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@RunWith(SpringRunner.class)
public class HibernateBugTest extends IntegrationTest.Jpa {

  @Autowired
  private TransactionTemplate tx;
  @Autowired
  private ReportRepository reportRepo;

  private static final TimeZone originalTimezone = TimeZone.getDefault();

  @BeforeClass
  public static void beforeClass() {
    // to reproduce the bug we need to set up a timezone which differs from db a server timezone
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kiev"));
  }

  @AfterClass
  public static void afterClass() {
    TimeZone.setDefault(originalTimezone);
  }

  @After
  public void after() {
    reportRepo.deleteAll();
  }

  @Test
  public void hibernateShouldFailToRetrieveLocalDateProperly() {
    // given
    final LocalDate updatedDate = LocalDate.of(2020, 5, 28);
    final Report report = new Report().setUpdatedDate(updatedDate);

    // when
    final Integer reportId = reportRepo.save(report).getId();

    // then
    tx.execute(status -> {
      final Report saved = reportRepo.getOne(reportId);
      Assert.assertNotNull(saved);
      // [bug] Hibernate always subtract one day from the originally saved local date when client
      // and server time zones are different
      Assert.assertEquals(updatedDate.minusDays(1), saved.getUpdatedDate());
      return null;
    });
  }
}
