package utils;

import com.stablest.web_crawler.utils.HTMLParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HTMLParserTest {

    @Test
    void testToString_ValidResource() throws IOException {
        Path tempFile = Path.of("test_file.html");
        String expectedContent = "<html><body>Hello!</body></html>";
        Files.writeString(tempFile, expectedContent);
        String result = HTMLParser.toString("test_file.html");
        assertNotNull(result);
        assertTrue(result.contains("Hello!"));
    }

    @Test
    void testToString_NonexistentResource() {
        String result = HTMLParser.toString("no_such_file.html");
        assertNull(result);
    }

    @Test
    void testToString_ThrowsExceptionReturnsNull() {
        String result = HTMLParser.toString(":/\\bad_uri");
        assertNull(result);
    }
}