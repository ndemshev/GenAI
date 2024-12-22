package com.epam.training.gen.ai.service;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatService {

  private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);

  @Autowired
  private Kernel kernel;

  private ChatHistory chatHistory;

  private KernelFunction<String> chat;

  public ChatService() {
    chatHistory = new ChatHistory("You are a librarian, expert about books");

    chat = KernelFunction.<String>createFromPrompt(
            """ 
                User: {{$request}}
                """)
        .build();
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
}
