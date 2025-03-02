package fish.payara.sampleedgecontroller.jsf;

import fish.payara.sampleedgecontroller.service.DataProcessor;
import jakarta.ejb.EJB;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;

/**
 * Backing bean for faces, application state.
 *
 * @author Petr Aubrecht
 */
@Named(value = "appStateBean")
@RequestScoped
public class AppStateBean {

    @EJB
    private DataProcessor dataProcessor;

    public int getCacheSize() {
        return dataProcessor.getCacheSize();
    }

    public long getDataReceived() {
        return dataProcessor.getCounterData();
    }

    public long getDataMiningStarted() {
        return dataProcessor.getCounterDataMiningStarted();
    }

    public long getDataMiningFinished() {
        return dataProcessor.getCounterDataMiningFinished();
    }


}
