package com.epam.training.gen.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PromptSettingsDto {

  double temperature;
  double top;
}
