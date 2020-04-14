package learn.test.containers;

import java.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@RunWith(SpringRunner.class)
public class HibernateBugWorkaroundTest extends IntegrationTest.Jpa {

  @Autowired
  private TransactionTemplate tx;
  @Autowired
  private ReportRepository reportRepo;

  @After
  public void tearDown() {
    reportRepo.deleteAll();
  }

  @Test
  public void localDateWorkaroundTest() {
    // given
    final LocalDate updatedDate = LocalDate.of(2020, 5, 28);
    final Report reportToSave = new Report().setUpdatedDateUsingHack(updatedDate);

    // when
    final Integer reportId = reportRepo.save(reportToSave).getId();

    // then
    tx.execute(status -> {
      final Report saved = reportRepo.getOne(reportId);
      Assert.assertNotNull(saved);
      Assert.assertEquals(updatedDate, saved.getUpdatedDateUsingHack());
      return null;
    });
  }

}
