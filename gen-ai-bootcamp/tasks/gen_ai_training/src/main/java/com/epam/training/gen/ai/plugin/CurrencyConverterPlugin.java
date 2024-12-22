package com.epam.training.gen.ai.plugin;

import static java.util.Optional.ofNullable;

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CurrencyConverterPlugin {

  private Map<String, Map<String, Double>> currencyMap = Map.of(
      "UAH", Map.of("EUR", 0.023, "USD", 0.024),
      "EUR", Map.of("UAH", 44.1, "USD", 1.05),
      "USD", Map.of("UAH", 41.75, "EUR", 0.95)
  );

  @DefineKernelFunction(name = "convert_currency", description = "Convert currency")
  public double convertCurrency(
      @KernelFunctionParameter(description = "currency from which to convert", name = "fromCurrency") String fromCurrency,
      @KernelFunctionParameter(description = "currency to which convert", name = "toCurrency") String toCurrency,
      @KernelFunctionParameter(description = "amount to convert", name = "amount") double amount) {

    double coefficient = ofNullable(currencyMap.get(fromCurrency))
        .map(map -> map.get(toCurrency))
        .orElse(0.0);

    return amount * coefficient;
  }
}
