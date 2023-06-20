
package org.epos.converter.plugin.pluginsmetadata.content.jackson.out;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "mapping",
    "execution"
})
@Generated("jsonschema2pojo")
public class Conversion {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("mapping")
    private Mapping mapping;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("execution")
    private Execution execution;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("mapping")
    public Mapping getMapping() {
        return mapping;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("mapping")
    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    public Conversion withMapping(Mapping mapping) {
        this.mapping = mapping;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("execution")
    public Execution getExecution() {
        return execution;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("execution")
    public void setExecution(Execution execution) {
        this.execution = execution;
    }

    public Conversion withExecution(Execution execution) {
        this.execution = execution;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Conversion.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("mapping");
        sb.append('=');
        sb.append(((this.mapping == null)?"<null>":this.mapping));
        sb.append(',');
        sb.append("execution");
        sb.append('=');
        sb.append(((this.execution == null)?"<null>":this.execution));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.execution == null)? 0 :this.execution.hashCode()));
        result = ((result* 31)+((this.mapping == null)? 0 :this.mapping.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Conversion) == false) {
            return false;
        }
        Conversion rhs = ((Conversion) other);
        return (((this.execution == rhs.execution)||((this.execution!= null)&&this.execution.equals(rhs.execution)))&&((this.mapping == rhs.mapping)||((this.mapping!= null)&&this.mapping.equals(rhs.mapping))));
    }

}
