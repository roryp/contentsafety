package com.microsoft.cognitiveservices;

import org.json.JSONObject;
import okhttp3.*;
import java.io.IOException;

public class ContentSafetyCustomCategorySampleCode {
    private static final String ENDPOINT = "https://contentsafetyrpza.cognitiveservices.azure.com";
    private static final String API_VERSION = "2024-02-15-preview";
    private static final String TEXT_TO_ANALYZE = "Recognizing symptoms of dehydration";
    private static final String CUSTOM_CATEGORY_NAME = "survival";
    private static final int CUSTOM_CATEGORY_VERSION = 1;

    public static void main(String[] args) {
        String subscriptionKey = System.getenv("CONTENT_SAFETY_SUBSCRIPTION_KEY");
        if (subscriptionKey == null || subscriptionKey.isEmpty()) {
            throw new RuntimeException("Environment variable CONTENT_SAFETY_SUBSCRIPTION_KEY is not set or is empty");
        }

        OkHttpClient client = new OkHttpClient();
        Headers headers = new Headers.Builder()
                .add("Ocp-Apim-Subscription-Key", subscriptionKey)
                .build();

        String analyzeApiUrl = String.format("%s/contentsafety/text:analyzeCustomCategory?api-version=%s", ENDPOINT, API_VERSION);

        JSONObject requestBody = new JSONObject();
        requestBody.put("text", TEXT_TO_ANALYZE);
        requestBody.put("categoryName", CUSTOM_CATEGORY_NAME);
        requestBody.put("version", CUSTOM_CATEGORY_VERSION);

        RequestBody body = RequestBody.create(requestBody.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(analyzeApiUrl)
                .post(body)
                .headers(headers)
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
