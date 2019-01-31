package de.wasenweg.komix.preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PreferencesService {

    private final Map<String, Preference> defaults = new HashMap<String, Preference>();

    private final PreferenceRepository preferenceRepository;

    @Autowired
    public PreferencesService(final PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
        defaults.put("comics.path", new Preference("comics.path", "Path", "sample", "Path to your comic library"));
        defaults.forEach((key, value) -> {
            if (!this.preferenceRepository.findByKey(key).isPresent()) {
                this.preferenceRepository.save(value);
            }
        });
    }

    public String get(final String key) {
        return this.preferenceRepository.findByKey(key).get().getValue();
    }
}
