package de.wasenweg.alfred.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class SettingsService {

  private final ArrayList<Setting> defaults = new ArrayList<Setting>();

  private final SettingRepository settingRepository;
  private final Environment environment;

  @Autowired
  public SettingsService(final SettingRepository settingRepository, final Environment environment) {
    this.settingRepository = settingRepository;
    this.environment = environment;

    // Built in defaults:
    this.defaults.add(new Setting("comics.path", "Path", "/comics", "Path to your comic library"));
    this.defaults.add(new Setting("auth.users", "Users", "", "Users authorized to access this server"));
    this.defaults.add(new Setting("auth.client.id", "Google client ID", "", "Google client ID to use for this server"));

    this.defaults.forEach((settingDefault) -> {
      // Defaults passed in via a property override built in defaults
      final Optional<String> environmentValue = this.getEnvironmentValue(settingDefault.getKey());
      if (environmentValue.isPresent()) {
        settingDefault.setValue(environmentValue.get());
      }

      // Defaults are ignored if values for the given key already exist
      final Optional<Setting> hasSetting = this.settingRepository.findByKey(settingDefault.getKey());
      if (!hasSetting.isPresent()) {
        this.settingRepository.save(settingDefault);
      } else {
        final Setting setting = hasSetting.get();
        if (environmentValue.isPresent()) {
          setting.setValue(environmentValue.get());
          this.settingRepository.save(setting);
        }
      }
    });
  }

  private Optional<String> getEnvironmentValue(final String key) {
    return Optional.ofNullable(this.environment.getProperty(key));
  }

  public String get(final String key) {
    return this.settingRepository.findByKey(key).get().getValue();
  }
}
