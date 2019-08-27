package de.wasenweg.alfred;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class TestHelper {

  public static JsonNode parseJson(final String path) throws IOException {
    return new ObjectMapper().readTree(new File("src/test/resources/" + path));
  }
}
