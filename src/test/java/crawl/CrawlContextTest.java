package crawl;

import com.stablest.web_crawler.crawl.CrawlContext;
import com.stablest.web_crawler.crawl.model.Crawl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CrawlContextTest {
    private CrawlContext context;

    private Crawl createCrawl(String id) {
        return new Crawl("", id, URI.create(""));
    }

    @Test
    void givenMaxResultSizeReached_whenPutInResult_thenThrowsIllegalStateException() {
        context = new CrawlContext(0);
        String key = "abc12345";
        Crawl crawl = createCrawl(key);

        Assertions.assertThrows(IllegalStateException.class, () -> context.putInResult(key, crawl));
    }

    @Test
    void givenMaxResultSizeNotReached_whenPutInResult_thenStoresResult() {
        context = new CrawlContext(1);
        String key = "abc12345";
        Crawl expectedCrawl = createCrawl(key);

        context.putInResult(key, expectedCrawl);
        Optional<Crawl> response = context.getResult(key);

        Assertions.assertTrue(response.isPresent());
        Assertions.assertEquals(expectedCrawl, response.get());
    }

    @Test
    void givenKeyAlreadyExists_whenPutInResult_thenThrowsIllegalStateException() {
        context = new CrawlContext(1);
        context.putInResult("k1", createCrawl("k1"));

        Assertions.assertThrows(IllegalStateException.class, () -> context.putInResult("k1", createCrawl("k1-new")));
    }
}
