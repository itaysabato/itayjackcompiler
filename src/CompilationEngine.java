import java.io.IOException;
import java.io.Writer;

public class CompilationEngine {

    private JackTokenizer tokenizer;
    private VMWriter writer;
    private SymbolTable symbolTable;

    public CompilationEngine(JackTokenizer tokenizer, Writer writer) {
        this.tokenizer = tokenizer;
        this.writer = new VMWriter(writer);
        symbolTable = new SymbolTable();
    }

    public void compileClass() throws IOException {
        writer.write("<class>\n");

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();

            if(!type.equals(TokenType.KEYWORD)){
                writer.write(type.wrap(tokenizer.token())+"\n");
            }
            else {
                Keyword keyword = tokenizer.keyword();
                if(keyword.equals(Keyword.CLASS)){
                    writer.write(type.wrap(keyword)+"\n");
                }
                else if(keyword.equals(Keyword.FIELD) || keyword.equals(Keyword.STATIC)){
                    compileClassVarDec(keyword);
                }
                else {
                    compileSubroutine(keyword);
                }
            }
        }

        writer.write("</class>\n");
    }

    private void compileSubroutine(Keyword keyword) throws IOException {
        symbolTable.startSubroutine();

        writer.write("<subroutineDec>\n");
        writer.write(TokenType.KEYWORD.wrap(keyword)+"\n");

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            if(!type.equals(TokenType.SYMBOL)){
                writer.write(type.wrap(tokenizer.token())+"\n");
            }
            else {
                String symbol = tokenizer.token();
                if(symbol.equals("(")){
                    writer.write(type.wrap(symbol)+"\n");
                    compileParameterList();
                    writer.write(type.wrap(")")+"\n");
                }
                else if(symbol.equals("{")){
                    compileSubroutineBody(symbol);
                    break;
                }
            }
        }
        writer.write("</subroutineDec>\n");
    }

    private void compileSubroutineBody(String symbol) throws IOException {
        writer.write("<subroutineBody>\n");
        writer.write(TokenType.SYMBOL.wrap(symbol)+"\n");

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            if(type.equals(TokenType.KEYWORD)){
                Keyword keyword = tokenizer.keyword();
                if(keyword.equals(Keyword.VAR)){
                    compileVarDec();
                }
                else {
                    compileStatements(keyword);
                    break;
                }
            }
            else {
                compileStatements(null);
                break;
            }
        }

        writer.write(TokenType.SYMBOL.wrap("}")+"\n");
        writer.write("</subroutineBody>\n");
    }

    private void compileParameterList() throws IOException {
        writer.write("<parameterList>\n");
        String varType = null;

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();

            if(token.equals(")") ) break;

            if(type.equals(TokenType.KEYWORD)){
                varType = token;
            }
            else if(type.equals(TokenType.IDENTIFIER)){
                if(varType == null){
                    varType = token;
                }
                else {
                    symbolTable.define(token, varType, VarKind.ARG);
                    writer.write(type.wrap(symbolTable.findVariable(token))+"\n");
                    varType = null;
                }
            }
        }

        writer.write("</parameterList>\n");
    }

    private void compileClassVarDec(Keyword keyword) throws IOException {
        compileTemplateVarDec(VarKind.valueOf(keyword.name()),"classVarDec");
    }

    private void compileVarDec() throws IOException {
        compileTemplateVarDec(VarKind.VAR,"varDec");
    }

    private void compileTemplateVarDec(VarKind kind,String tag) throws IOException {
        writer.write("<"+tag+">\n");
        String varType = null;

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();

            if(token.equals(";") ){
                break;
            }
            if(type.equals(TokenType.KEYWORD)){
                varType = token;
            }
            else if(type.equals(TokenType.IDENTIFIER)){
                if(varType == null){
                    varType = token;
                }
                else {
                    symbolTable.define(token, varType, kind);
                    writer.write(type.wrap(symbolTable.findVariable(token))+"\n");
                }
            }
        }

        writer.write("</"+tag+">\n");
    }

    private void compileStatements(Keyword keyword) throws IOException {

        writer.write("<statements>\n");

        if(keyword==null){
            writer.write("</statements>\n");
            return;
        }
        String next;
        next = compileStatement(keyword);
        if(next==null) tokenizer.advance();

        while(true){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();

            if(!type.equals(TokenType.KEYWORD) ) break;
            else{
                next = compileStatement(tokenizer.keyword());
                if(next==null)  tokenizer.advance();
                else if(next.equals("}")) break;
            }
        }

        writer.write("</statements>\n");
    }

    private String compileStatement(Keyword keyword) throws IOException {
        if(keyword.equals(Keyword.LET))   compileLet();
        if(keyword.equals(Keyword.WHILE))   compileWhile();
        if(keyword.equals(Keyword.DO))   compileDo();
        if(keyword.equals(Keyword.IF)) return compileIf();
        if(keyword.equals(Keyword.RETURN))   compileReturn();
        return null;
    }

    private void compileLet() throws IOException {
        writer.write("<letStatement>\n");
        writer.write(TokenType.KEYWORD.wrap(Keyword.LET)+"\n");

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();

            if(type.equals(TokenType.IDENTIFIER)){
                writer.write(type.wrap("let usage: "+symbolTable.findVariable(token))+"\n");
            }
            else {
                writer.write(type.wrap(token)+"\n");

                if(type.equals(TokenType.SYMBOL)){
                    String symbol = compileExpression(null, null);
                    writer.write(TokenType.SYMBOL.wrap(symbol)+"\n");
                    if(symbol.equals(";")){
                        break;
                    }
                }
            }
        }
        writer.write("</letStatement>\n");
    }

    private void compileWhile() throws IOException {
        writer.write("<whileStatement>\n");
        writer.write(TokenType.KEYWORD.wrap(Keyword.WHILE)+"\n");
        String closer;
        TokenType type;
        String token;

        while(tokenizer.advance()){
            type = tokenizer.tokenType();
            token = tokenizer.token();
            if(token.equals("(") ){
                writer.write(type.wrap(token)+"\n");
                closer = compileExpression(null,null);
                writer.write(type.wrap(closer)+"\n");
            }
            if(token.equals("{")){
                writer.write(type.wrap(token)+"\n");

                if(tokenizer.advance()){
                    type = tokenizer.tokenType();
                    if(type.equals(TokenType.KEYWORD)){
                        compileStatements(tokenizer.keyword());
                    }
                    else {
                        token = tokenizer.token();
                        compileStatements(null);
                    }
                    writer.write(TokenType.SYMBOL.wrap("}")+"\n");
                    break;
                }
            }
        }
        writer.write("</whileStatement>\n");
    }

    private void compileDo() throws IOException {
        writer.write("<doStatement>\n");
        writer.write(TokenType.KEYWORD.wrap(Keyword.DO)+"\n");

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();

            if(type.equals(TokenType.IDENTIFIER)){
                SymbolTable.Variable variable = symbolTable.findVariable(token);
                if(variable != null){
                    writer.write(TokenType.IDENTIFIER.wrap("do object method call usage: "+variable)+"\n");
                }
                else {
                    writer.write(type.wrap(token)+"\n");
                }
            }
            else {
                writer.write(type.wrap(token)+"\n");

                if(type.equals(TokenType.SYMBOL)){
                    if(token.equals("(")){
                        compileExpressionList();
                        writer.write(TokenType.SYMBOL.wrap(")")+"\n");
                    }
                    else if(token.equals(";")){
                        break;
                    }
                }
            }
        }
        writer.write("</doStatement>\n");
    }

    private void compileExpressionList() throws IOException {
        writer.write("<expressionList>\n");

        if(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();

            if(!token.equals(")") ){
                String symbol = compileExpression(type, token);
                while(symbol.equals(",")){
                    writer.write(TokenType.SYMBOL.wrap(symbol)+"\n");
                    symbol = compileExpression(null, null);
                }
            }
        }
        writer.write("</expressionList>\n");
    }

    private String compileIf() throws IOException {
        writer.write("<ifStatement>\n");
        writer.write(TokenType.KEYWORD.wrap(Keyword.IF)+"\n");
        String closer;
        TokenType type;
        String token;

        while(tokenizer.advance()){
            type = tokenizer.tokenType();
            token = tokenizer.token();
            if(token.equals("(") ){
                writer.write(type.wrap(token)+"\n");
                closer = compileExpression(null,null);
                writer.write(type.wrap(closer)+"\n");
            }
            if(token.equals("{")){
                writer.write(type.wrap(token)+"\n");

                if(tokenizer.advance()){
                    type = tokenizer.tokenType();
                    if(type.equals(TokenType.KEYWORD)){
                        compileStatements(tokenizer.keyword());
                    }
                    else {
                        token = tokenizer.token();
                        compileStatements(null);
                    }
                    writer.write(TokenType.SYMBOL.wrap("}")+"\n");
                    break;
                }
            }

        }
        if(tokenizer.advance()) {
            type = tokenizer.tokenType();
            token = tokenizer.token();
            if(token.equals(Keyword.ELSE.tag)){
                writer.write(TokenType.KEYWORD.wrap(Keyword.ELSE)+"\n");
                while(tokenizer.advance()) {
                    type = tokenizer.tokenType();
                    token = tokenizer.token();
                    if(token.equals("{")){
                        writer.write(type.wrap(token)+"\n");

                        if(tokenizer.advance()){
                            type = tokenizer.tokenType();
                            if(type.equals(TokenType.KEYWORD)){
                                compileStatements(tokenizer.keyword());
                            }
                            else {
                                token = tokenizer.token();
                                compileStatements(null);
                            }
                            writer.write(TokenType.SYMBOL.wrap("}")+"\n");
                            break;
                        }
                    }
                }
            }
            else {
                writer.write("</ifStatement>\n");
                return token;
            }
        }
        writer.write("</ifStatement>\n");
        return null;
    }

    private void compileReturn() throws IOException {
        writer.write("<returnStatement>\n");
        writer.write(TokenType.KEYWORD.wrap(Keyword.RETURN)+"\n");
        if(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();
            if(!type.equals(TokenType.SYMBOL) || !token.equals(";")){
                compileExpression(type, token);
            }
        }
        writer.write(TokenType.SYMBOL.wrap(";")+"\n");
        writer.write("</returnStatement>\n");
    }

    private String compileExpression(TokenType type, String token) throws IOException {

        writer.write("<expression>\n");
        boolean opTurn;
        String next = null;
        if(type==null && tokenizer.advance())  {
            type = tokenizer.tokenType();
            token = tokenizer.token();
        }
        opTurn = false;

        while(true) {
            if(opTurn && !isOp(token)) break;
            else if(opTurn) {
                writer.write(TokenType.SYMBOL.wrap(token)+"\n");
                opTurn = false;
            }
            else {
                token = compileTerm(type,token);
                opTurn = true;
                if(token!=null)  continue;
            }
            tokenizer.advance();
            type = tokenizer.tokenType();
            token = tokenizer.token();
        }

        writer.write("</expression>\n");
        return token;
    }

    private boolean isOp(String token) {
        String[] ops = {"+","-","*","/", "&amp;","|","&lt;","&gt;","="};
        int i = 0;
        for(;i<ops.length;i++) {
            if(token.equals(ops[i])) return true;
        }
        return false;
    }

    private String compileTerm(TokenType type, String token) throws IOException {
        writer.write("<term>\n");
        do {
            if(type.equals(TokenType.IDENTIFIER)){
                String identifier = token;
                if(tokenizer.advance()){
                    type = tokenizer.tokenType();
                    token = tokenizer.token();
                    if(type.equals(TokenType.SYMBOL)){
                        if(token.equals("[")){
                            writer.write(TokenType.IDENTIFIER.wrap("term array usage: "+symbolTable.findVariable(identifier))+"\n");
                            writer.write(type.wrap(token)+"\n");
                            token = compileExpression(null,null);
                            writer.write(TokenType.SYMBOL.wrap(token)+"\n");
                            token = null;
                            break;
                        }
                        else if(token.equals("(")){
                            writer.write(TokenType.IDENTIFIER.wrap(identifier)+"\n");
                            writer.write(type.wrap(token)+"\n");
                            compileExpressionList();
                            writer.write(TokenType.SYMBOL.wrap(")")+"\n");
                            token = null;
                            break;
                        }
                        else if(token.equals(".")){
                            SymbolTable.Variable variable = symbolTable.findVariable(identifier);
                            if(variable != null){
                                writer.write(TokenType.IDENTIFIER.wrap("term object method call usage: "+variable)+"\n");
                            }
                            else {
                                writer.write(TokenType.IDENTIFIER.wrap(identifier)+"\n");
                            }

                            writer.write(type.wrap(token)+"\n");
                            if(tokenizer.advance()){
                                type = tokenizer.tokenType();
                                token = tokenizer.token();
                            }
                        }
                        else {
                            writer.write(TokenType.IDENTIFIER.wrap("term plain usage: "+symbolTable.findVariable(identifier))+"\n");
                            break;
                        }
                    }
                }
            }
            else {
                writer.write(type.wrap(token)+"\n");

                if(type.equals(TokenType.SYMBOL)){
                    if(token.equals("(")){
                        compileExpression(null,null);
                        writer.write(TokenType.SYMBOL.wrap(")")+"\n");
                        token = null;
                        break;
                    }
                    else if(token.equals("-") || token.equals("~")){
                        if(tokenizer.advance()) {
                            type = tokenizer.tokenType();
                            token = tokenizer.token();
                            token = compileTerm(type, token);
                            break;
                        }
                    }
                }
                else{
                    token = null;
                    break;
                }
            }
        } while(true);

        writer.write("</term>\n");
        return token;
    }
}


