
import com.emreekiziltoprak.blockworld_server.application.GameLoop;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class GameLoopTest {

    @Test
    void tickExecutesQueuedCommandsInOrder() {
        GameLoop loop = new GameLoop();
        List<String> order = new ArrayList<>();

        loop.submit(() -> order.add("ilk"));
        loop.submit(() -> order.add("ikinci"));
        assertThat(order).isEmpty();

        loop.tick();

        assertThat(order).containsExactly("ilk", "ikinci");
        assertThat(loop.tickCount()).isEqualTo(1);
    }
}