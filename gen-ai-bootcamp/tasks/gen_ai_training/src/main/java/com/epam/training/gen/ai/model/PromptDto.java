package com.epam.training.gen.ai.model;

import lombok.Data;

@Data
public class PromptDto {
  private String prompt;
  private String modelId;
  private PromptSettingsDto settings;
}
