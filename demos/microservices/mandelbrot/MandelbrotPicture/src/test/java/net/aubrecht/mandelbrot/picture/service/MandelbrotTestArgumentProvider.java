package net.aubrecht.mandelbrot.picture.service;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * Provider of test data.
 *
 * @author Petr Aubrecht
 */
public class MandelbrotTestArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext ec) throws Exception {
        return Stream.of(
                Arguments.of(new MandelbrotPointArgument(0d, 0, 0, 0, 0)),
                Arguments.of(new MandelbrotPointArgument(500d, 0, 0, 500, 4))
        );
    }

}
