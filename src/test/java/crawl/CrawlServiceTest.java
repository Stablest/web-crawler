package crawl;

import com.stablest.web_crawler.common.ApplicationContext;
import com.stablest.web_crawler.common.exception.NotFoundException;
import com.stablest.web_crawler.common.utils.AlphanumericGenerator;
import com.stablest.web_crawler.crawl.CrawlContext;
import com.stablest.web_crawler.crawl.CrawlMapper;
import com.stablest.web_crawler.crawl.CrawlService;
import com.stablest.web_crawler.crawl.dto.CrawlPublicResult;
import com.stablest.web_crawler.crawl.dto.output.CreateCrawlOutput;
import com.stablest.web_crawler.crawl.model.Crawl;
import com.stablest.web_crawler.crawl.model.CrawlNode;
import com.stablest.web_crawler.crawl.queue.CrawlerQueueManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CrawlServiceTest {
    @Mock
    private CrawlContext crawlContext;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private CrawlerQueueManager crawlerQueueManager;
    @Mock
    private AlphanumericGenerator alphanumericGenerator;
    @InjectMocks
    private CrawlService crawlService;

    @Test
    void givenIdNotInContext_whenGetCrawl_thenThrowsNotFoundException() {
        String id = "1";
        when(crawlContext.getResult(eq(id))).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> crawlService.getCrawl(id));
    }

    @Test
    void givenIdFoundInContext_whenGetCrawl_thenReturnPublicCrawl() {
        String id = "1";
        Crawl crawl = new Crawl("keyword", id, URI.create(""));
        when(crawlContext.getResult(eq(id))).thenReturn(Optional.of(crawl));

        CrawlPublicResult expectedResponse = CrawlMapper.toPublic(crawl);
        CrawlPublicResult response = crawlService.getCrawl(id);

        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    void givenNotEmptyKeyword_whenCreateCrawl_thenPutInContextAndPutInQueueAndReturnOutput() {
        String keyword = "random_keyword";
        String id = "abc12345";
        String baseURL = "url1";

        when(applicationContext.getBaseURL()).thenReturn(baseURL);
        when(alphanumericGenerator.generate()).thenReturn(id);

        Crawl crawl = new Crawl(keyword, id, URI.create(baseURL));
        CrawlNode crawlNode = new CrawlNode(crawl, baseURL);
        CreateCrawlOutput expectedResponse = new CreateCrawlOutput(id);
        CreateCrawlOutput response = crawlService.createCrawl(keyword);

        verify(crawlContext).putInResult(eq(id), eq(crawl));
        verify(crawlerQueueManager).process(eq(crawlNode));
        verifyNoMoreInteractions(crawlContext, crawlerQueueManager);
        Assertions.assertEquals(expectedResponse, response);
    }

    @ParameterizedTest
    @NullAndEmptySource()
    void givenNullKeyword_whenCreateCrawl_thenThrowsIllegalArgumentExceptionAndNotInteractWithContextAndQueueManager(String keyword) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> crawlService.createCrawl(keyword));
        verifyNoInteractions(crawlContext, crawlerQueueManager);
    }
}
