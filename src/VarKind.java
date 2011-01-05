/**
 * Names: Itay Sabato, Rotem Barzilay <br/>
 * Logins: itays04, rotmus <br/>
 * IDs: 036910008, 300618592 <br/>
 * Date: 03/01/2011 <br/>
 * Time: 19:40:00 <br/>
 */
public enum VarKind {
    STATIC(VMWriter.STATIC),
    FIELD(VMWriter.THIS),
    ARG(VMWriter.ARG),
    VAR(VMWriter.LCL);

    String segment;

    VarKind(String segment) {
        this.segment = segment;
    }
}
