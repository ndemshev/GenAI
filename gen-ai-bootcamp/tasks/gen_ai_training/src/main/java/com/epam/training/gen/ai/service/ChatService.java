package com.epam.training.gen.ai.service;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatService {

  private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);

  private Kernel kernel;

  private ChatHistory chatHistory;

  private ChatCompletionService chatCompletionService;

  private KernelFunction<String> chat;

  @Autowired
  public ChatService(Kernel kernel,
      @Qualifier("openAiChatCompletion") ChatCompletionService chatCompletionService) {
    this.kernel = kernel;
    this.chatCompletionService = chatCompletionService;

    chatHistory = new ChatHistory("You are an assistant");

    chat = KernelFunction.<String>createFromPrompt(
            """ 
                User: {{$request}}
                """)
        .build();
  }

  public String getChatResponse(String prompt, double temperature, double top) {
    temperature = normalize(temperature);
    top = normalize(top);

    chatHistory.addUserMessage(prompt);

    List<ChatMessageContent<?>> responseList = chatCompletionService.
        getChatMessageContentsAsync(chatHistory, kernel, invocationContext(temperature, top))
        .block();

    chatHistory.addAll(responseList);

    String modelResponse = responseList.stream()
        .filter(response -> AuthorRole.ASSISTANT.equals(response.getAuthorRole()))
        .map(ChatMessageContent::getContent)
        .filter(Objects::nonNull)
        .collect(Collectors.joining("\n"));

    LOG.info("Model response = {}", modelResponse);

    return modelResponse;
  }

  public String generateChatResponse(String request, double temperature, double top) {

    LOG.info("User > {}", request);

    KernelFunctionArguments arguments = KernelFunctionArguments.builder()
        .withVariable("request", request)
        .withVariable("history", chatHistory)
        .build();

    FunctionResult<String> chatResult = chat.invokeAsync(kernel)
        .withArguments(arguments)
        .block();

    String response = chatResult.getResult();

    LOG.info("Assistant > {}", response);

    // Append to history
    chatHistory.addUserMessage(request);
    chatHistory.addAssistantMessage(response);

    return response;
  }

  public Map<String, String> getChatHistory() {
    Map<String, String> historyMap = new LinkedHashMap<>();

    for (ChatMessageContent msg : chatHistory.getMessages()) {
      historyMap.put(msg.getAuthorRole().name(), msg.getContent());
    }

    return historyMap;
  }

  private PromptExecutionSettings getPromptExecutionSettings(double temperature, double top) {
    return PromptExecutionSettings.builder()
        .withMaxTokens(500)
        .withTemperature(temperature)
        .withTopP(top)
        .build();
  }

  private double normalize(double value) {
    if (value < 0 || value > 1) {
      return 0;
    }
    return value;
  }

  public InvocationContext invocationContext(double temperature, double top) {
    return InvocationContext.builder()
        .withPromptExecutionSettings(getPromptExecutionSettings(temperature, top))
        .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
        .build();
  }
}
