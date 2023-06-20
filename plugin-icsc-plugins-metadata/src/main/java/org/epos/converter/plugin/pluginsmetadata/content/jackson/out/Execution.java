
package org.epos.converter.plugin.pluginsmetadata.content.jackson.out;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "invocationDetail"
})
@Generated("jsonschema2pojo")
public class Execution {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("invocationDetail")
    private List<String> invocationDetail = new ArrayList<String>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("invocationDetail")
    public List<String> getInvocationDetail() {
        return invocationDetail;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("invocationDetail")
    public void setInvocationDetail(List<String> invocationDetail) {
        this.invocationDetail = invocationDetail;
    }

    public Execution withInvocationDetail(List<String> invocationDetail) {
        this.invocationDetail = invocationDetail;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Execution.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("invocationDetail");
        sb.append('=');
        sb.append(((this.invocationDetail == null)?"<null>":this.invocationDetail));
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
        result = ((result* 31)+((this.invocationDetail == null)? 0 :this.invocationDetail.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Execution) == false) {
            return false;
        }
        Execution rhs = ((Execution) other);
        return ((this.invocationDetail == rhs.invocationDetail)||((this.invocationDetail!= null)&&this.invocationDetail.equals(rhs.invocationDetail)));
    }

}
