package com.epam.training.gen.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(
    ignoreUnknown = true
)
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
  private long id;
  private long version;
  private float score;
  private Map<String, String> payload;
  private String text;
}
