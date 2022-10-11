package de.wasenweg.alfred.integration;

import de.wasenweg.alfred.TestUtil;
import de.wasenweg.alfred.settings.Setting;
import de.wasenweg.alfred.settings.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class IntegrationTestHelper {

  private final SettingRepository settingsRepository;

  public void setComicsPath(final String comicsPath, final File temp) {
    TestUtil.copyResources(temp, comicsPath);
    final Setting comicsPathSetting = this.settingsRepository.findByKey("comics.path").get();
    comicsPathSetting.setValue(temp.getAbsolutePath());
    this.settingsRepository.save(comicsPathSetting);
  }
}
