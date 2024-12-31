package com.epam.training.gen.ai.service;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;
import static io.qdrant.client.WithPayloadSelectorFactory.enable;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.EmbeddingItem;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.epam.training.gen.ai.model.SearchResponse;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Points;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.SearchPoints;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmbeddingsService {

  private static final String COLLECTION_NAME = "search_text";

  private static final String ADA_MODEL_ID = "text-embedding-ada-002";
  private final OpenAIAsyncClient openAIAsyncClient;
  private final QdrantClient qdrantClient;


  @Autowired
  public EmbeddingsService(OpenAIAsyncClient openAIAsyncClient, QdrantClient qdrantClient) {
    this.openAIAsyncClient = openAIAsyncClient;
    this.qdrantClient = qdrantClient;
  }

  @PostConstruct
  public void init() throws ExecutionException, InterruptedException {
    createCollection(COLLECTION_NAME);
  }

  public List<EmbeddingItem> buildEmbeddings(String text) {
    var embeddingsList = getEmbeddings(text);

    log.info("Created embeddings, size={}", embeddingsList.size());

    return embeddingsList;
  }

  public void saveEmbeddings(String text) throws ExecutionException, InterruptedException {
    var embeddingsList = getEmbeddings(text);

    var points = getPoints(embeddingsList);

    var pointStructs = new ArrayList<PointStruct>();
    points.forEach(listPoint -> {
      var uniqueId = UUID.randomUUID();
      var pointStruct = Points.PointStruct.newBuilder()
          .setId(id(uniqueId))
          .setVectors(vectors(listPoint))
          .putAllPayload(Map.of("info", value(text)))
          .build();
      pointStructs.add(pointStruct);
    });
    Points.UpdateResult result = qdrantClient
        .upsertAsync(COLLECTION_NAME, pointStructs)
        .get();

    log.info("Embeddings are saved, qdgrant status={}", result.getStatus());
  }

  private static List<List<Float>> getPoints(List<EmbeddingItem> embeddingsList) {
    var points = new ArrayList<List<Float>>();
    embeddingsList.forEach(
        embeddingItem -> {
          var values = new ArrayList<>(embeddingItem.getEmbedding());
          points.add(values);
        });
    return points;
  }

  public List<SearchResponse> search(String text) throws ExecutionException, InterruptedException {
    var embeddingsList = getEmbeddings(text);

    var points = new ArrayList<Float>();
    embeddingsList.forEach(
        embeddingItem -> points.addAll(embeddingItem.getEmbedding()));

    List<Points.ScoredPoint> scoredPointList = qdrantClient
        .searchAsync(
            SearchPoints.newBuilder()
                .setCollectionName(COLLECTION_NAME)
                .addAllVector(points)
                .setWithPayload(enable(true))
                .setLimit(1)
                .build())
        .get();

    log.info("Obtained search result, scoredPointList.size={}", scoredPointList.size());

    return scoredPointList.stream()
        .map(scoredPoint -> {

          Map<String, String> payload = new HashMap<>();
          scoredPoint.getPayloadMap().forEach((s, value) -> payload.put(s, value.getStringValue()));

          return new SearchResponse(scoredPoint.getId().getNum(),
              scoredPoint.getVersion(),
              scoredPoint.getScore(),
              payload);
        }).collect(Collectors.toList());
  }

  public void createCollection(String collectionName)
      throws ExecutionException, InterruptedException {
    boolean isExists = qdrantClient.collectionExistsAsync(collectionName).get();

    if (!isExists) {
      boolean result =
          qdrantClient.createCollectionAsync(collectionName,
                  Collections.VectorParams.newBuilder()
                      .setDistance(Collections.Distance.Cosine)
                      .setSize(1536)
                      .build())
              .get().getResult();

      log.info("Collection creation result={}", result);
    }
  }

  private List<EmbeddingItem> getEmbeddings(String text) {
    var options = new EmbeddingsOptions(List.of(text));
    return openAIAsyncClient
        .getEmbeddings(ADA_MODEL_ID, options)
        .block()
        .getData();
  }
}
