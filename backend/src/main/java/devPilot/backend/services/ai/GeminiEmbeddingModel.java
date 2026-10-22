package devPilot.backend.services.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.AbstractEmbeddingModel;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Primary
@Slf4j
public class GeminiEmbeddingModel extends AbstractEmbeddingModel {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final int dimensions;

    public GeminiEmbeddingModel(
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.embedding.options.model:gemini-embedding-001}") String model,
            @Value("${spring.ai.vectorstore.pgvector.dimensions:1536}") int dimensions) {
        this.apiKey = apiKey;
        this.model = model;
        this.dimensions = dimensions;
        this.objectMapper = new ObjectMapper();
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/openai")
                .build();
    }

    @Override
    public float[] embed(Document document) {
        return embed(document.getText());
    }

    @Override
    public int dimensions() {
        return this.dimensions;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        List<String> instructions = request.getInstructions();
        if (instructions == null || instructions.isEmpty()) {
            return new EmbeddingResponse(List.of());
        }

        Map<String, Object> body = Map.of(
                "model", model,
                "input", instructions,
                "dimensions", dimensions
        );

        String responseJson = restClient.post()
                .uri("/embeddings")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode dataArray = root.get("data");
            List<Embedding> embeddings = new ArrayList<>();

            if (dataArray != null && dataArray.isArray()) {
                for (int i = 0; i < dataArray.size(); i++) {
                    JsonNode item = dataArray.get(i);
                    JsonNode vectorNode = item.get("embedding");
                    if (vectorNode != null && vectorNode.isArray()) {
                        float[] vector = new float[vectorNode.size()];
                        for (int j = 0; j < vectorNode.size(); j++) {
                            vector[j] = (float) vectorNode.get(j).asDouble();
                        }
                        embeddings.add(new Embedding(vector, i));
                    }
                }
            }

            return new EmbeddingResponse(embeddings);
        } catch (Exception e) {
            log.error("Failed to parse Gemini embedding response: {}", responseJson, e);
            throw new RuntimeException("Failed to parse Gemini embedding response: " + e.getMessage(), e);
        }
    }
}
