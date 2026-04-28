package in.scalive.Velora.exception;
//when email or sku will repeat 
public class DuplicateResourceException extends RuntimeException{
	private final String resourceName; 
	private final String fieldName;
	private final Object fieldValue;
	public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
		super(String.format("%s not found with %s: %s",resourceName, fieldName,fieldValue));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}
	public DuplicateResourceException(String message) {
		super(message);
		this.resourceName = null;
		this.fieldName = null;
		this.fieldValue = null;
	}

}
