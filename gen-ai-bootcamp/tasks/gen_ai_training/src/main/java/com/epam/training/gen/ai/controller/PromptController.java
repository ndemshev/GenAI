package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.model.PromptDto;
import com.epam.training.gen.ai.service.PromptService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prompt")
public class PromptController {

  private PromptService promptService;

  @Autowired
  public PromptController(PromptService promptService) {
    this.promptService = promptService;
  }

  @PostMapping
  public ResponseEntity<String> getModelResponse(@RequestBody PromptDto promptInput) {
    String response = promptService.getModelResponse(promptInput);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/history")
  public ResponseEntity<Map<String, String>> getChatHistory() {

    return new ResponseEntity<>(promptService.getChatHistory(), HttpStatus.OK);
  }
}
