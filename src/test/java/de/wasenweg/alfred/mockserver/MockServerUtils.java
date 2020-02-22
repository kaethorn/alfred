package de.wasenweg.alfred.mockserver;

import de.wasenweg.alfred.unit.TestHelper;

import org.mockserver.integration.ClientAndServer;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.io.IOException;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpClassCallback.callback;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.Parameter.param;

public final class MockServerUtils {

  private static ClientAndServer server;

  private MockServerUtils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static void startServer() throws IOException {
    server = startClientAndServer(1080);

    server.when(
        request()
          .withMethod("GET")
          .withPath("/search/")
          .withQueryStringParameters(
              param("offset", "0"),
              param("query", "Batman")
          )
    ).respond(
        response()
          .withStatusCode(200)
          .withHeaders(
            header("Content-Type", "application/json; charset=utf-8")
          )
          .withBody(TestHelper.parseJson("search-batman.json").toString())
    );

    server.when(
        request()
          .withMethod("GET")
          .withPath("/issues/")
          .withQueryStringParameters(
              param("filter", "volume:796")
          )
    ).respond(
        callback().withCallbackClass("de.wasenweg.alfred.mockserver.MockServer$BatmanIssuesCallback")
    );

    server.when(
        request()
          .withMethod("GET")
          .withPath("/issue/4000-224555/")
    ).respond(
        response()
          .withStatusCode(200)
          .withHeaders(
            header("Content-Type", "application/json; charset=utf-8")
          )
          .withBody(TestHelper.parseJson("batman-701.json").toString())
    );
  }

  public static void stop() {
    server.stop();
  }

  public static class BatmanIssuesCallback implements ExpectationResponseCallback {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
      final String offsetString = httpRequest.getQueryStringParameters().getValues("offset").get(0);
      final int page = Integer.parseInt(offsetString) / 100 + 1;
      try {
        return response()
                 .withStatusCode(200)
                 .withHeaders(
                   header("Content-Type", "application/json; charset=utf-8")
                 )
                 .withBody(TestHelper.parseJson("issues-batman-page" + page + ".json").toString());
      } catch (final IOException exception) {
        return response()
                 .withStatusCode(500);
      }
    }
  }
}
