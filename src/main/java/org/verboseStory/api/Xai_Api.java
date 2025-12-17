package org.verboseStory.api;

// my classes
import org.verboseStory.engine.GameEngine;

//std
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.List;

//Ext
import com.google.gson.*;

//NOTE: For detailed comments, see LocalOllama_API.java files comments.
// Grok-3 xAI api connector
public final class Xai_Api {

    private static final String API_BASE_URL = "https://api.x.ai/v1";
    private static String MODEL = "grok-3";


    //Holds the last N messages to preserve context.
    public static List<JsonObject> messages = new ArrayList<>();

    //Public entry point used by GameEngine
    public static String invokeResponseFromGrok(String initialPrompt) {
        MODEL = "grok-4-1-fast-non-reasoning";
        String apiKey = System.getenv("xAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            GameEngine.printOutput(Color.RED, "XAI_API_CONN", "NO xAI_API_KEY, check if ENV variable exists!");
        }

        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        String someResponse = "";
        try {
            if (GameEngine.INITIAL){
                // ---- system instruction ---------------------------------
                String systemInstruction = GameEngine.buildStoryGuide();
                JsonObject systemMsg = new JsonObject();
                systemMsg.addProperty("role", "system");
                systemMsg.addProperty("content", systemInstruction);
                messages.add(systemMsg);

                JsonObject init = new JsonObject();
                init.addProperty("role", "user");
                init.addProperty("content", initialPrompt);
                messages.add(init);

                someResponse = sendRequest(client, gson, apiKey);
                GameEngine.printOutput(Color.WHITE, "XAI_API_CONN", "---------------------------External StoryMaster ---------------------------\n");
                JsonObject assistant = new JsonObject();
                assistant.addProperty("role", "assistant");
                assistant.addProperty("content", someResponse);
                messages.add(assistant);
            }
            if (!GameEngine.INITIAL) {
                JsonObject userMsg = new JsonObject();
                userMsg.addProperty("role", "user");
                userMsg.addProperty("content", initialPrompt);
                messages.add(userMsg);

                someResponse = sendRequest(client, gson, apiKey);
                GameEngine.printOutput(Color.WHITE, "XAI_API_CONN", "----------------------External StoryMaster ---------------------------\n");
                JsonObject assistantMsg = new JsonObject();
                assistantMsg.addProperty("role", "assistant");
                assistantMsg.addProperty("content", someResponse);
                messages.add(assistantMsg);
            }
        } catch (IOException | InterruptedException e) {
            GameEngine.printOutput(Color.RED, "XAI_API_CONN","Error: " + e.getMessage());
            e.printStackTrace();
        }
        return someResponse;
    }
    //takes a HTTP client, some json and an API key.
    private static String sendRequest(HttpClient client, Gson gson, String apiKey) throws IOException, InterruptedException {
        JsonObject body = new JsonObject();
        body.addProperty("model", MODEL);
        JsonArray msgs = new JsonArray();

        // Send only the last N messages (N = 5 is a sane default)
        // NOTE: During testing I noticed that after some play time the LLM may lose track of
        // what the player was originally doing.
        final int N = 20;
        int start = Math.max(0, messages.size() - N);
        for (int i = start; i < messages.size(); i++) {
            msgs.add(messages.get(i));
        }

        body.add("messages", msgs);
        body.addProperty("max_tokens", 70000);
        body.addProperty("temperature", 1.2);

        String jsonBody = gson.toJson(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("API request failed: " + response.statusCode() + " – " + response.body());
        }
        JsonObject json = gson.fromJson(response.body(), JsonObject.class);
        return json.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
    }

    public static String enrichData(String somePrompt, String someSystemInstruction){
        MODEL = "grok-4-1-fast-non-reasoning";
        String apiKey = System.getenv("xAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            GameEngine.printOutput(Color.RED, "XAI_API_CONN", "NO xAI_API_KEY, check if ENV variable exists!");
        }

        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        String someResponse = "";
        try {
            // ---- system instruction ---------------------------------
            JsonObject systemMsg = new JsonObject();
            systemMsg.addProperty("role", "system");
            systemMsg.addProperty("content", someSystemInstruction);
            messages.add(systemMsg);

            JsonObject init = new JsonObject();
            init.addProperty("role", "user");
            init.addProperty("content", somePrompt);
            messages.add(init);

            someResponse = sendRequest(client, gson, apiKey);
            GameEngine.printOutput(Color.WHITE, "XAI_API_CONN", "---------------------------External Story Guide ---------------------------\n");
            JsonObject assistant = new JsonObject();
            assistant.addProperty("role", "assistant");
            assistant.addProperty("content", someResponse);
            messages.add(assistant);
        } catch (IOException | InterruptedException e) {
            GameEngine.printOutput(Color.RED, "XAI_API_CONN","Error: " + e.getMessage());
            e.printStackTrace();
        }
        return someResponse;
    }
}