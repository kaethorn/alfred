package de.wasenweg.alfred.unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class TestHelper {

  private TestHelper() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static JsonNode parseJson(final String path) throws IOException {
    return new ObjectMapper().readTree(new File("src/test/resources/" + path));
  }

  public static String readString(final String path) throws IOException {
    return Files.readString(Paths.get("src/test/resources/" + path), StandardCharsets.US_ASCII);
  }
}
