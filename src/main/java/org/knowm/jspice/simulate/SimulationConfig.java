package org.knowm.jspice.simulate;

import org.knowm.jspice.simulate.dcoperatingpoint.DCOPConfig;
import org.knowm.jspice.simulate.dcsweep.DCSweepConfig;
import org.knowm.jspice.simulate.transientanalysis.TransientConfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@Type(value = DCOPConfig.class, name = "dcop"), @Type(value = DCSweepConfig.class, name = "sweep"),
    @Type(value = TransientConfig.class, name = "trans")})
public abstract class SimulationConfig {

}