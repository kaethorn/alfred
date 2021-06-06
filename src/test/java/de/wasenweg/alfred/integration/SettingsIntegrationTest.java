package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.EnableEmbeddedMongo;
import de.wasenweg.alfred.settings.Setting;
import de.wasenweg.alfred.settings.SettingRepository;
import de.wasenweg.alfred.settings.SettingsService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableEmbeddedMongo
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class SettingsIntegrationTest {

  private static final String TEST_APPLICATION_YML = "applicationConfig: [classpath:/application-test.yml]";

  private final SettingsService settingsService;
  private final SettingRepository settingRepository;
  private final ConfigurableEnvironment environment;

  @BeforeEach
  public void setUp() {
    this.settingRepository.deleteAll();
  }

  @AfterEach
  public void tearDown() {
    this.settingRepository.deleteAll();
  }

  @Test
  public void settingsByDefault() {
    assertThat(this.settingRepository.findAll().size()).isEqualTo(0);
    this.setComicsPathEnvironment(null);

    this.settingsService.setup();

    final List<Setting> settings = this.settingRepository.findAll();
    assertThat(settings.size()).isEqualTo(5);
    assertThat(settings.get(0).getKey()).isEqualTo("comics.path");
    assertThat(settings.get(0).getValue()).isEqualTo("/comics");
  }

  @Test
  public void withExistingSettingsAndWithoutEnvironmentValue() {
    final Setting setting = Setting.builder()
        .key("comics.path")
        .name("Path")
        .value("/existing/comics")
        .comment("")
        .build();
    this.settingRepository.save(setting);
    this.setComicsPathEnvironment(null);

    this.settingsService.setup();

    final List<Setting> settings = this.settingRepository.findAll();
    assertThat(settings.size()).isEqualTo(5);
    assertThat(settings.get(0).getKey()).isEqualTo("comics.path");
    assertThat(settings.get(0).getValue()).isEqualTo("/existing/comics");
  }

  @Test
  public void withoutExistingSettingsAndWithEnvironmentValue() {
    this.setComicsPathEnvironment("/environment/comics");

    this.settingsService.setup();

    final List<Setting> settings = this.settingRepository.findAll();
    assertThat(settings.size()).isEqualTo(5);
    assertThat(settings.get(0).getKey()).isEqualTo("comics.path");
    assertThat(settings.get(0).getValue()).isEqualTo("/environment/comics");
  }

  @Test
  public void withExistingSettingsAndWithEnvironmentValue() {
    final Setting setting = Setting.builder()
        .key("comics.path")
        .name("Path")
        .value("/existing/comics")
        .comment("")
        .build();
    this.settingRepository.save(setting);
    this.setComicsPathEnvironment("/environment/comics");

    this.settingsService.setup();

    final List<Setting> settings = this.settingRepository.findAll();
    assertThat(settings.size()).isEqualTo(5);
    assertThat(settings.get(0).getKey()).isEqualTo("comics.path");
    assertThat(settings.get(0).getValue()).isEqualTo("/environment/comics");
  }

  private void setComicsPathEnvironment(final String value) {
    this.environment.getPropertySources().replace(TEST_APPLICATION_YML, new PropertySource(TEST_APPLICATION_YML) {
      @Override
      public Object getProperty(final String name) {
        if ("comics.path".equals(name)) {
          return value;
        }
        return null;
      }
    });
  }
}
