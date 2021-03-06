/*
 * Copyright (c) 2011-2013 The original author or authors
 *  ------------------------------------------------------
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *      The Eclipse Public License is available at
 *      http://www.eclipse.org/legal/epl-v10.html
 *
 *      The Apache License v2.0 is available at
 *      http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.ext.dropwizard;

import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;

import java.util.Random;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class MetricsOptionsTest extends VertxTestBase {

  @Test
  public void testOptions() {
    DropwizardMetricsOptions options = new DropwizardMetricsOptions();

    assertFalse(options.isEnabled());
    assertEquals(options, options.setEnabled(true));
    assertTrue(options.isEnabled());
    assertNull(options.getRegistryName());

    // Test metrics get enabled if jmx is set to true
    options.setEnabled(false);
    assertFalse(options.isJmxEnabled());
    assertEquals(options, options.setJmxEnabled(true));
    assertTrue(options.isJmxEnabled());
    assertTrue(options.isEnabled());

    assertNull(options.getJmxDomain());
    assertEquals("foo", options.setJmxDomain("foo").getJmxDomain());

    assertNull(options.getRegistryName());
    assertEquals("bar", options.setRegistryName("bar").getRegistryName());
  }

  @Test
  public void testCopyOptions() {
    DropwizardMetricsOptions options = new DropwizardMetricsOptions();

    Random rand = new Random();
    boolean metricsEnabled = rand.nextBoolean();
    boolean jmxEnabled = rand.nextBoolean();
    String jmxDomain = TestUtils.randomAlphaString(100);
    String name = TestUtils.randomAlphaString(100);
    options.setEnabled(metricsEnabled);
    options.setJmxEnabled(jmxEnabled);
    options.setJmxDomain(jmxDomain);
    options.setRegistryName(name);
    options = new DropwizardMetricsOptions(options);
    assertEquals(metricsEnabled || jmxEnabled, options.isEnabled());
    assertEquals(jmxEnabled, options.isJmxEnabled());
    assertEquals(jmxDomain, options.getJmxDomain());
    assertEquals(name, options.getRegistryName());
  }

  @Test
  public void testJsonOptions() {
    DropwizardMetricsOptions options = new DropwizardMetricsOptions(new JsonObject());
    assertFalse(options.isEnabled());
    assertFalse(options.isJmxEnabled());
    assertNull(options.getJmxDomain());
    assertNull(options.getRegistryName());
    Random rand = new Random();
    boolean metricsEnabled = rand.nextBoolean();
    boolean jmxEnabled = rand.nextBoolean();
    String jmxDomain = TestUtils.randomAlphaString(100);
    String registryName = TestUtils.randomAlphaString(100);
    options = new DropwizardMetricsOptions(new JsonObject().
        put("enabled", metricsEnabled).
        put("registryName", registryName).
        put("jmxEnabled", jmxEnabled).
        put("jmxDomain", jmxDomain)
    );
    assertEquals(metricsEnabled, options.isEnabled());
    assertEquals(registryName, options.getRegistryName());
    assertEquals(jmxEnabled, options.isJmxEnabled());
    assertEquals(jmxDomain, options.getJmxDomain());
  }
}
