package controller;

import com.stablest.web_crawler.controller.CrawlController;
import com.stablest.web_crawler.dto.CrawlPublicResult;
import com.stablest.web_crawler.dto.output.CreateCrawlOutput;
import com.stablest.web_crawler.exception.NotFoundException;
import com.stablest.web_crawler.exception.ValidationException;
import com.stablest.web_crawler.service.CrawlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import spark.Request;
import spark.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CrawlControllerTest {
    @Mock
    private CrawlService crawlService = Mockito.mock(CrawlService.class);
    @Mock
    private Request request = Mockito.mock(Request.class);
    @Mock
    private Response response = Mockito.mock(Response.class);
    private CrawlController controller;

    @BeforeEach
    void setUp() throws Exception {
        controller = CrawlController.getInstance();
        var crawlField = CrawlController.class.getDeclaredField("crawlService");
        crawlField.setAccessible(true);
        crawlField.set(controller, crawlService);
    }

    @Test
    void testCreateCrawl_ValidInput() {
        String input = "{\"keyword\": \"java\"}";
        when(request.body()).thenReturn(input);
        CreateCrawlOutput expectedOutput = new CreateCrawlOutput("crawl123");
        when(crawlService.createCrawl("java")).thenReturn(expectedOutput);
        CreateCrawlOutput output = controller.createCrawl(request, null);
        verify(crawlService).createCrawl("java");
        assertEquals(expectedOutput, output);
    }

    @Test
    void testCreateCrawl_InvalidInput() {
        String invalidInput = "{\"key\": \"java\"}";
        when(request.body()).thenReturn(invalidInput);
        assertThrows(
                ValidationException.class,
                () -> controller.createCrawl(request, null)
        );
        verifyNoInteractions(crawlService);
    }

    @Test
    void testGetCrawl_ValidAndFoundInput() {
        when(request.params("id")).thenReturn("crawl123");
        CrawlPublicResult expectedOutput = new CrawlPublicResult("crawl123", "done", List.of("http://base_url.com"));
        when(crawlService.getCrawl("crawl123")).thenReturn(expectedOutput);
        CrawlPublicResult output = controller.getCrawlResult(request, null);
        verify(crawlService).getCrawl("crawl123");
        assertEquals(expectedOutput, output);
    }


    @Test
    void testGetCrawlResult_ValidAndNotFoundResult() {
        when(request.params("id")).thenReturn("crawl123");
        when(crawlService.getCrawl("crawl123")).thenThrow(NotFoundException.class);
        assertThrows(
                NotFoundException.class,
                () -> controller.getCrawlResult(request, null)
        );
    }

    @Test
    void testCreateCrawl_InvalidJsonThrows() {
        when(request.body()).thenReturn("invalid-json");
        assertThrows(Exception.class, () -> controller.createCrawl(request, response));
    }
}
