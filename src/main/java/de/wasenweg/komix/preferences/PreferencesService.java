package de.wasenweg.komix.preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PreferencesService {

    private final Map<String, Preference> defaults = new HashMap<String, Preference>();

    private final PreferenceRepository preferenceRepository;
    private final Environment environment;

    @Autowired
    public PreferencesService(final PreferenceRepository preferenceRepository, final Environment environment) {
        this.preferenceRepository = preferenceRepository;
        this.environment = environment;

        defaults.put("comics.path", new Preference("comics.path", "Path", "/comics", "Path to your comic library"));
        defaults.forEach((key, preferenceDefault) -> {
            final Optional<String> environmentValue = this.getEnvironmentValue(key);
            if (environmentValue.isPresent()) {
                preferenceDefault.setValue(environmentValue.get());
            }

            final Optional<Preference> hasPreference = this.preferenceRepository.findByKey(key);
            if (!hasPreference.isPresent()) {
                this.preferenceRepository.save(preferenceDefault);
            } else {
                final Preference preference = hasPreference.get();
                if (environmentValue.isPresent()) {
                    preference.setValue(environmentValue.get());
                    this.preferenceRepository.save(preference);
                }
            }
        });
    }

    private Optional<String> getEnvironmentValue(final String key) {
        return Optional.ofNullable(this.environment.getProperty(key));
    }

    public String get(final String key) {
        return this.preferenceRepository.findByKey(key).get().getValue();
    }
}
