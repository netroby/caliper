/*
 * Copyright (C) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.caliper.runner.worker.trial;

/**
 * The scheduling policy for a particular trial.
 *
 * <p>TODO(lukes): Currently this is extremely simple. Trials can be scheduled in parallel with
 * other trials or not. In the future, this should use some kind of cost modeling.
 */
public enum TrialSchedulingPolicy {
  PARALLEL,
  SERIAL
}
