package com.epam.training.gen.ai.model;

import lombok.Data;

@Data
public class RagInputDto {

  private String input;
  private boolean cleanHistory;

}
