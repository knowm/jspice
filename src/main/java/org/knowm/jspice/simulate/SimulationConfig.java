package org.knowm.jspice.simulate;

import org.knowm.jspice.simulate.dcoperatingpoint.SimulationConfigDCOP;
import org.knowm.jspice.simulate.dcsweep.SimulationConfigDCSweep;
import org.knowm.jspice.simulate.transientanalysis.SimulationConfigTransient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@Type(value = SimulationConfigDCOP.class, name = "dcop"), @Type(value = SimulationConfigDCSweep.class, name = "sweep"),
    @Type(value = SimulationConfigTransient.class, name = "trans")})
public abstract class SimulationConfig {

}