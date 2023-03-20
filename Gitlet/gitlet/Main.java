package gitlet;
import java.io.IOException;
/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Christian T. Bernard */
public class Main {
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        InitRepo gitlet = new InitRepo();
        checkInputs(args);
        switch (args[0]) {
        case "add":
            gitlet.retC(); gitlet.retH(); gitlet.retCur(); gitlet.retBr();
            gitlet.addToStage(args[1]);
            gitlet.stC(); gitlet.stH(); gitlet.stBr(); gitlet.stCr(); break;
        case "init":
            gitlet.init();
            gitlet.stC(); gitlet.stH(); gitlet.stBr(); gitlet.stCr(); break;
        case "commit":
            gitlet.retC(); gitlet.retH(); gitlet.retCur(); gitlet.retBr();
            gitlet.commitData(args[1]);
            gitlet.stC(); gitlet.stH(); gitlet.stBr(); gitlet.stCr(); break;
        case "rm":
            gitlet.retC(); gitlet.retH(); gitlet.retCur(); gitlet.retBr();
            gitlet.removeFromStage(args);
            gitlet.stC(); gitlet.stH(); gitlet.stBr(); gitlet.stCr(); break;
        case "log":
            gitlet.retC(); gitlet.retH(); gitlet.retCur(); gitlet.retBr();
            gitlet.logOfCommits();
            gitlet.stC(); gitlet.stH(); gitlet.stBr(); gitlet.stCr(); break;
        case "global-log":
            gitlet.retC(); gitlet.retH(); gitlet.retCur(); gitlet.retBr();
            gitlet.allLogs();
            gitlet.stC(); gitlet.stH(); gitlet.stBr(); gitlet.stCr(); break;
        case "find":
            gitlet.retC(); gitlet.retH(); gitlet.retCur(); gitlet.retBr();
            gitlet.findCommit(args[1]);
            gitlet.stC(); gitlet.stH(); gitlet.stBr(); gitlet.stCr(); break;
        case "status":
            gitlet.retC(); gitlet.retH(); gitlet.retCur(); gitlet.retBr();
            gitlet.status();
            gitlet.stC(); gitlet.stH(); gitlet.stBr(); gitlet.stCr(); break;
        case "checkout":
            gitlet.retC(); gitlet.retH(); gitlet.retCur(); gitlet.retBr();
            gitlet.checkout(args);
            gitlet.stC(); gitlet.stH(); gitlet.stBr(); gitlet.stCr(); break;
        case "branch":
            gitlet.retC(); gitlet.retH(); gitlet.retCur(); gitlet.retBr();
            gitlet.createBranches(args[1]);
            gitlet.stC(); gitlet.stH(); gitlet.stBr(); gitlet.stCr(); break;
        case "rm-branch":
            gitlet.retC(); gitlet.retH(); gitlet.retCur(); gitlet.retBr();
            gitlet.removeBranches(args[1]);
            gitlet.stC(); gitlet.stH(); gitlet.stBr(); gitlet.stCr(); break;
        case "reset":
            gitlet.retC(); gitlet.retH(); gitlet.retCur(); gitlet.retBr();
            gitlet.reset(args[1]);
            gitlet.stC(); gitlet.stH(); gitlet.stBr(); gitlet.stCr(); break;
        case "merge":
            gitlet.retC(); gitlet.retH(); gitlet.retCur(); gitlet.retBr();
            gitlet.stC(); gitlet.stH(); gitlet.stBr(); gitlet.stCr(); break;
        default:
            System.out.println("No command with that name exists.");
        }
    }
    public static void checkInputs(String[] args) {
        if (args.length == 0) {
            System.out.println("No commands have been entered");
            return;
        }
    }
}
