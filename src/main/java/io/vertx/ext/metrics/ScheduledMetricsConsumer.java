/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.ext.metrics;

import io.vertx.core.Vertx;
import io.vertx.core.impl.Arguments;
import io.vertx.core.json.JsonObject;
import io.vertx.core.metrics.Measured;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class ScheduledMetricsConsumer {

  private static final BiPredicate<String, JsonObject> FILTER_ANY = (name, metric) -> true;
  private final Vertx vertx;
  private final Measured measured;
  private BiPredicate<String, JsonObject> filter = FILTER_ANY;
  private volatile long timerId = -1;

  public ScheduledMetricsConsumer(Vertx vertx) {
    this(vertx, vertx);
  }

  public ScheduledMetricsConsumer(Vertx vertx, Measured measured) {
    Objects.requireNonNull(vertx, "no null vertx accepted");
    Objects.requireNonNull(measured, "no null measured accepted");
    this.vertx = vertx;
    this.measured = measured;
  }

  public ScheduledMetricsConsumer filter(BiPredicate<String, JsonObject> filter) {
    Objects.requireNonNull(filter, "no null filter accepted");
    if (timerId != -1) throw new IllegalStateException("Cannot set filter while metrics consumer is running.");
    if (FILTER_ANY.equals(this.filter)) {
      this.filter = filter;
    } else {
      this.filter = this.filter.or(filter);
    }
    return this;
  }

  public void start(long delay, TimeUnit unit, BiConsumer<String, JsonObject> consumer) {
    Arguments.require(delay > 0, "delay must be > 0");
    Objects.requireNonNull(unit, "no null unit accepted");
    Objects.requireNonNull(consumer, "no null consumer accepted");
    timerId = vertx.setPeriodic(unit.toMillis(delay), tid -> {
      measured.metrics().forEach((name, metric) -> {
        if (filter.test(name, metric)) {
          consumer.accept(name, metric);
        }
      });
    });
  }

  public void stop() {
    vertx.cancelTimer(timerId);
    timerId = -1;
  }
}
