package fish.payara.sampledataminer.resources;

/**
 * Data to process.
 *
 * @author Petr Aubrecht <aubrecht@asoftware.cz>
 */
public record DataRow(
        int sourceId,
        double time,
        int position,
        int temperature,
        int label) {
}
