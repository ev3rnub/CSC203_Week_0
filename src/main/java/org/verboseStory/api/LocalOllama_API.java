package org.verboseStory.api;
// local classes
import org.verboseStory.engine.GameEngine;
import org.verboseStory.engine.GameEngineStaticHolder;
// std
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
// ext
import com.google.gson.*;

/** Mirrros XaiAPI
 * Model used: {@code DMVHSH13_v0:latest}
 * Model file used: DMVHSH13_v0.modelfile
 * Endpoint   : {@code http://localhost:11434/v1/chat/completions}
 */
public final class LocalOllama_API {

    //Ollama base URL
    private static final String API_BASE_URL = "http://localhost:11434/v1";

    //Model name that the VHSH13 uses as a local model in Ollama.
    private static final String MODEL = "DMVHSH13_v0:latest";

    //Holds messages to preserve context.
    private static final List<JsonObject> messages = new ArrayList<>();

    //Public entry point used by GameEngine.
    public static String invokeResponseFromLocal(String initialPrompt) {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        String someResponse = "";
        try {
            if (GameEngine.INITIAL) {
                JsonObject init = new JsonObject();
                init.addProperty("role", "user");
                init.addProperty("content", initialPrompt);
                messages.add(init);

                someResponse = sendRequest(client, gson);
                GameEngine.printOutput(Color.WHITE, "LocalOllamaAPI", "---------------------------Local StoryMaster(0) --------------------------- ");
                JsonObject assistant = new JsonObject();
                assistant.addProperty("role", "assistant");
                assistant.addProperty("content", someResponse);
                messages.add(assistant);
                GameEngine.INITIAL = false;
                GameEngine.currentClient = client;
            }

            if (!GameEngine.INITIAL) {
                JsonObject userMsg = new JsonObject();
                userMsg.addProperty("role", "user");
                userMsg.addProperty("content", initialPrompt);
                messages.add(userMsg);

                // Send the players response to the endpoint.
                someResponse = sendRequest(GameEngine.currentClient, gson);
                GameEngine.printOutput(Color.WHITE, "LocalOllamaAPI", "----------------------Local StoryMaster ---------------------------");
                // Define a json object to hold the LLM's response
                JsonObject assistantMsg = new JsonObject();
                assistantMsg.addProperty("role", "assistant");
                assistantMsg.addProperty("content", someResponse);
                messages.add(assistantMsg);
            }
        } catch (IOException | InterruptedException e) {
            GameEngine.printOutput(Color.RED, "LocalOllama_API", "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return someResponse;
    }

    private static String sendRequest(HttpClient client, Gson gson) throws IOException, InterruptedException {
        //define a json object to store our properties in; used in http
        JsonObject body = new JsonObject();
        body.addProperty("model", MODEL);
        // Keep only the last N messages
        final int N = 5;
        // create a json array named msgs.
        JsonArray msgs = new JsonArray();
        //define the index start for the last N.
        // IE grab the last 5 messages.
        int start = Math.max(0, messages.size() - N);
        // @ start, increment until max size.
        for (int i = start; i < messages.size(); i++) {
            msgs.add(messages.get(i));
        }
        body.add("messages", msgs);

        // Ollama‑specific parameters
        body.addProperty("max_tokens", 20000);
        body.addProperty("temperature", 0.5);
        body.addProperty("stream", false);

        // use gson library to cast to json.
        String jsonBody = gson.toJson(body);

        // define our request properties.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/chat/completions"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // using the client, send the request and when received store the response in response.
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // if not http ok
        if (response.statusCode() != 200) {
            throw new IOException("Ollama request failed: " + response.statusCode()
                    + " – " + response.body());
        }
//      Take the response body and use gson to create a json object and assign it the name of json
        JsonObject json = gson.fromJson(response.body(), JsonObject.class);
//      Return a json array of LLM output.
        return json.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
    }
}
