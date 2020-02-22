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

  @PostConstruct
  public void setup() {
    // Built in defaults:
    this.defaults.add(new Setting("comics.path", "Path", "/comics", "Path to your comic library"));
    this.defaults.add(new Setting("comics.comicVine.ApiKey", "Comi Vine API key", "", "Comic Vine API key from https://comicvine.gamespot.com/api/"));
    this.defaults.add(new Setting("auth.users", "Users", "", "Users authorized to access this server"));
    this.defaults.add(new Setting("auth.client.id", "Google client ID", "", "Google client ID to use for this server"));

    this.defaults.forEach((settingDefault) -> {
      // Defaults passed in via a property override built in defaults
      final Optional<String> environmentValue = this.getEnvironmentValue(settingDefault.getKey());
      if (environmentValue.isPresent()) {
        settingDefault.setValue(environmentValue.get());
      }

      // Defaults are ignored if values for the given key already exist
      final Optional<Setting> maybeSetting = this.settingRepository.findByKey(settingDefault.getKey());
      if (maybeSetting.isPresent()) {
        if (environmentValue.isPresent()) {
          final Setting setting = maybeSetting.get();
          setting.setValue(environmentValue.get());
          this.settingRepository.save(setting);
        }
      } else {
        this.settingRepository.save(settingDefault);
      }
    });
  }

  public String get(final String key) {
    return this.settingRepository.findByKey(key).get().getValue();
  }

  private Optional<String> getEnvironmentValue(final String key) {
    return Optional.ofNullable(this.environment.getProperty(key));
  }
}
