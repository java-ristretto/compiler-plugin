package io.github.tprado.ristretto.plugins.intellij;

import org.jetbrains.plugins.gradle.service.project.AbstractProjectResolverExtension;

import java.util.logging.Logger;

/*
 * This should probably be the place where it is decided if the plugin is going to be enabled
 * for a particular module:
 *
 * Does the annotation preprocessing path contains a ristretto jar?
 * yes -> check for ${project.basePath}/ristretto.properties
 * no  -> disable ristretto for the module
 */
public class RistrettoGradleProjectResolver extends AbstractProjectResolverExtension {

  private static final Logger LOG = Logger.getLogger(RistrettoGradleProjectResolver.class.getName());

  public RistrettoGradleProjectResolver() {
    LOG.info("resolver loaded");
  }

}
