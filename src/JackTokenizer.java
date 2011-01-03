import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class JackTokenizer {

    private ArrayList<String> strings =  new  ArrayList<String>();
    private Scanner source;
    private int counter = 0;
    private Scanner tokenizer;
    private String token = "";
    private static final String[] SYMBOLS_LOOKUP = {"\\{", "\\}", "\\(", "\\)","\\[","\\]","\\.","\\,",";","\\+","\\-","\\*","/","\\&","\\|","\\<","\\>","\\=","~"};
    private static final String[] SYMBOLS = {"{","}","(",")","[","]",".",",",";","+","-","*","/", "&amp;","|","&lt;","&gt;","=","~"};
    private Keyword key;

    public JackTokenizer(File source) throws FileNotFoundException {
        this.source = new Scanner(new MyReader(source,strings));
        this.source.useDelimiter("(\\s)|(//(.*))");
    }

    /**
     *   Advances to the next line.
     */
    public boolean advance() {
        while(tokenizer != null) {
            token = tokenizer.next();
            if(!tokenizer.hasNext()){
                tokenizer = null;
            }
            if(!token.isEmpty()){
                return true;
            }
        }

        while(source.hasNext()) {
            token = source.next();

            if(!token.isEmpty()){
                for(int i = 0; i < SYMBOLS_LOOKUP.length; i++){
                    token = token.replaceAll(SYMBOLS_LOOKUP[i], " "+SYMBOLS[i]+" ");
                }

                tokenizer = new Scanner(token);
                token =  tokenizer.next();
                if(!tokenizer.hasNext()){
                    tokenizer = null;
                }
                return true;
            }
        }
        return false;
    }

    public TokenType tokenType() {
        if(Character.isDigit(token.charAt(0))){
            return TokenType.INT_CONST;
        }

        if(token.startsWith("\"")){
            token = strings.get(counter).replaceAll("\\&","&amp;").replaceAll("\\<","&lt;").replaceAll("\\>","&gt;");
            counter++;
            return TokenType.STRING_CONST;
        }

        for(String symbol : SYMBOLS){
            if(symbol.equals(token)){
                return TokenType.SYMBOL;
            }
        }

        for(Keyword keyword : Keyword.values()){
            if(keyword.tag.equals(token)){
                key = keyword;
                return TokenType.KEYWORD;
            }
        }

        return TokenType.IDENTIFIER;
    }

    public String token() {
        return token;
    }

    public Keyword keyword() {
        return key;
    }
    
    /**
     *    closes the file.
     */
    public void close() {
        source.close();
    }
}
