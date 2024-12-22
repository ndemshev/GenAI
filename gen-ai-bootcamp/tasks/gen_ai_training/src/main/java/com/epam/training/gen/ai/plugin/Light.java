package com.epam.training.gen.ai.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Light {

  private String id;
  private boolean state;

  public void setState(boolean state) {
    this.state = state;
  }
}