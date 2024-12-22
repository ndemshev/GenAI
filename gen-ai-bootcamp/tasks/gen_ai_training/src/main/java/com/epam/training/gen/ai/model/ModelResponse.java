package com.epam.training.gen.ai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class ModelResponse {

  @JsonProperty(value = "data")
  List<ModelDto> models;
}
