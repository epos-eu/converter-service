package org.epos.converter.common.type;

import java.util.Objects;
import java.util.Optional;

import org.epos.converter.common.type.ContentFormat;

/**
 * Describes the format of a payload, or a particular portion of a payload.
 * There are two flavours of this immutable class that can be instantiated:
 * <p><ul>
 * <li>Root Descriptor
 * <li>non-Root Descriptor
 * </ul><p>
 */
public final class PayloadFormatDescriptor {
	
	private ContentFormat targetFormat;
	private Optional<ContentFormat> pointerFormat;
	private Optional<String> pointerExpr;	// XPath or JSONPath depending on specified pointerFormat

	/**
	 * For instantiation of <b>Root Descriptor</b>
	 */
	public PayloadFormatDescriptor(ContentFormat targetFormat) {
		super();		
		this.pointerFormat = Optional.empty();
		this.pointerExpr = Optional.empty();
		
		try {
			this.targetFormat = Objects.requireNonNull(targetFormat);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * For instantiation of <b>non-Root Descriptor</b>
	 */
	public PayloadFormatDescriptor(ContentFormat targetFormat, ContentFormat pointerFormat, String pointerExpr) throws IllegalArgumentException {		
		super();
		
		try {
			this.targetFormat = Objects.requireNonNull(targetFormat);
			this.pointerFormat = Optional.of(pointerFormat);
			this.pointerExpr = Optional.of(pointerExpr);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}	
	}
	
	public boolean isRootDescriptor() {
		return !pointerFormat.isPresent() && !pointerExpr.isPresent();
	}

	public ContentFormat getTargetFormat() {
		return targetFormat;
	}
	
	public Optional<ContentFormat> getPointerFormat() {
		return pointerFormat;
	}

	public Optional<String> getPointerExpr() {
		return pointerExpr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pointerExpr == null) ? 0 : pointerExpr.hashCode());
		result = prime * result + ((pointerFormat == null) ? 0 : pointerFormat.hashCode());
		result = prime * result + ((targetFormat == null) ? 0 : targetFormat.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PayloadFormatDescriptor other = (PayloadFormatDescriptor) obj;
		if (pointerExpr == null) {
			if (other.pointerExpr != null)
				return false;
		} else if (!pointerExpr.equals(other.pointerExpr))
			return false;
		if (pointerFormat == null) {
			if (other.pointerFormat != null)
				return false;
		} else if (!pointerFormat.equals(other.pointerFormat))
			return false;
		if (targetFormat != other.targetFormat)
			return false;
		return true;
	}


}
