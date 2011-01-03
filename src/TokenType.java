
public enum TokenType {
	KEYWORD("keyword"),
	SYMBOL("symbol"),
	IDENTIFIER("identifier"),
	INT_CONST("integerConstant"),
	STRING_CONST("stringConstant");
	
	private String tag;
	
	TokenType(String tag) {
		this.tag = tag;
	}
	
	public String wrap(Object token) {
		return "<"+tag+"> "+token+" </"+tag+">";
	}
	
}
