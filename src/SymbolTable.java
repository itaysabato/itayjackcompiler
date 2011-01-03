import java.util.HashMap;
import java.util.Map;

/**
 * Names: Itay Sabato, Rotem Barzilay <br/>
 * Logins: itays04, rotmus <br/>
 * IDs: 036910008, 300618592 <br/>
 * Date: 03/01/2011 <br/>
 * Time: 19:32:26 <br/>
 */
public class SymbolTable {

    static class Variable {
        final String name;
        final String type;
        final VarKind kind;
        final int index;

        public Variable(String name, String type, VarKind kind, int index) {
            this. name = name;
            this.type = type;
            this.kind = kind;
            this.index = index;
        }

        public String toString() {
            return "name: "+name+" kind: "+kind+" type: "+type+" index: "+index;
        }
    }

    private final Map<String, Variable> globalMap = new HashMap<String, Variable>();
    private final Map<String, Variable> localMap = new HashMap<String, Variable>();
    private int[] indexCounters = {0,0,0,0};

    public void startSubroutine() {
        localMap.clear();
    }

    public void define(String name, String type, VarKind kind) {
        Variable variable = new Variable(name, type, kind, indexCounters[kind.ordinal()]++);

        if(kind.equals(VarKind.STATIC) || kind.equals(VarKind.FIELD)){
            globalMap.put(name, variable);
        }
        else {
            globalMap.put(name, variable);
        }
    }

    public Variable findVariable(String varName) {
        Variable variable = localMap.get(varName);
        if(variable == null){
                variable = globalMap.get(varName);
        }
        return variable;
    }

    public int varCount(VarKind kind) {
        return indexCounters[kind.ordinal()];
    }
}
