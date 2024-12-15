package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.model.ModelResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ModelService {

  private static final Logger LOG = LoggerFactory.getLogger(ModelService.class);

  private String apiKey;

  private String endpoint;

  private RestTemplate restTemplate;

  @Autowired
  public ModelService(String apiKey, String endpoint) {
    this.apiKey = apiKey;
    this.endpoint = endpoint;
    restTemplate = new RestTemplate();
  }

  public ModelResponse getModels() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Api-Key", apiKey);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<ModelResponse> response = restTemplate.exchange(endpoint + "openai/deployments",
        HttpMethod.GET, entity, ModelResponse.class);
    LOG.info("Models = {}", response);
    return response.getBody();
  }

}
