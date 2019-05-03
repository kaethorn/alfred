package de.wasenweg.alfred.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SettingsService {

    private final Map<String, Setting> defaults = new HashMap<String, Setting>();

    private final SettingRepository settingRepository;
    private final Environment environment;

    @Autowired
    public SettingsService(final SettingRepository settingRepository, final Environment environment) {
        this.settingRepository = settingRepository;
        this.environment = environment;

        defaults.put("comics.path", new Setting("comics.path", "Path", "/comics", "Path to your comic library"));
        defaults.forEach((key, settingDefault) -> {
            final Optional<String> environmentValue = this.getEnvironmentValue(key);
            if (environmentValue.isPresent()) {
                settingDefault.setValue(environmentValue.get());
            }

            final Optional<Setting> hasSetting = this.settingRepository.findByKey(key);
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
