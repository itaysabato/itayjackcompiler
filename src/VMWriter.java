import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Names: Itay Sabato, Rotem Barzilay <br/>
 * Logins: itays04, rotmus <br/>
 * IDs: 036910008, 300618592 <br/>
 * Date: 03/01/2011 <br/>
 * Time: 19:32:41 <br/>
 */
public class VMWriter {

    // segments:
    public static final String CONST = "constant";
    public static final String ARG = "argument";
    public static final String LCL = "local";
    public static final String STATIC = "static";
    public static final String THIS = "this";
    public static final String THAT = "that";
    public static final String POINT = "pointer";
    public static final String TEMP = "temp";

    // commands:
    public static final String ADD = "add";
    public static final String SUB = "sub";
    public static final String NEG = "neg";
    public static final String EQ = "eq";
    public static final String GT = "gt";
    public static final String LT = "lt";
    public static final String AND = "and";
    public static final String OR = "or";
    public static final String NOT= "not";
    
    private Writer writer;

    public VMWriter(Writer writer) {
        this.writer = writer;
    }

    public void write(String text) throws IOException {
        writer.write(text);
    }

    public void writePush(String segment, int i) throws IOException {
        writer.write("push "+segment+" "+i+"\n");
    }

     public void writePop(String segment, int i) throws IOException {
        writer.write("pop "+segment+" "+i+"\n");
    }

     public void writeArithmetic(String command) throws IOException {
         writer.write(command+"\n");
    }

     public void writeLabel(String label) throws IOException {
         writer.write("label "+label+"\n");
    }

     public void writeGoTo(String label) throws IOException {
         writer.write("goto "+label+"\n");
    }

     public void writeIfGoTo(String label) throws IOException {
         writer.write("if-goto "+label+"\n");
    }

     public void writeCall(String name, int numArguments) throws IOException {
         writer.write("call "+name+" "+numArguments+"\n");
    }

     public void writeFunction(String name, int numLocals) throws IOException {
         writer.write("function "+name+" "+numLocals+"\n");
    }

     public void writeReturn() throws IOException {
         writer.write("return\n");
    }
}
