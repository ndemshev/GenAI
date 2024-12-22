package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.model.ModelDto;
import com.epam.training.gen.ai.model.ModelResponse;
import com.epam.training.gen.ai.service.ModelService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/model")
public class ModelController {

  private ModelService modelService;

  @Autowired
  public ModelController(ModelService modelService) {
    this.modelService = modelService;
  }

  @GetMapping
  public ResponseEntity<ModelResponse> getSupportedModels() {
    return new ResponseEntity<>(modelService.getModels(), HttpStatus.OK);
  }
}
