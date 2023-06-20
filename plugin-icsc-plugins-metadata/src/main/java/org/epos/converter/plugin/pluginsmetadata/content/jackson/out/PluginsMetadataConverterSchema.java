
package org.epos.converter.plugin.pluginsmetadata.content.jackson.out;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "pluginKey",
    "pluginHeader",
    "conversions"
})
@Generated("jsonschema2pojo")
public class PluginsMetadataConverterSchema {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("pluginKey")
    private PluginKey pluginKey;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("pluginHeader")
    private PluginHeader pluginHeader;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("conversions")
    private List<Conversion> conversions = new ArrayList<Conversion>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("pluginKey")
    public PluginKey getPluginKey() {
        return pluginKey;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("pluginKey")
    public void setPluginKey(PluginKey pluginKey) {
        this.pluginKey = pluginKey;
    }

    public PluginsMetadataConverterSchema withPluginKey(PluginKey pluginKey) {
        this.pluginKey = pluginKey;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("pluginHeader")
    public PluginHeader getPluginHeader() {
        return pluginHeader;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("pluginHeader")
    public void setPluginHeader(PluginHeader pluginHeader) {
        this.pluginHeader = pluginHeader;
    }

    public PluginsMetadataConverterSchema withPluginHeader(PluginHeader pluginHeader) {
        this.pluginHeader = pluginHeader;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("conversions")
    public List<Conversion> getConversions() {
        return conversions;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("conversions")
    public void setConversions(List<Conversion> conversions) {
        this.conversions = conversions;
    }

    public PluginsMetadataConverterSchema withConversions(List<Conversion> conversions) {
        this.conversions = conversions;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(PluginsMetadataConverterSchema.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("pluginKey");
        sb.append('=');
        sb.append(((this.pluginKey == null)?"<null>":this.pluginKey));
        sb.append(',');
        sb.append("pluginHeader");
        sb.append('=');
        sb.append(((this.pluginHeader == null)?"<null>":this.pluginHeader));
        sb.append(',');
        sb.append("conversions");
        sb.append('=');
        sb.append(((this.conversions == null)?"<null>":this.conversions));
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
        result = ((result* 31)+((this.pluginKey == null)? 0 :this.pluginKey.hashCode()));
        result = ((result* 31)+((this.conversions == null)? 0 :this.conversions.hashCode()));
        result = ((result* 31)+((this.pluginHeader == null)? 0 :this.pluginHeader.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PluginsMetadataConverterSchema) == false) {
            return false;
        }
        PluginsMetadataConverterSchema rhs = ((PluginsMetadataConverterSchema) other);
        return ((((this.pluginKey == rhs.pluginKey)||((this.pluginKey!= null)&&this.pluginKey.equals(rhs.pluginKey)))&&((this.conversions == rhs.conversions)||((this.conversions!= null)&&this.conversions.equals(rhs.conversions))))&&((this.pluginHeader == rhs.pluginHeader)||((this.pluginHeader!= null)&&this.pluginHeader.equals(rhs.pluginHeader))));
    }

}
