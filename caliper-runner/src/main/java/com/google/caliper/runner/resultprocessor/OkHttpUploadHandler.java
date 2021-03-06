/*
 * Copyright (C) 2016 Google Inc.
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

package com.google.caliper.runner.resultprocessor;

import com.google.caliper.model.Trial;
import com.google.common.base.Optional;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

/** Upload handler that uses an OkHttp client. */
final class OkHttpUploadHandler implements ResultsUploader.UploadHandler {
  private final OkHttpClient client;

  OkHttpUploadHandler(OkHttpClient client) {
    this.client = client;
  }

  @Override
  public boolean upload(
      URI uri, String content, String mediaType, Optional<UUID> apiKey, Trial trial) {
    HttpUrl url = HttpUrl.get(uri);
    if (apiKey.isPresent()) {
      url = url.newBuilder().addQueryParameter("key", apiKey.get().toString()).build();
    }

    RequestBody body = RequestBody.create(MediaType.parse(mediaType), content);
    Request request = new Request.Builder().url(url).post(body).build();

    try {
      Response response = client.newCall(request).execute();
      if (response.isSuccessful()) {
        return true;
      } else {
        ResultsUploader.logger.fine("Failed upload response: " + response.code());
      }
    } catch (IOException e) {
      ResultsUploader.logUploadFailure(trial, e);
    }
    return false;
  }
}
