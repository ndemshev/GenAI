package com.epam.training.gen.ai.controller;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.epam.training.gen.ai.model.BookDto;
import com.epam.training.gen.ai.model.InputDto;
import com.epam.training.gen.ai.model.ResponseDto;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.implementation.CollectionUtil;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

  private String apiKey;

  private String apiEndpoint;

  private String modelId;

  private OpenAIAsyncClient client;
  private ChatCompletionService chatGPT;

  public ChatController(@Value("${client-openai-key}") String apiKey,
      @Value("${client-openai-endpoint}") String apiEndpoint,
      @Value("${client-openai-deployment-name}") String modelId) {

    this.apiKey = apiKey;
    this.apiEndpoint = apiEndpoint;
    this.modelId = modelId;

    client = new OpenAIClientBuilder()
        .credential(new AzureKeyCredential(apiKey))
        .endpoint(apiEndpoint)
        .buildAsyncClient();

    chatGPT = OpenAIChatCompletion.builder()
        .withModelId(modelId)
        .withOpenAIAsyncClient(client)
        .build();
  }

  @PostMapping("/books")
  public ResponseEntity<ResponseDto> getChatResponse(@RequestBody InputDto inputDto) {

    ChatHistory chatHistory = new ChatHistory("You are a librarian, expert about books");

    chatHistory.addUserMessage(inputDto.getInput());

    ResponseDto response = new ResponseDto();
    response.setBooks(GPTReply(chatGPT, chatHistory));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  private List<BookDto> GPTReply(ChatCompletionService chatGPT, ChatHistory chatHistory) {
    var reply = chatGPT.getChatMessageContentsAsync(chatHistory, null, null);

    String message = reply
        .mapNotNull(CollectionUtil::getLastOrNull)
        .doOnNext(streamingChatMessage -> {
        })
        .map(ChatMessageContent::getContent)
        .block();
    chatHistory.addAssistantMessage(message);

    return parseResponse(message);
  }

  private List<BookDto> parseResponse(String message) {
    List<BookDto> result = new ArrayList<>();

    if (message.indexOf(":") >= 0) {
      String booksStr = message.substring(message.indexOf(":") + 1).trim();

      String lines[] = booksStr.split("[\\r\\n]+");

      int idx = 0;
      for (int i = 0; i < lines.length - 1; i++) {
        String[] book = lines[i].split("\\.");
        if (!book[0].trim().isBlank()) {
          idx = Integer.valueOf(book[0]);
        } else {
          idx++;
        }

        result.add(new BookDto(idx, book[1].trim()));
      }
    }
    return result;
  }
}