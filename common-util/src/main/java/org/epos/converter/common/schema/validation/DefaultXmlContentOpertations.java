package org.epos.converter.common.schema.validation;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.text.StringEscapeUtils;
import org.epos.converter.common.schema.validation.exception.ConfigurationException;
import org.epos.converter.common.schema.validation.exception.ContentExtractionException;
import org.epos.converter.common.schema.validation.exception.ContentValidationException;
import org.epos.converter.common.type.ContentFormat;
import org.epos.converter.common.type.ContentOperationSet;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Current default implementation for XML. Uses...
 *  <b>XPath (restrictive support)</b> for extracting substrings;
 *  <b>????</b> for validation
 */
public class DefaultXmlContentOpertations extends ContentOperationSet<javax.xml.validation.Schema> {
	
	private final Pattern ENCLOSING_DOUBLE_QUOTES_PATTERN = Pattern.compile("^\"|\"$");
	private final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

	@Override
	public String getSubstrFromContent(String pointerExpr, String content) throws ContentExtractionException {
/*		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {*/
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			Document doc = builder.parse(new ByteArrayInputStream(payload.getBytes()));
//			XPathFactory xPathfactory = XPathFactory.newInstance();
		try {
			InputSource xmlContent = new InputSource(new StringReader(content));			
			XPath xpath = XPATH_FACTORY.newXPath();
			NodeList nodes = (NodeList) xpath.evaluate(pointerExpr, xmlContent, XPathConstants.NODESET);
			
			int noOfNodes = nodes.getLength();
			if (noOfNodes > 0) {
				if (noOfNodes == 1) {
				    StringWriter buf = new StringWriter();
				    Transformer xform = TransformerFactory.newInstance().newTransformer();
				    xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				    xform.transform(new DOMSource(nodes.item(0)), new StreamResult(buf));
				    return(buf.toString());
				}
				String fmtMsg = "Pointer expression, '%s', matched more than one instance within the XML content passed in...%n%s";
				throw new ContentExtractionException(String.format(fmtMsg, pointerExpr, content));
			}
			return "";
			
		} catch (XPathExpressionException e) {
			String msg = String.format("Couldn't resolve declared pointer expression ('%s')", pointerExpr);
			throw new ContentExtractionException(msg);
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			throw new ContentExtractionException(e);			
		}
	}
	
	private static String nodeToString(Node node) throws Exception{
/*		StringWriter sw = new StringWriter();
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		t.transform(new DOMSource(node), new StreamResult(sw));
		return sw.toString();*/
		
	    StringWriter buf = new StringWriter();
	    Transformer xform = TransformerFactory.newInstance().newTransformer();
	    xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    xform.transform(new DOMSource(node), new StreamResult(buf));
	    return(buf.toString());
	}

	@Override
	public Optional<Object> loadSchema(URL schemaPathUrl) throws ConfigurationException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			return Optional.ofNullable(schemaFactory.newSchema(schemaPathUrl));
		} catch (SAXException | IllegalArgumentException e) {
			String errMsg = String.format(
					"PLUG-IN DEPLOYMENT ISSUE: Failed to load the schema, '%s', specified. Could not parse schema [%s: %s]",
					schemaPathUrl.getPath(), e.getClass(), e.getMessage());
			throw new ConfigurationException(errMsg);
		}
	}

	@Override
	protected void doValidateContent(Schema typedSchema, String contentSubstr) throws ContentValidationException {
		Validator validator = typedSchema.newValidator();
		
		contentSubstr = getAsCleanContent(contentSubstr);			
		Source xml = new StreamSource(new StringReader(contentSubstr));
		
		try {
			validator.validate(xml);
		} catch (SAXException e) {
			String warnMsg = "Failed to parse payload ready for schema validation";
			throw new ContentValidationException(warnMsg, e);
		} catch (IOException e) {
			String warnMsg = "Problem reading the payload for schema validation";
			throw new ContentValidationException(warnMsg, e);
		}
	}

	@Override
	protected Class<Schema> getSchemaClass() {
		return javax.xml.validation.Schema.class;
	}

	@Override
	public ContentFormat getSupportedContentFormat() {
		return ContentFormat.XML;
	}

	@Override
	public String getAsCleanContent(String rawPayload) {
		
		// Strip out enclosing double-quotes if present
		String payload = ENCLOSING_DOUBLE_QUOTES_PATTERN.matcher(rawPayload).replaceAll("");
		
		// Expect to receive XML with escape characters, so unescape here 
		return StringEscapeUtils.unescapeJava(payload);
	}

	@Override
	public String getRootPointerExpr() {
		return "/";
	}

}
