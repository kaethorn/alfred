package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.settings.Setting;
import de.wasenweg.alfred.settings.SettingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

@Component
public class IntegrationTestHelper {

  @Autowired
  private SettingRepository settingsRepository;

  public Flux<String> triggerScan(final int port) {
    return WebClient
        .create("http://localhost:" + port + "/api")
        .get()
        .uri("/scan-progress")
        .accept(TEXT_EVENT_STREAM)
        .retrieve()
        .bodyToFlux(new ParameterizedTypeReference<String>() { });
  }

  public void setComicsPath(final String comicsPath) {
    final Setting comicsPathSetting = this.settingsRepository.findByKey("comics.path").get();
    comicsPathSetting.setValue(comicsPath);
    this.settingsRepository.save(comicsPathSetting);
  }
}
