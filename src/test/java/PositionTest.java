import com.emreekiziltoprak.blockworld_server.domain.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.internal.matchers.Null;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.util.stream.Stream;

public class PositionTest {
    private final UUID validWorldId = UUID.randomUUID();

    @Test
    @DisplayName("Coordinates should be created with valid arguments")
    void shouldCreatePositionWithValidCoordinates(){
        assertDoesNotThrow(() -> new Position(validWorldId, 10.0, 64.0, -10.0, 90.0f, 45.0f));
    }

    @Test
    @DisplayName("Any Y coordinate can be accepted without check")
    void shouldSetAnyYValue(){
        assertDoesNotThrow(()->
                new Position(validWorldId, 10.0,5000.0,-10.0, 90.0f, 45.0f));
    }

    @Test
    @DisplayName("Should reject invalid pitch values")
    void shouldRejectInvalidPitchValued(){
        assertThrows(IllegalArgumentException.class,()->
                new Position(validWorldId, 10.0,5000.0,-10.0, 90.0f, 91.0f));
        assertThrows(IllegalArgumentException.class,()->
                new Position(validWorldId, 10.0,5000.0,-10.0, 90.0f, -91.0f));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidNumbers")
    @DisplayName("NaN or Infinity containing coordinates should throw IllegalArgumentException")
    void shouldThrowExceptionWhenCoordinatesAreNaNOrInfinity(
            double x, double y, double z, float yaw, float pitch) {

        assertThrows(IllegalArgumentException.class, () -> {
            new Position(validWorldId, x, y, z, yaw, pitch);
        });
    }

    private static Stream<Arguments> provideInvalidNumbers() {
        return Stream.of(
                Arguments.of(Double.NaN, 64.0, 10.0, 0.0f, 0.0f),
                Arguments.of(Double.POSITIVE_INFINITY, 64.0, 10.0, 0.0f, 0.0f),
                Arguments.of(Double.NEGATIVE_INFINITY, 64.0, 10.0, 0.0f, 0.0f),
                Arguments.of(10.0, Double.NaN, 10.0, 0.0f, 0.0f),
                Arguments.of(10.0, Double.POSITIVE_INFINITY, 10.0, 0.0f, 0.0f),
                Arguments.of(10.0, 64.0, Double.NaN, 0.0f, 0.0f),
                Arguments.of(10.0, 64.0, Double.NEGATIVE_INFINITY, 0.0f, 0.0f),
                Arguments.of(10.0, 64.0, 10.0, Float.NaN, 0.0f),
                Arguments.of(10.0, 64.0, 10.0, Float.POSITIVE_INFINITY, 0.0f),
                Arguments.of(10.0, 64.0, 10.0, 0.0f, Float.NaN),
                Arguments.of(10.0, 64.0, 10.0, 0.0f, Float.NEGATIVE_INFINITY)
        );
    }


}
