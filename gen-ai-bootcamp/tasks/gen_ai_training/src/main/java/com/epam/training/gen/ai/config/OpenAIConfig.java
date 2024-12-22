package com.epam.training.gen.ai.config;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.epam.training.gen.ai.service.ModelService;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class OpenAIConfig {

  @Value("${client-openai-key}")
  private String apiKey;

  @Value("${client-openai-endpoint}")
  private String openAIEndpoint;

  @Value("${client-openai-deployment-name}")
  private String modelId;

  @Value("${client-azureopenai-deployment-name}")
  private String azureOpenAIDeploymentName;

  @Bean
  public OpenAIAsyncClient openAIClient() {
    return new OpenAIClientBuilder()
        .credential(new AzureKeyCredential(apiKey))
        .endpoint(openAIEndpoint)
        .buildAsyncClient();
  }

  @Bean
  @Qualifier("openAiChatCompletion")
  public OpenAIChatCompletion chatCompletionService() {
    return OpenAIChatCompletion.builder()
        .withModelId(modelId)
        .withOpenAIAsyncClient(openAIClient())
        .build();
  }

  @Bean
  @Qualifier("azureOpenAiChatCompletion")
  public OpenAIChatCompletion chatCompletionServiceAzure() {
    return OpenAIChatCompletion.builder()
        .withModelId(azureOpenAIDeploymentName)
        .withOpenAIAsyncClient(openAIClient())
        .build();
  }

  @Bean
  @Scope(value = "prototype")
  @Qualifier("configurableChatCompletion")
  public OpenAIChatCompletion chatCompletionServiceWithDeployment(String deploymentName) {
    return OpenAIChatCompletion.builder()
        .withModelId(deploymentName)
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

  @Bean
  public ModelService modelService() {
    return new ModelService(apiKey, openAIEndpoint);
  }
}
