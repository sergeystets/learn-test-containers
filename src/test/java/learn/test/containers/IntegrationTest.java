package learn.test.containers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IntegrationTest {

  private static final boolean enableTestContainers = enableTestContainers();

  /**
   * Extend this class if you want to have integration environment configured for JPA tests.
   */
  @ContextConfiguration(initializers = Jpa.Initializer.class)
  @ActiveProfiles("test")
  public abstract static class Jpa {

    private static MySQLContainer<?> mySQLContainer;

    static {
      if (enableTestContainers) {
        mySQLContainer = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("db")
            .withPassword("password");
        mySQLContainer.start();
      }
    }

    static class Initializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

      public void initialize(final @NotNull ConfigurableApplicationContext applicationContext) {
        if (enableTestContainers) {
          TestPropertyValues.of(
              "spring.datasource.url=" + mySQLContainer.getJdbcUrl(),
              "spring.datasource.username=" + mySQLContainer.getUsername(),
              "spring.datasource.password=" + mySQLContainer.getPassword()
          ).applyTo(applicationContext.getEnvironment());
        }
      }
    }
  }

  /**
   * Tests containers feature disable might be useful for situations when you want to manually start
   * containers in order to speed up tests runtime (usually you may want to disable test containers
   * when you initially create a test or troubleshoot it).
   */
  private static boolean enableTestContainers() {
    boolean testContainersEnabled = !"true".equals(System.getenv("test.containers.disable"));
    log.info("Test containers feature is " + (testContainersEnabled ? "ENABLED" : "DISABLED")
        + " for integration tests");
    return testContainersEnabled;
  }
}
