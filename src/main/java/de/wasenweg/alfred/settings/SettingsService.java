package de.wasenweg.alfred.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettingsService {

  private final List<Setting> defaults = new ArrayList<>();
  private final SettingRepository settingRepository;
  private final Environment environment;

  /**
   * Establishes application settings.
   *
   * It follows this order:
   * 1. Spring Environment properties.
   * 2. Existing database entries.
   * 3. Defaults.
   */
  @PostConstruct
  public void setup() {
    this.defaults.clear();

    // Built in defaults:
    this.defaults.add(new Setting("comics.path", "Path", "/comics", "Path to your comic library"));
    this.defaults.add(new Setting("comics.comicVine.ApiKey", "Comi Vine API key", "", "Comic Vine API key from https://comicvine.gamespot.com/api/"));
    this.defaults.add(new Setting("auth.users", "Users", "", "Users authorized to access this server (comma separated)"));
    this.defaults.add(new Setting("auth.passwords", "Passwords", "", "Passwords for users defined in auth.users (comma separated)"));
    this.defaults.add(new Setting("auth.client.id", "Google client ID", "", "Google client ID to use for this server"));

    this.defaults.forEach((settingDefault) -> {
      final Optional<Setting> existingSetting = this.settingRepository.findByKey(settingDefault.getKey());
      if (existingSetting.isPresent()) {
        settingDefault.setId(existingSetting.get().getId());
        settingDefault.setValue(existingSetting.get().getValue());
      }
      final Optional<String> environmentValue = this.getEnvironmentValue(settingDefault.getKey());
      if (environmentValue.isPresent()) {
        settingDefault.setValue(environmentValue.get());
      }
      this.settingRepository.save(settingDefault);
    });
  }

  public String get(final String key) {
    return this.settingRepository.findByKey(key).get().getValue();
  }

  private Optional<String> getEnvironmentValue(final String key) {
    return Optional.ofNullable(this.environment.getProperty(key));
  }
}
