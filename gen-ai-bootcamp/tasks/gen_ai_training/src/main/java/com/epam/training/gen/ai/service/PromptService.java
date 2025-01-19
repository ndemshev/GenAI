package com.epam.training.gen.ai.service;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import com.epam.training.gen.ai.model.PromptDto;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PromptService {

  private static final Logger LOG = LoggerFactory.getLogger(PromptService.class);

  private static final String DEFAULT_SYSTEM_MESSAGE = """
          Act as an assistant and answer question.
      """;

  private ObjectProvider<OpenAIChatCompletion> chatCompletionProvider;

  private ChatHistory chatHistory;

  @Autowired
  public PromptService(@Qualifier("configurableChatCompletion")
  ObjectProvider<OpenAIChatCompletion> chatCompletionProvider) {
    this.chatCompletionProvider = chatCompletionProvider;
    chatHistory = new ChatHistory();
  }

  public String getModelResponse(boolean cleanHistory, String systemMessage, PromptDto promptDto) {
    if (cleanHistory) {
      chatHistory = new ChatHistory();
    }
    return getModelResponse(systemMessage, promptDto);
  }


  public String getModelResponse(String systemMessage, PromptDto promptInput) {
    LOG.info("Prompt={}, model={}", promptInput.getPrompt(), promptInput.getModelId());

    OpenAIChatCompletion openAIChatCompletion = chatCompletionProvider.getObject(
        promptInput.getModelId());

    Kernel kernel = getKernel(openAIChatCompletion);

    double temperature = normalize(promptInput.getSettings().getTemperature());
    double top = normalize(promptInput.getSettings().getTop());

    if (isEmpty(systemMessage)) {
      systemMessage = DEFAULT_SYSTEM_MESSAGE;
    }

    chatHistory.addSystemMessage(systemMessage);
    chatHistory.addUserMessage(promptInput.getPrompt());

    List<ChatMessageContent<?>> responseList = openAIChatCompletion.getChatMessageContentsAsync(
            chatHistory, kernel,
            getInvocationContext(promptInput.getModelId(), temperature, top))
        .block();

    chatHistory.addAll(responseList);

    String modelResponse = responseList.stream()
        .filter(response -> AuthorRole.ASSISTANT.equals(response.getAuthorRole()))
        .map(ChatMessageContent::getContent)
        .collect(Collectors.joining(","));

    LOG.info("Model response = {}", modelResponse);

    return modelResponse;
  }

  public Map<String, String> getChatHistory() {
    Map<String, String> historyMap = new LinkedHashMap<>();

    for (ChatMessageContent msg : chatHistory.getMessages()) {
      historyMap.put(msg.getAuthorRole().name(), msg.getContent());
    }

    return historyMap;
  }

  public Kernel getKernel(OpenAIChatCompletion chatCompletion) {
    return Kernel.builder()
        .withAIService(OpenAIChatCompletion.class, chatCompletion)
        .build();
  }

  public InvocationContext getInvocationContext(String modelId, double temperature, double top) {
    return InvocationContext.builder()
        .withPromptExecutionSettings(PromptExecutionSettings.builder()
            .withModelId(modelId)
            .withTemperature(temperature)
            .withTopP(top)
            .build())
        .build();
  }

  private double normalize(double value) {
    if (value < 0 || value > 1) {
      return 0;
    }
    return value;
  }
}
