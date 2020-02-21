package de.wasenweg.alfred.unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public final class TestHelper {

  private TestHelper() {
    throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static JsonNode parseJson(final String path) throws IOException {
    return new ObjectMapper().readTree(new File("src/test/resources/" + path));
  }
}
