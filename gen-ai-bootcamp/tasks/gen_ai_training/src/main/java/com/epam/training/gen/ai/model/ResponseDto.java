package com.epam.training.gen.ai.model;

import java.util.List;
import lombok.Data;

@Data
public class ResponseDto {

  private List<BookDto> books;
}
