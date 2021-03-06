/*
 * Copyright (c) 2011-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package examples;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.docgen.Source;
import io.vertx.ext.dropwizard.MatchType;
import io.vertx.ext.dropwizard.Match;
import io.vertx.ext.dropwizard.MetricsService;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;

import java.util.Map;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@Source
public class MetricsExamples {

  public void setup() {
    Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
        new DropwizardMetricsOptions().setEnabled(true)
    ));
  }

  public void setupJMX() {
    Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
        new DropwizardMetricsOptions().setJmxEnabled(true)
    ));
  }

  public void setupJMXWithDomain() {
    Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
        new DropwizardMetricsOptions().
            setJmxEnabled(true).
            setJmxDomain("mydomain")
    ));
  }

  public void setupMonitoredHandlers() {
    Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
        new DropwizardMetricsOptions().
            setEnabled(true).
            addMonitoredEventBusHandler(
                new Match().setValue("some-address")).
            addMonitoredEventBusHandler(
                new Match().setValue("business-.*").setType(MatchType.REGEX))
    ));
  }

  public void setupMonitoredUris() {
    Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
        new DropwizardMetricsOptions().
            setEnabled(true).
            addMonitoredHttpServerUri(
                new Match().setValue("/")).
            addMonitoredHttpServerUri(
                new Match().setValue("/foo/.*").setType(MatchType.REGEX))
    ));
  }

  public void naming1(Vertx vertx, MetricsService metricsService) {
    JsonObject metrics = metricsService.getMetricsSnapshot(vertx);
    metrics.getJsonObject("vertx.eventbus.handlers");
  }

  public void naming2(Vertx vertx, MetricsService metricsService) {
    EventBus eventBus = vertx.eventBus();
    JsonObject metrics = metricsService.getMetricsSnapshot(eventBus);
    metrics.getJsonObject("handlers");
  }

  public void example1(Vertx vertx) {
    MetricsService metricsService = MetricsService.create(vertx);
    JsonObject metrics = metricsService.getMetricsSnapshot(vertx);
    System.out.println(metrics);
  }

  public void example3(Vertx vertx) {
    MetricsService metricsService = MetricsService.create(vertx);
    HttpServer server = vertx.createHttpServer();
    // set up server
    JsonObject metrics = metricsService.getMetricsSnapshot(server);
  }
}
