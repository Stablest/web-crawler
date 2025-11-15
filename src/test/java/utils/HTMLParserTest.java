package utils;

import com.stablest.web_crawler.utils.HTMLParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HTMLParserTest {

    @Test
    void givenValidResource_whenToStringCalled_thenReturnsContent() throws IOException {
        String expectedContent = "<html><body>Hello!</body></html>";
        String result = HTMLParser.toString("hello_world.html");
        assertEquals(expectedContent, result);
    }

    @Test
    void givenMissingResource_whenToString_thenReturnsNull() {
        String result = HTMLParser.toString("no_such_file.html");
        assertNull(result);
    }

    @Test
    void givenInvalidUri_whenToString_thenReturnsNull() {
        String result = HTMLParser.toString(":/\\bad_uri");
        assertNull(result);
    }
}