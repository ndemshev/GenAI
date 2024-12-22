package com.epam.training.gen.ai.config;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

  @Value("${client-openai-key}")
  private String apiKey;

  @Value("${client-openai-endpoint}")
  private String openAIEndpoint;

  @Value("${client-openai-deployment-name}")
  private String modelId;

  @Bean
  public OpenAIAsyncClient openAIClient() {
    return new OpenAIClientBuilder()
        .credential(new AzureKeyCredential(apiKey))
        .endpoint(openAIEndpoint)
        .buildAsyncClient();
  }

  @Bean
  public OpenAIChatCompletion chatCompletionService() {
    return OpenAIChatCompletion.builder()
        .withModelId(modelId)
        .withOpenAIAsyncClient(openAIClient())
        .build();
  }

  @Bean
  public Kernel kernel() {
    return Kernel.builder()
        .withAIService(ChatCompletionService.class, OpenAIChatCompletion.builder()
            .withModelId(modelId)
            .withOpenAIAsyncClient(openAIClient())
            .build())
        .build();
  }
}
