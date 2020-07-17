package io.github.tprado.ristretto.plugins.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class RistrettoStartupActivity implements StartupActivity {

  private static final Logger LOG = Logger.getLogger(RistrettoStartupActivity.class.getName());

  @Override
  public void runActivity(@NotNull Project project) {
    LOG.info("project base path" + project.getBasePath());
  }
}
