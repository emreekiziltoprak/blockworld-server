import com.emreekiziltoprak.blockworld_server.domain.World;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class WorldTest {

    @Test
    @DisplayName("When world id is not assigned should throw NullPointerException")
    void shouldThrowExceptionWheWorldIdIsNull(){
        assertThrows(NullPointerException.class, () -> {
            new World(100.0,400.0,null);
        });
    }
}
