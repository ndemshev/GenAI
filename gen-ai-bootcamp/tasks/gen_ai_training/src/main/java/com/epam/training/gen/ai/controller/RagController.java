package com.epam.training.gen.ai.controller;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.epam.training.gen.ai.model.FileInputDto;
import com.epam.training.gen.ai.model.RagInputDto;
import com.epam.training.gen.ai.service.RagService;
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
@RequestMapping("/api/rag")
@Slf4j
public class RagController {

  private RagService ragService;

  @Autowired
  public RagController(RagService ragService) {
    this.ragService = ragService;
  }

  @PostMapping(value = "/context")
  public ResponseEntity<String> initializeContext(@RequestBody FileInputDto fileInputDto)
      throws ExecutionException, InterruptedException {

    log.info("Initialize context for the file: {}", fileInputDto.getFilename());

    String result = ragService.initializeContext(
        isBlank(fileInputDto.getFilename()) ? "RAG.txt" : fileInputDto.getFilename());

    return new ResponseEntity<>(result, HttpStatus.CREATED);
  }

  @PostMapping(value = "/question")
  public ResponseEntity<String> searchEmbeddings(@RequestBody RagInputDto ragInputDto)
      throws ExecutionException, InterruptedException {

    log.info("Received RAG request with question: {}", ragInputDto.getInput());

    String ragResponses = ragService.retrieveAnswer(ragInputDto);

    log.info("Obtained RAG search response: {}", ragResponses);

    return ResponseEntity.ok().body(ragResponses);
  }
}
