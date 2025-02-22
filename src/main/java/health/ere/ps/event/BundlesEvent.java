package health.ere.ps.event;

import org.hl7.fhir.r4.model.Bundle;

import java.util.Arrays;
import java.util.List;

public final class BundlesEvent {

    private final List<Bundle> bundles;

    public BundlesEvent(List<Bundle> bundles) {
        this.bundles = bundles;
    }

    public List<Bundle> getBundles() {
        return this.bundles;
    }
}
