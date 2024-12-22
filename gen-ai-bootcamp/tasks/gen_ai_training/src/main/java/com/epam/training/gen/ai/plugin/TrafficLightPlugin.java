package com.epam.training.gen.ai.plugin;

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import java.util.HashMap;
import java.util.Map;

public class TrafficLightPlugin {

  private final Map<String, Light> lights = new HashMap<>();

  public TrafficLightPlugin() {
    lights.put("red", new Light("red", true));
    lights.put("yellow", new Light("yellow", false));
    lights.put("green", new Light("green", false));
  }

  @DefineKernelFunction(name = "get_traffic_lights_current_state", description = "Gets a list of traffic_lights and their current state")
  public LightState getCurrentState() {
    return new LightState(lights.values());
  }

  @DefineKernelFunction(name = "change_state", description = "Changes the state of the traffic light")
  public Light changeState(
      @KernelFunctionParameter(name = "id", description = "The ID of the light to change") String id,
      @KernelFunctionParameter(name = "state", description = "The new state of the light in traffic light") boolean state) {
    if (!lights.containsKey(id)) {
      throw new IllegalArgumentException("Light not found");
    }

    lights.get(id).setState(state);

    return lights.get(id);
  }
}
