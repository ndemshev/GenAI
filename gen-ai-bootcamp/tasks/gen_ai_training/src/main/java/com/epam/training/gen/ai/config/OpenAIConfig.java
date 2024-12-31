package com.epam.training.gen.ai.config;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.epam.training.gen.ai.plugin.CurrencyConverterPlugin;
import com.epam.training.gen.ai.plugin.TrafficLightPlugin;
import com.epam.training.gen.ai.service.ModelService;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
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
  @Scope(value = "prototype")
  @Qualifier("configurableChatCompletion")
  public OpenAIChatCompletion chatCompletionServiceWithModel(String modelId) {
    return OpenAIChatCompletion.builder()
        .withModelId(modelId)
        .withOpenAIAsyncClient(openAIClient())
        .build();
  }

  @Bean
  public Kernel kernel(
      @Qualifier("openAiChatCompletion") ChatCompletionService chatCompletionService) {
    return Kernel.builder()
        .withAIService(ChatCompletionService.class, chatCompletionService)
        .withPlugin(KernelPluginFactory.createFromObject(new CurrencyConverterPlugin(),
            "CurrencyConverterPlugin"))
        .withPlugin(
            KernelPluginFactory.createFromObject(new TrafficLightPlugin(), "TrafficLightPlugin"))
        .build();
  }

  @Bean
  public ModelService modelService() {
    return new ModelService(apiKey, openAIEndpoint);
  }

  @Bean
  public InvocationContext invocationContext() {
    return InvocationContext.builder()
        .withPromptExecutionSettings(PromptExecutionSettings.builder()
            .withTemperature(0.2)
            .build())
        .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
        .build();
  }

}
