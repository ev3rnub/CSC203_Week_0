package org.verboseStory.api;
// local classes
import org.verboseStory.engine.GameEngine;
import org.verboseStory.engine.GameEngineStaticHolder;
// std
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
 * Endpoint   : {@code http://localhost:11434/v1/chat/completions}
 * For future use.
 */
public final class LocalOllama_API {

    //Ollama base URL
    private static final String API_BASE_URL = "http://localhost:11434/v1";

    //Model name that the VHSH13 uses as a local model in Ollama.
    private static final String MODEL = "DMVHSH13_v0:latest";

    //Holds messages to preserve context.
    private static final List<JsonObject> messages = new ArrayList<>();

    //Public entry point used by GameEngine.
    public static void invokeResponseFromLocal(String initialPrompt) {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        // try catch
        try {
            // if initialPrompt is equal to BEGIN_GAME, define a new json object with the variable name of init, add
            // properties to init and then add it to the messages list. Send the request and await the response, and
            // send the response to our game window. Create another json object, define its properties and add it to
            // the messages list.
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

            // Main Game loop.
            while (GameEngine.STARTED) {
                //define a reference to our input queue.
                BlockingQueue<String> q = GameEngineStaticHolder.engine.inputQueue;
                // take the top most object of type string and define it as userInput.
                String userInput = q.take(); // blocks
                // if the below strings exist, exit main loop
                if (userInput.equalsIgnoreCase("q")
                        || userInput.equalsIgnoreCase("quit")
                        || userInput.equalsIgnoreCase("exit")) {
                    GameEngine.STARTED = false;
                    break;
                }
                // Send the players input to the GameWindow
                GameEngine.white_chat_output(GameEngine.playerKey + ": " + userInput);
                // Define a json object to hold our playerInput
                JsonObject userMsg = new JsonObject();
                userMsg.addProperty("role", "user");
                userMsg.addProperty("content", userInput);
                messages.add(userMsg);

                // Send the players response to the endpoint.
                String resp = sendRequest(client, gson);
                GameEngine.white_chat_output("***** StoryMaster *****");
                GameEngine.cyan_chat_output(resp);
                // Define a json object to hold the LLM's response
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
     * @param client                the HttpClient used to perform the call
     * @param gson                  a Gson instance for (de)serialisation
     * @return                      the assistant's textual response
     * @throws IOException          on network / protocol errors
     * @throws InterruptedException if the thread is interrupted while waiting
     */
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
