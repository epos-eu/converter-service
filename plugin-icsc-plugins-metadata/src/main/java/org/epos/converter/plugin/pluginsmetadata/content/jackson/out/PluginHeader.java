
package org.epos.converter.plugin.pluginsmetadata.content.jackson.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "repoLocation",
    "repoArtifactLocations",
    "repoAccessRestriction",
    "installLocation",
    "proxyType"
})
@Generated("jsonschema2pojo")
public class PluginHeader {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    private String name;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("repoLocation")
    private String repoLocation;
    @JsonProperty("repoArtifactLocations")
    private List<String> repoArtifactLocations = new ArrayList<String>();
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("repoAccessRestriction")
    private Boolean repoAccessRestriction;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("installLocation")
    private String installLocation;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("proxyType")
    private PluginHeader.ProxyType proxyType;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public PluginHeader withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("repoLocation")
    public String getRepoLocation() {
        return repoLocation;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("repoLocation")
    public void setRepoLocation(String repoLocation) {
        this.repoLocation = repoLocation;
    }

    public PluginHeader withRepoLocation(String repoLocation) {
        this.repoLocation = repoLocation;
        return this;
    }

    @JsonProperty("repoArtifactLocations")
    public List<String> getRepoArtifactLocations() {
        return repoArtifactLocations;
    }

    @JsonProperty("repoArtifactLocations")
    public void setRepoArtifactLocations(List<String> repoArtifactLocations) {
        this.repoArtifactLocations = repoArtifactLocations;
    }

    public PluginHeader withRepoArtifactLocations(List<String> repoArtifactLocations) {
        this.repoArtifactLocations = repoArtifactLocations;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("repoAccessRestriction")
    public Boolean getRepoAccessRestriction() {
        return repoAccessRestriction;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("repoAccessRestriction")
    public void setRepoAccessRestriction(Boolean repoAccessRestriction) {
        this.repoAccessRestriction = repoAccessRestriction;
    }

    public PluginHeader withRepoAccessRestriction(Boolean repoAccessRestriction) {
        this.repoAccessRestriction = repoAccessRestriction;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("installLocation")
    public String getInstallLocation() {
        return installLocation;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("installLocation")
    public void setInstallLocation(String installLocation) {
        this.installLocation = installLocation;
    }

    public PluginHeader withInstallLocation(String installLocation) {
        this.installLocation = installLocation;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("proxyType")
    public PluginHeader.ProxyType getProxyType() {
        return proxyType;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("proxyType")
    public void setProxyType(PluginHeader.ProxyType proxyType) {
        this.proxyType = proxyType;
    }

    public PluginHeader withProxyType(PluginHeader.ProxyType proxyType) {
        this.proxyType = proxyType;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(PluginHeader.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("repoLocation");
        sb.append('=');
        sb.append(((this.repoLocation == null)?"<null>":this.repoLocation));
        sb.append(',');
        sb.append("repoArtifactLocations");
        sb.append('=');
        sb.append(((this.repoArtifactLocations == null)?"<null>":this.repoArtifactLocations));
        sb.append(',');
        sb.append("repoAccessRestriction");
        sb.append('=');
        sb.append(((this.repoAccessRestriction == null)?"<null>":this.repoAccessRestriction));
        sb.append(',');
        sb.append("installLocation");
        sb.append('=');
        sb.append(((this.installLocation == null)?"<null>":this.installLocation));
        sb.append(',');
        sb.append("proxyType");
        sb.append('=');
        sb.append(((this.proxyType == null)?"<null>":this.proxyType));
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
        result = ((result* 31)+((this.installLocation == null)? 0 :this.installLocation.hashCode()));
        result = ((result* 31)+((this.repoArtifactLocations == null)? 0 :this.repoArtifactLocations.hashCode()));
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.repoAccessRestriction == null)? 0 :this.repoAccessRestriction.hashCode()));
        result = ((result* 31)+((this.proxyType == null)? 0 :this.proxyType.hashCode()));
        result = ((result* 31)+((this.repoLocation == null)? 0 :this.repoLocation.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PluginHeader) == false) {
            return false;
        }
        PluginHeader rhs = ((PluginHeader) other);
        return (((((((this.installLocation == rhs.installLocation)||((this.installLocation!= null)&&this.installLocation.equals(rhs.installLocation)))&&((this.repoArtifactLocations == rhs.repoArtifactLocations)||((this.repoArtifactLocations!= null)&&this.repoArtifactLocations.equals(rhs.repoArtifactLocations))))&&((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name))))&&((this.repoAccessRestriction == rhs.repoAccessRestriction)||((this.repoAccessRestriction!= null)&&this.repoAccessRestriction.equals(rhs.repoAccessRestriction))))&&((this.proxyType == rhs.proxyType)||((this.proxyType!= null)&&this.proxyType.equals(rhs.proxyType))))&&((this.repoLocation == rhs.repoLocation)||((this.repoLocation!= null)&&this.repoLocation.equals(rhs.repoLocation))));
    }

    @Generated("jsonschema2pojo")
    public enum ProxyType {

        JAVA_REFLECTION("Java-Reflection");
        private final String value;
        private final static Map<String, PluginHeader.ProxyType> CONSTANTS = new HashMap<String, PluginHeader.ProxyType>();

        static {
            for (PluginHeader.ProxyType c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        ProxyType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static PluginHeader.ProxyType fromValue(String value) {
            PluginHeader.ProxyType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
