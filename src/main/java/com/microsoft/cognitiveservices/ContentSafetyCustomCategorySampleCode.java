//
// Copyright (c) Microsoft. All rights reserved.
// To learn more, please visit the documentation - Quickstart: Azure Content Safety: https://aka.ms/acsstudiodoc
//

package com.microsoft.cognitiveservices;

import org.json.JSONObject;
import okhttp3.*;
import java.io.IOException;

public class ContentSafetyCustomCategorySampleCode {
    public static void main(String[] args) {

        // Replace <your_subscription_key> and <your_endpoint>
        String subscriptionKey = System.getenv("CONTENT_SAFETY_SUBSCRIPTION_KEY");
        if (subscriptionKey == null || subscriptionKey.isEmpty()) {
            throw new RuntimeException("Environment variable CONTENT_SAFETY_SUBSCRIPTION_KEY is not set or is empty");
        }
        String endpoint = "https://contentsafetyrpza.cognitiveservices.azure.com";

        // String to analyze
        String text = "Recognizing symptoms of dehydration";

        // Custom categories name and version
        String customCategoryName = "survival";
        int customCategoryVersion = 1; // replace the version here

        OkHttpClient client = new OkHttpClient();
        Headers.Builder headersBuilder = new Headers.Builder();
        headersBuilder.add("Ocp-Apim-Subscription-Key", subscriptionKey);

        // Setup the RestAPI request
        String analyzeApiUrl = endpoint + "/contentsafety/text:analyzeCustomCategory?api-version=2024-02-15-preview";

        JSONObject requestBody = new JSONObject();
        requestBody.put("text", text);
        requestBody.put("categoryName", customCategoryName);
        requestBody.put("version", customCategoryVersion);
        RequestBody body = RequestBody.create(requestBody.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(analyzeApiUrl)
                .post(body)
                .headers(headersBuilder.build())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                System.out.println(jsonResponse);
            } else {
                throw new RuntimeException("Unexpected code " + response);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
