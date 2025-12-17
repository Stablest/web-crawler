package crawl;

import com.google.gson.JsonSyntaxException;
import com.stablest.web_crawler.common.exception.NotFoundException;
import com.stablest.web_crawler.common.exception.ValidationException;
import com.stablest.web_crawler.crawl.CrawlController;
import com.stablest.web_crawler.crawl.CrawlService;
import com.stablest.web_crawler.crawl.dto.CrawlPublicResult;
import com.stablest.web_crawler.crawl.dto.output.CreateCrawlOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spark.Request;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CrawlControllerTest {
    @Mock
    private CrawlService crawlService;
    @Mock
    private Request request;
    @InjectMocks
    private CrawlController controller;

    private void stubCrawlServiceCreateCrawl(String id) {
        CreateCrawlOutput createCrawlOutput = new CreateCrawlOutput(id);
        when(crawlService.createCrawl(any(String.class))).thenReturn(createCrawlOutput);
    }

    private void stubCrawlServiceGetCrawl(Throwable throwable) {
        when(crawlService.getCrawl(any(String.class))).thenThrow(throwable);
    }

    private void stubCrawlServiceGetCrawl(CrawlPublicResult output) {
        when(crawlService.getCrawl(any(String.class))).thenReturn(output);
    }

    private void stubRequestBody(String body) {
        when(request.body()).thenReturn(body);
    }

    private void stubRequestParams(String paramName, String output) {
        when(request.params(paramName)).thenReturn(output);
    }

    @Test
    void givenMissingKeyword_whenCreateCrawl_thenThrowsValidationException() {
        String invalidInput = "{\"key\": \"java\"}";

        stubRequestBody(invalidInput);

        assertThrows(ValidationException.class, () -> controller.createCrawl(request, null));
        verifyNoInteractions(crawlService);
    }

    @Test
    void givenNonExistentCrawlId_whenGetCrawlResult_thenThrowsNotFound() {
        stubRequestParams("id", "crawl123");
        stubCrawlServiceGetCrawl(new NotFoundException());

        assertThrows(NotFoundException.class, () -> controller.getCrawlResult(request, null));
    }

    @Test
    void givenInvalidJson_whenCreateCrawl_thenThrowsJsonSyntaxException() {
        String input = "{";
        stubRequestBody(input);
        assertThrows(JsonSyntaxException.class, () -> controller.createCrawl(request, null));
    }

    @Test
    void givenValidInput_whenCreateCrawl_thenReturnsCreatedCrawl() {
        String input = "{\"keyword\": \"java\"}";

        stubRequestBody(input);
        stubCrawlServiceCreateCrawl("1");

        CreateCrawlOutput expectedOutput = new CreateCrawlOutput("1");
        CreateCrawlOutput output = controller.createCrawl(request, null);
        assertEquals(expectedOutput, output);
    }

    @Test
    void givenExistingCrawlId_whenGetCrawlResult_thenReturnsCrawlResult() {
        CrawlPublicResult expectedOutput = new CrawlPublicResult("crawl123", "done", Set.of("http://base_url.com"));

        stubRequestParams("id", "crawl123");
        stubCrawlServiceGetCrawl(expectedOutput);

        CrawlPublicResult output = controller.getCrawlResult(request, null);
        assertEquals(expectedOutput, output);
    }
}
