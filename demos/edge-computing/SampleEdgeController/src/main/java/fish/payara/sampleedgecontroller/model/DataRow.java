package fish.payara.sampleedgecontroller.model;

/**
 * Data to process.
 *
 * @author Petr Aubrecht <aubrecht@asoftware.cz>
 */
public class DataRow {
    private int sourceId;
    private double time;
    private int position;
    private int temperature;
    private int label;

    public DataRow() {
    }

    public DataRow(int sourceId, double time, int position, int temperature, int label) {
        this.sourceId = sourceId;
        this.time = time;
        this.position = position;
        this.temperature = temperature;
        this.label = label;
    }

    @Override
    public String toString() {
        return "DataRow{" + "time=" + time + ", position=" + position + ", temperature=" + temperature + ", label=" + label + '}';
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * @return the time
     */
    public double getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @return the temperature
     */
    public int getTemperature() {
        return temperature;
    }

    /**
     * @param temperature the temperature to set
     */
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    /**
     * @return the label
     */
    public int getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(int label) {
        this.label = label;
    }

}
