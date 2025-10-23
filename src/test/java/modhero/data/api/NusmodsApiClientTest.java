// NusmodsApiClientTest.java
package modhero.data.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NusmodsApiClientTest {
    private final NusmodsApiClient client = new NusmodsApiClient();

    @Test
    void testFetchModuleDataSafelyValidModule() {
        String moduleCode = "CS2113";
        String acadYear = "2024-2025";
        String result = client.fetchModuleDataSafely(acadYear, moduleCode);
        assertTrue(result.contains(moduleCode));
        assertTrue(result.contains(acadYear.replace("-","/")));
    }

    @Test
    void fetchModuleDataSafelyReturnsNullOnInvalidAddress() {
        assertNull(client.fetchModuleDataSafely("invalid", "XXXX"));
    }
}
