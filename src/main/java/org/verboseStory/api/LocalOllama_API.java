package org.verboseStory.api;

import com.google.gson.*;
import org.verboseStory.engine.GameEngine;
import org.verboseStory.engine.GameEngineStaticHolder;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/** Mirrros XaiAPI
 * Model used: {@code DMVHSH13_v0:latest}
 * Endpoint   : {@code http://localhost:11434/v1/chat/completions}
 */
public final class LocalOllama_API {

    /** Ollama base URL (default local installation). */
    private static final String API_BASE_URL = "http://localhost:11434/v1";

    /** Model name that the game uses. */
    private static final String MODEL = "DMVHSH13_v0:latest";

    /** Holds the last N messages to preserve context. */
    private static final List<JsonObject> messages = new ArrayList<>();

    /** Public entry point used by {@link GameEngine}. */
    public static void invokeResponseFromGrok(String initialPrompt) {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        try {
            // -----------------------------------------------------------------
            // OPTIONAL: if you still need a static system prompt you can add it
            // manually here (the previous buildSysInstruct() method was removed).
            // -----------------------------------------------------------------
            // Example:
            // JsonObject systemMsg = new JsonObject();
            // systemMsg.addProperty("role", "system");
            // systemMsg.addProperty("content", "YOUR SYSTEM PROMPT HERE");
            // messages.add(systemMsg);

            if ("BEGIN_GAME".equalsIgnoreCase(initialPrompt)) {
                JsonObject init = new JsonObject();
                init.addProperty("role", "user");
                init.addProperty("content", initialPrompt);
                messages.add(init);

                String resp = sendRequest(client, gson);
                GameEngine.white_chat_output("***** StoryMaster *****");
                GameEngine.white_chat_output(resp);

                JsonObject assistant = new JsonObject();
                assistant.addProperty("role", "assistant");
                assistant.addProperty("content", resp);
                messages.add(assistant);
            }

            // -----------------------------------------------------------------
            // Main interaction loop
            // -----------------------------------------------------------------
            while (GameEngine.STARTED) {
                BlockingQueue<String> q = GameEngineStaticHolder.engine.inputQueue;
                String userInput = q.take(); // blocks

                if (userInput.equalsIgnoreCase("q")
                        || userInput.equalsIgnoreCase("quit")
                        || userInput.equalsIgnoreCase("exit")) {
                    GameEngine.STARTED = false;
                    break;
                }

                GameEngine.white_chat_output(GameEngine.playerKey + ": " + userInput);

                JsonObject userMsg = new JsonObject();
                userMsg.addProperty("role", "user");
                userMsg.addProperty("content", userInput);
                messages.add(userMsg);

                String resp = sendRequest(client, gson);
                GameEngine.white_chat_output("***** StoryMaster *****");
                GameEngine.white_chat_output(resp);

                JsonObject assistantMsg = new JsonObject();
                assistantMsg.addProperty("role", "assistant");
                assistantMsg.addProperty("content", resp);
                messages.add(assistantMsg);
            }
        } catch (IOException | InterruptedException e) {
            GameEngine.red_chat_output("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sends a chat‑completion request to the local Ollama server.
     *
     * @param client the {@link HttpClient} used to perform the call
     * @param gson   a {@link Gson} instance for (de)serialisation
     * @return the assistant's textual response
     * @throws IOException          on network / protocol errors
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    private static String sendRequest(HttpClient client, Gson gson) throws IOException, InterruptedException {
        JsonObject body = new JsonObject();
        body.addProperty("model", MODEL);

        // Keep only the last N messages (N = 5 is a sane default)
        final int N = 5;
        JsonArray msgs = new JsonArray();
        int start = Math.max(0, messages.size() - N);
        for (int i = start; i < messages.size(); i++) {
            msgs.add(messages.get(i));
        }
        body.add("messages", msgs);

        // Ollama‑specific parameters (feel free to tweak)
        body.addProperty("max_tokens", 20000);
        body.addProperty("temperature", 0.5);
        body.addProperty("stream", false);

        String jsonBody = gson.toJson(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/chat/completions"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Ollama request failed: " + response.statusCode()
                    + " – " + response.body());
        }

        JsonObject json = gson.fromJson(response.body(), JsonObject.class);
        return json.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
    }
}
