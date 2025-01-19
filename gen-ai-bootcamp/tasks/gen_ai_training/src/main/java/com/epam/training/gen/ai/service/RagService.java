package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.model.PromptDto;
import com.epam.training.gen.ai.model.PromptSettingsDto;
import com.epam.training.gen.ai.model.RagInputDto;
import com.epam.training.gen.ai.model.SearchResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RagService {

  private static final String DEFAULT_MODEL = "gpt-35-turbo";

  private static final String SYSTEM_MESSAGE = """
          Act as an assistant and answer questions only from provided CONTEXT. If no answer found, 
          please return "No Data provided" 
      """;

  private static final String PROMPT = """
      CONTEXT:
      %s
      QUESTION:
      %s
      """;

  private EmbeddingsService embeddingsService;

  private PromptService promptService;

  @Autowired
  public RagService(EmbeddingsService embeddingsService, PromptService promptService) {
    this.embeddingsService = embeddingsService;
    this.promptService = promptService;
  }

  public String initializeContext(String fileName) throws ExecutionException, InterruptedException {
    String content;
    try {
      content = readFileContent(fileName);
    } catch (IOException e) {
      return String.format("Failed to load content for file %s, reason %s", fileName,
          e.getMessage());
    }

    embeddingsService.saveEmbeddings(content);

    return "Success";
  }

  public String retrieveAnswer(RagInputDto ragInputDto)
      throws ExecutionException, InterruptedException {

    List<SearchResponse> searchResponses = embeddingsService.search(ragInputDto.getInput());
    String context = searchResponses.stream()
        .map(SearchResponse::getText)
        .reduce((a, b) -> a + "\n" + b)
        .orElse("No data");

    log.info("Context: {}", context);

    PromptDto promptDto = new PromptDto();
    promptDto.setModelId(DEFAULT_MODEL);
    promptDto.setSettings(new PromptSettingsDto(0.0, 0.0));
    promptDto.setPrompt(String.format(PROMPT, context, ragInputDto.getInput()));

    String result = promptService.getModelResponse(ragInputDto.isCleanHistory(), SYSTEM_MESSAGE,
        promptDto);

    log.info("Result = {}", result);

    return result;
  }


  private String readFileContent(String fileName) throws IOException {
    String result;
    try (InputStream is = RagService.class.getClassLoader().getResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      result = reader.lines()
          .collect(Collectors.joining(System.lineSeparator()));
    }
    return result;
  }

}
