
package org.epos.converter.plugin.pluginsmetadata.content.jackson.in;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "identifier",
    "name",
    "downloadURL",
    "softwareVersion",
    "proxy-type",
    "requirements",
    "action",
    "operations",
    "location"
})
@Generated("jsonschema2pojo")
public class PluginsMetadataIcscSchema {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("identifier")
    private String identifier;
    @JsonProperty("name")
    private String name;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("downloadURL")
    private String downloadURL;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("softwareVersion")
    private String softwareVersion;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("proxy-type")
    private String proxyType;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requirements")
    private String requirements;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("action")
    private Action action;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("operations")
    private List<String> operations = new ArrayList<String>();
    @JsonProperty("location")
    private String location;
    @JsonIgnore
    private Map<String, java.lang.Object> additionalProperties = new HashMap<String, java.lang.Object>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("identifier")
    public String getIdentifier() {
        return identifier;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("identifier")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public PluginsMetadataIcscSchema withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public PluginsMetadataIcscSchema withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("downloadURL")
    public String getDownloadURL() {
        return downloadURL;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("downloadURL")
    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public PluginsMetadataIcscSchema withDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("softwareVersion")
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("softwareVersion")
    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public PluginsMetadataIcscSchema withSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("proxy-type")
    public String getProxyType() {
        return proxyType;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("proxy-type")
    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }

    public PluginsMetadataIcscSchema withProxyType(String proxyType) {
        this.proxyType = proxyType;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requirements")
    public String getRequirements() {
        return requirements;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requirements")
    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public PluginsMetadataIcscSchema withRequirements(String requirements) {
        this.requirements = requirements;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("action")
    public Action getAction() {
        return action;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("action")
    public void setAction(Action action) {
        this.action = action;
    }

    public PluginsMetadataIcscSchema withAction(Action action) {
        this.action = action;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("operations")
    public List<String> getOperations() {
        return operations;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("operations")
    public void setOperations(List<String> operations) {
        this.operations = operations;
    }

    public PluginsMetadataIcscSchema withOperations(List<String> operations) {
        this.operations = operations;
        return this;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(String location) {
        this.location = location;
    }

    public PluginsMetadataIcscSchema withLocation(String location) {
        this.location = location;
        return this;
    }

    @JsonAnyGetter
    public Map<String, java.lang.Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, java.lang.Object value) {
        this.additionalProperties.put(name, value);
    }

    public PluginsMetadataIcscSchema withAdditionalProperty(String name, java.lang.Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(PluginsMetadataIcscSchema.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("identifier");
        sb.append('=');
        sb.append(((this.identifier == null)?"<null>":this.identifier));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("downloadURL");
        sb.append('=');
        sb.append(((this.downloadURL == null)?"<null>":this.downloadURL));
        sb.append(',');
        sb.append("softwareVersion");
        sb.append('=');
        sb.append(((this.softwareVersion == null)?"<null>":this.softwareVersion));
        sb.append(',');
        sb.append("proxyType");
        sb.append('=');
        sb.append(((this.proxyType == null)?"<null>":this.proxyType));
        sb.append(',');
        sb.append("requirements");
        sb.append('=');
        sb.append(((this.requirements == null)?"<null>":this.requirements));
        sb.append(',');
        sb.append("action");
        sb.append('=');
        sb.append(((this.action == null)?"<null>":this.action));
        sb.append(',');
        sb.append("operations");
        sb.append('=');
        sb.append(((this.operations == null)?"<null>":this.operations));
        sb.append(',');
        sb.append("location");
        sb.append('=');
        sb.append(((this.location == null)?"<null>":this.location));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
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
        result = ((result* 31)+((this.identifier == null)? 0 :this.identifier.hashCode()));
        result = ((result* 31)+((this.requirements == null)? 0 :this.requirements.hashCode()));
        result = ((result* 31)+((this.operations == null)? 0 :this.operations.hashCode()));
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.downloadURL == null)? 0 :this.downloadURL.hashCode()));
        result = ((result* 31)+((this.proxyType == null)? 0 :this.proxyType.hashCode()));
        result = ((result* 31)+((this.action == null)? 0 :this.action.hashCode()));
        result = ((result* 31)+((this.location == null)? 0 :this.location.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.softwareVersion == null)? 0 :this.softwareVersion.hashCode()));
        return result;
    }

    @Override
    public boolean equals(java.lang.Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PluginsMetadataIcscSchema) == false) {
            return false;
        }
        PluginsMetadataIcscSchema rhs = ((PluginsMetadataIcscSchema) other);
        return (((((((((((this.identifier == rhs.identifier)||((this.identifier!= null)&&this.identifier.equals(rhs.identifier)))&&((this.requirements == rhs.requirements)||((this.requirements!= null)&&this.requirements.equals(rhs.requirements))))&&((this.operations == rhs.operations)||((this.operations!= null)&&this.operations.equals(rhs.operations))))&&((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name))))&&((this.downloadURL == rhs.downloadURL)||((this.downloadURL!= null)&&this.downloadURL.equals(rhs.downloadURL))))&&((this.proxyType == rhs.proxyType)||((this.proxyType!= null)&&this.proxyType.equals(rhs.proxyType))))&&((this.action == rhs.action)||((this.action!= null)&&this.action.equals(rhs.action))))&&((this.location == rhs.location)||((this.location!= null)&&this.location.equals(rhs.location))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.softwareVersion == rhs.softwareVersion)||((this.softwareVersion!= null)&&this.softwareVersion.equals(rhs.softwareVersion))));
    }

}
