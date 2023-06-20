
package org.epos.converter.plugin.pluginsmetadata.content.jackson.out;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "requestType",
    "requestContentType",
    "responseContentType"
})
@Generated("jsonschema2pojo")
public class Mapping {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestType")
    private String requestType;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestContentType")
    private String requestContentType;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("responseContentType")
    private String responseContentType;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestType")
    public String getRequestType() {
        return requestType;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestType")
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Mapping withRequestType(String requestType) {
        this.requestType = requestType;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestContentType")
    public String getRequestContentType() {
        return requestContentType;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestContentType")
    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public Mapping withRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("responseContentType")
    public String getResponseContentType() {
        return responseContentType;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("responseContentType")
    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public Mapping withResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Mapping.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("requestType");
        sb.append('=');
        sb.append(((this.requestType == null)?"<null>":this.requestType));
        sb.append(',');
        sb.append("requestContentType");
        sb.append('=');
        sb.append(((this.requestContentType == null)?"<null>":this.requestContentType));
        sb.append(',');
        sb.append("responseContentType");
        sb.append('=');
        sb.append(((this.responseContentType == null)?"<null>":this.responseContentType));
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
        result = ((result* 31)+((this.requestType == null)? 0 :this.requestType.hashCode()));
        result = ((result* 31)+((this.requestContentType == null)? 0 :this.requestContentType.hashCode()));
        result = ((result* 31)+((this.responseContentType == null)? 0 :this.responseContentType.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Mapping) == false) {
            return false;
        }
        Mapping rhs = ((Mapping) other);
        return ((((this.requestType == rhs.requestType)||((this.requestType!= null)&&this.requestType.equals(rhs.requestType)))&&((this.requestContentType == rhs.requestContentType)||((this.requestContentType!= null)&&this.requestContentType.equals(rhs.requestContentType))))&&((this.responseContentType == rhs.responseContentType)||((this.responseContentType!= null)&&this.responseContentType.equals(rhs.responseContentType))));
    }

}
