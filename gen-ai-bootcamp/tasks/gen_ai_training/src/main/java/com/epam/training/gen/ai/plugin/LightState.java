package com.epam.training.gen.ai.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Data;

@Data
public class LightState {

  private List<Light> lightList = new ArrayList<>();

  public LightState(Collection lights) {
    lightList.addAll(lights);
  }

}
