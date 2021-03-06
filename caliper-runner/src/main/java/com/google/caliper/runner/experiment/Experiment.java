/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.caliper.runner.experiment;

import com.google.auto.value.AutoValue;
import com.google.caliper.bridge.ExperimentSpec;
import com.google.caliper.model.BenchmarkSpec;
import com.google.caliper.runner.instrument.Instrument.InstrumentedMethod;
import com.google.caliper.runner.target.Target;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import java.util.Map;

/**
 * A single "premise" for making benchmark measurements: which class and method to invoke, which
 * target to use, which choices for user parameters and vmArguments to fill in and which instrument
 * to use to measure. A caliper run will compute all possible scenarios using {@link
 * ExperimentSelector}, and will run one or more trials of each.
 */
@AutoValue
public abstract class Experiment {

  /** Creates a new {@link Experiment}. */
  public static Experiment create(
      int id,
      InstrumentedMethod instrumentedMethod,
      Map<String, String> userParameters,
      Target target) {
    BenchmarkSpec benchmarkSpec = createBenchmarkSpec(instrumentedMethod, userParameters);
    return new AutoValue_Experiment(
        id, instrumentedMethod, ImmutableSortedMap.copyOf(userParameters), target, benchmarkSpec);
  }

  /**
   * Returns the ID of this experiment.
   *
   * <p>This is just a simple number to help the runner identify the experiment in results sent back
   * from workers. It is not included in data uploaded to the webapp server.
   */
  public abstract int id();

  /** Returns the instrumented method for this experiment. */
  public abstract InstrumentedMethod instrumentedMethod();

  /** Returns the selection of user parameter values for this experiment. */
  public abstract ImmutableSortedMap<String, String> userParameters();

  /** Returns the target this experiment is to be run on. */
  public abstract Target target();

  /** Returns the {@link BenchmarkSpec} for this experiment. */
  public abstract BenchmarkSpec benchmarkSpec();

  /** Returns an {@link ExperimentSpec} representing this experiment. */
  public final ExperimentSpec toExperimentSpec() {
    return new ExperimentSpec(
        id(),
        instrumentedMethod().type(),
        instrumentedMethod().workerOptions(),
        benchmarkSpec(),
        ImmutableList.copyOf(instrumentedMethod().benchmarkMethod().parameterTypes()));
  }

  private static BenchmarkSpec createBenchmarkSpec(
      InstrumentedMethod method, Map<String, String> userParameters) {
    return new BenchmarkSpec.Builder()
        .className(method.benchmarkMethod().declaringClass())
        .methodName(method.benchmarkMethod().name())
        .addAllParameters(userParameters)
        .build();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper("")
        .add("instrument", instrumentedMethod().instrument())
        .add("benchmarkMethod", instrumentedMethod().benchmarkMethod().name())
        .add("target", target().name())
        .add("parameters", userParameters())
        .toString();
  }
}
