package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.model.InputDto;
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
@RequestMapping("/api/chat")
public class ChatController {

  private PromptService promptService;

  @Autowired
  public ChatController(PromptService promptService) {
    this.promptService = promptService;
  }

  @PostMapping("/books")
  public ResponseEntity<String> getChatResponse(@RequestBody InputDto inputDto) {

    // Experiment with this value (e.g., 0.3, 1.0)
    String response = promptService.generateChatResponse(inputDto.getInput(),
        inputDto.getSettings() != null ? inputDto.getSettings().getTemperature() : 0.2,
        inputDto.getSettings() != null ? inputDto.getSettings().getTop() : 0.9);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/history")
  public ResponseEntity<Map<String, String>> getChatHistory() {

    return new ResponseEntity<>(promptService.getChatHistory(), HttpStatus.OK);
  }
}