import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class JackCompiler {

    public static void main(String[] arguments) {
        File input = new File(arguments[0]);

        try {
            if(input.isDirectory()){
                for(File file : input.listFiles()){
                    if(file.getName().endsWith(".jack")){
                        compile(file, input);
                    }
                }
            }
            else compile(input,input.getParentFile());
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: "+e.getMessage());
        }
    }

    private static void compile(File input, File location) throws FileNotFoundException {
        JackTokenizer tokenizer = new JackTokenizer(input);
        File output = new File(location, input.getName().replaceAll(".jack", ".my.xml"));

        FileWriter writer = null;
        try {
            writer = new FileWriter(output);
            CompilationEngine compiler = new CompilationEngine(tokenizer, writer);
            compiler.compileClass();

        }
        catch (IOException e) {
            System.out.println("Failed writing to output stream: "+e.getMessage());
        }
        finally {
            tokenizer.close();            
            if(writer != null){
                try {
                    writer.close();
                }
                catch (IOException e) {
                    System.out.println("Failed closing output stream: "+e.getMessage());
                }
            }
        }
    }
}
