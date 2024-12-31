package com.epam.training.gen.ai.controller;

import com.azure.ai.openai.models.EmbeddingItem;
import com.epam.training.gen.ai.model.SearchResponse;
import com.epam.training.gen.ai.service.EmbeddingsService;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/embeddings")
@Slf4j
public class SearchController {

  private EmbeddingsService embeddingsService;

  @Autowired
  public SearchController(EmbeddingsService embeddingsService) {
    this.embeddingsService = embeddingsService;
  }

  @PostMapping(value = "/build")
  ResponseEntity<List<EmbeddingItem>> buildEmbeddings(@RequestBody String text) {
    log.info("Build embeddings for text: {}", text);

    List<EmbeddingItem> embeddings = embeddingsService.buildEmbeddings(text);
    return ResponseEntity.ok().body(embeddings);
  }

  @PostMapping(value = "/save")
  public ResponseEntity<Void> saveEmbeddings(@RequestBody String text)
      throws ExecutionException, InterruptedException {

    log.info("Save embeddings for text: {}", text);

    embeddingsService.saveEmbeddings(text);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping(value = "/search")
  public ResponseEntity<List<SearchResponse>> searchEmbeddings(@RequestBody String input)
      throws ExecutionException, InterruptedException {

    log.info("Received search request with input: {}", input);

    List<SearchResponse> searchResponses = embeddingsService.search(input);

    log.info("Obtained search response, size={}", searchResponses.size());

    return ResponseEntity.ok().body(searchResponses);
  }

}
