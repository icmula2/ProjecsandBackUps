package gitlet;

import java.io.Serializable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * Repo class for Gitlet,
 * that turns the current folder into the CWD of the repository.
 *
 * @author Christian T. Bernard
 */
public class InitRepo implements Serializable {
    /**Path to the repository folder.*/
    private final String repoPATH = ".gitlet";
    /**Path to the blobs folder.*/
    private final String repoBLOBS = ".gitlet/blobs";
    /**Path to the staging folder.*/
    private final String repoSTAGE = ".gitlet/stage";
    /**Path to the commits folder.*/
    private final String repoCOMMITS = ".gitlet/commits";
    /**Path to the remove folder.*/
    private final String repoREMOVE = ".gitlet/remove";

    public Commit getHead() {
        return _head;
    }

    public InitRepo() {
        cWD = new File(System.getProperty("user.dir"));
        allCommits = new ArrayList<Commit>();
    }

    public void init() throws IOException {
        File repoExists = new File(repoPATH);
        if (!repoExists.exists()) {
            File savedddCommits = new File(repoPATH + "/.savedCommit.txt");
            File saveddddHead = new File(repoPATH + "/.savedHeads.txt");
            File saveddddBranches = new File(repoPATH + "/.savedBranches.txt");
            File savedCurBranch = new File(repoPATH + "/.savedCurrBranch.txt");
            File gitletFolder = new File(repoPATH);
            allBranches = new HashMap<String, String>();
            blobFolder = new File(repoBLOBS);
            stageFolder = new File(repoSTAGE);
            commitsFolder = new File(repoCOMMITS);
            removeFolder = new File(repoREMOVE);


            gitletFolder.mkdirs();
            blobFolder.mkdirs();
            stageFolder.mkdirs();
            removeFolder.mkdirs();
            commitsFolder.mkdirs();

            savedCommits.createNewFile();
            savedHead.createNewFile();
            savedBranches.createNewFile();
            savedCurrBranch.createNewFile();

            Commit firstCommit = new Commit();
            _head = firstCommit;
            currBranch = "master";
            allCommits.add(firstCommit);
            allBranches.put(currBranch, _head.getCommitID());
            Utils.writeObject(new File(commitsFolder + File.separator
                    + firstCommit.getCommitID() + ".txt"), _head);
        } else {
            System.out.println("A Gitlet version-control "
                    + "system already exists in the current directory.");
        }
    }

    public void addToStage(String fileName) throws IOException {
        allBranches.size();
        File toBeStaged = new File(".gitlet/stage/" + fileName);
        File temp = new File(fileName);
        if (!temp.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        String tempHash = generateBlobHash(temp);
        if (_head.getFileContents().keySet().contains(fileName)) {
            if (!_head.getFileContents().get(fileName).equals(tempHash)) {
                if (!toBeStaged.exists()) {
                    toBeStaged.createNewFile();
                }
                clearRemovals(fileName);
            } else {
                clearRemovals(fileName);
                return;
            }
        }
        Utils.writeContents(toBeStaged, Utils.readContents(temp));
        trackedFiles.add(fileName);
    }

    public void clearStage() {
        for (File file1 : stageFolder.listFiles()) {
            file1.delete();
        }
    }

    public void clearRemovals(String fileName) {
        File fileInRemove = Utils.join(repoREMOVE, fileName);
        if (fileInRemove.exists()) {
            fileInRemove.delete();
        }
    }

    public String generateBlobHash(File file) {
        String hash = Utils.sha1(Utils.readContents(file));
        return hash;
    }

    public void makeblobFile(String code, File file) {
        File blobFile = new File(repoBLOBS + File.separator + code);
        Utils.writeContents(blobFile, Utils.readContents(file));

    }

    public void updateBranches(String branchName, String commitID) {
        allBranches.put(branchName, commitID);
    }

    @SuppressWarnings("unchecked")
    public void commitData(String message) throws IOException {
        List stageChecking = Utils.plainFilenamesIn(repoSTAGE);
        List removeChecking = Utils.plainFilenamesIn(repoREMOVE);
        if (stageChecking.size() == 0 && removeChecking.size() == 0) {
            System.out.println("No changes added to the commit.");
        } else if (message.equals("")) {
            System.out.println("Please enter a commit message.");
        }
        if (removeFolder.listFiles().length > 0) {
            for (File toDelete : removeFolder.listFiles()) {
                toDelete.delete();
            }
        }
        HashMap<String, String> blobMap = (HashMap<String, String>)
                _head.getFileContents().clone();
        for (File file : stageFolder.listFiles()) {
            String hash = generateBlobHash(file);
            blobMap.put(file.getName(), hash);
            makeblobFile(hash, file);
        }
        Commit cNew = new Commit(message, getHead().getCommitID(), blobMap);
        _head = cNew;
        File file = new File(repoCOMMITS
                + File.separator + cNew.getCommitID() + ".txt");
        file.createNewFile();
        Utils.writeObject(file, cNew);
        allCommits.add(cNew);
        updateBranches(currBranch, getHead().getCommitID());
        clearStage();
        trackedFiles = new ArrayList<>();

    }


    public void logOfCommits() {
        Commit commit = _head;
        while (commit != null) {
            System.out.println(commit.getlog());
            if (commit.getparentID() == null) {
                break;
            }
            commit = Utils.readObject(Utils.join(commitsFolder,
                    commit.getparentID() + ".txt"), Commit.class);
        }

    }

    public boolean checkoutCheck1(String[] args) {
        boolean mCrit = false;
        if (((args.length == 4) && (args[2].equals("--")))
                || ((args.length == 3) && (args[1].equals("--")))
                || (args.length == 2)) {
            mCrit = true;
        }
        return mCrit;
    }

    public void checkoutHealper(String[] args) {
        if (!allBranches.containsKey(args[1])) {
            System.out.println("No such branch exists."); return;
        }
        if (currBranch.equals(args[1])) {
            System.out.println("No need to "
                    + "checkout the current branch.");
            return;
        }
        String branchCommit = allBranches.get(args[1]);
        reset(branchCommit); currBranch = args[1];
    }


    public void checkout(String[] args) throws IOException {
        if (args.length  == 2 || args.length == 3 || args.length == 4) {
            if (checkoutCheck1(args)) {
                boolean checkedOut = false;
                if (args.length == 4) {
                    String fileName = args[3]; String commitID = args[1];
                    for (File file : commitsFolder.listFiles()) {
                        if (file.getName().contains(commitID)) {
                            Commit checkCommit = Utils.readObject(file,
                                    Commit.class);
                            @SuppressWarnings("unchecked")
                            HashMap blobMap = (HashMap<String, String>)
                                    checkCommit.getFileContents().clone();
                            String blobCode = null;
                            if (blobMap.keySet().contains(fileName)) {
                                blobCode = (String) blobMap.get(fileName);
                            } else {
                                System.out.print("File does not "
                                    + "exist in that commit.");
                                return;
                            }
                            for (File blobFile
                                    : new File(repoBLOBS).listFiles()) {
                                if ((blobFile.getName()).equals(blobCode)) {
                                    checkedOut = true; byte[] contents =
                                            Utils.readContents(blobFile);
                                    Utils.writeContents(new File(cWD + File
                                            .separator + fileName), contents);
                                }
                            }
                        }
                    }
                    if (!checkedOut) {
                        System.out.println("No commit with that id exists.");
                    }
                } else if (args.length == 3) {
                    String fileName = args[2];
                    File reference = Utils.join(cWD, "/.gitlet/blobs/"
                            + getHead().getFileContents().get(fileName));
                    byte[] contents = Utils.readContents(reference);
                    Utils.writeContents(Utils.join(cWD, fileName), contents);
                } else if (args.length == 2) {
                    checkoutHealper(args);
                } else {
                    System.out.println("File does not exist in that commit.");
                }
            } else {
                System.out.println("Incorrect operands."); return;
            }
        } else {
            System.out.println("Incorrect syntax"); return;
        }
    }

    public void allLogs() {
        for (int i = allCommits.size() - 1; i >= 0; i--) {
            System.out.println(allCommits.get(i).getlog());
        }
    }

    public void reset(String commitID) {
        ArrayList<File> rtrackedFiles = new ArrayList<>();
        File resetComFile = Utils.join(commitsFolder, commitID + ".txt");
        Commit resetCommit;
        if (Utils.join(commitsFolder, commitID + ".txt").exists()) {
            resetCommit = Utils.readObject(resetComFile, Commit.class);
        } else {
            System.out.println("No commit with that id exists.");
            return;
        }
        for (File file : cWD.listFiles()) {
            if (file.getName().contains(".txt")) {
                rtrackedFiles.add(file);
            }
        }
        for (File file : rtrackedFiles) {
            if (resetCommit.getFileContents().keySet().contains(file.getName())
                    && !(_head.getFileContents().keySet().contains(
                            file.getName()))) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return;
            }
        }
        for (File file : rtrackedFiles) {
            if (!resetCommit.getFileContents().keySet().contains(
                    file.getName())) {
                Utils.restrictedDelete(file);
            }
        }
        for (String fileName : resetCommit.getFileContents().keySet()) {
            File toCWD = Utils.join(fileName);
            String blobCode = resetCommit.getFileContents().get(fileName);
            Utils.writeContents(toCWD,
                    Utils.readContents(Utils.join(repoBLOBS, blobCode)));
        }
        _head = resetCommit;
        clearStage();
        trackedFiles = new ArrayList<>();


    }

    public void createBranches(String branchName) {
        if (allBranches.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        allBranches.put(branchName, _head.getCommitID());
    }

    public void removeBranches(String branchName) {
        if (currBranch.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        if (!allBranches.keySet().contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        allBranches.remove(branchName);

    }

    public void findCommit(String msg) {
        boolean found = false;
        for (Commit commit : allCommits) {
            if (commit.getMessage().equals(msg)) {
                System.out.println(commit.getCommitID());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }

    }


    public void removeFromStage(String[] args) {
        String fileName = args[1];
        if (!Utils.join(cWD, fileName).exists()) {
            if ((Utils.join(repoBLOBS,
                    _head.getFileContents().get(fileName)).exists())) {
                File content = Utils.join(repoBLOBS,
                        _head.getFileContents().get(fileName));
                File delete = Utils.join(removeFolder, fileName);
                Utils.writeContents(delete, Utils.readContents(content));
                return;
            }
        }

        File fileInStage = Utils.join(repoSTAGE + File.separator, fileName);
        File fileInCWD = new File(fileName);
        String hash = generateBlobHash(
                Utils.join(fileName));
        File fileInBlobs = Utils.join(repoBLOBS
                + File.separator, hash);
        if (fileInStage.exists()) {
            fileInStage.delete();
            return;
        }
        trackedFiles.add(fileName);
        Utils.writeContents(Utils.join(repoREMOVE, fileName),
                Utils.readContents(fileInCWD));
        if (getHead().getFileContents().keySet().contains(fileName)) {
            if (fileInCWD.exists()) {
                Utils.writeContents(Utils.join(repoREMOVE, fileName),
                        Utils.readContents(fileInCWD));
                Utils.restrictedDelete(new File(fileName));
                return;
            } else {
                String blobHash = getHead().getFileContents().get(fileName);
                if (new File(".gitlet/blobs/" + blobHash).exists()) {
                    Utils.writeContents(Utils.join(repoREMOVE, fileName),
                            Utils.readContents(fileInBlobs));
                    return;
                }
            }
        } else {
            System.out.println("No reason to remove the file.");
        }
    }


    public void status() {
        ArrayList<String> bR = new ArrayList<>(allBranches.keySet());
        System.out.println("=== Branches ===");
        for (int i = bR.size() - 1; i >= 0; i--) {
            if (currBranch.equals(bR.get(i))) {
                System.out.println("*" + bR.get(i));
            } else {
                System.out.println(bR.get(i));
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (File file : stageFolder.listFiles()) {
            System.out.println(file.getName());
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (File rfile : new File(".gitlet/remove").listFiles()) {
            System.out.println(rfile.getName());
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (File file : cWD.listFiles()) {
            String fName = file.getName();
            if (_head.getFileContents().keySet().contains(fName)
                    && trackedFiles.contains(fName)) {
                System.out.println(fName);
            }
        }
    }

    public void stC() throws IOException {
        Utils.writeObject(savedCommits, allCommits);
    }
    public void stH() throws IOException {
        Utils.writeObject(savedHead, _head);
    }
    public void stCr() throws IOException {
        Utils.writeObject(savedCurrBranch, currBranch);
    }
    public void stBr() throws IOException {
        Utils.writeObject(savedBranches, allBranches);
    }

    @SuppressWarnings("unchecked")
    public void retC() {
        allCommits = Utils.readObject(savedCommits, ArrayList.class);
    }
    public void retH() {
        _head = Utils.readObject(savedHead, Commit.class);
    }
    @SuppressWarnings("unchecked")
    public void retBr() {
        allBranches = Utils.readObject(savedBranches, HashMap.class);
    }
    public void retCur() {
        currBranch = Utils.readObject(savedCurrBranch, String.class);
    }

    /**HashMap to store the branches and associated commits.*/
    private HashMap<String, String> allBranches = new HashMap<String, String>();
    /**Stores the current master branch.*/
    private String currBranch;
    /**Stores all of the commits.*/
    private ArrayList<Commit> allCommits;
    /**Stores the head commit.*/
    private Commit _head;
    /**File object of the current working directory.*/
    private File cWD;
    /**Stores the files being tracked by the current comit and the user.*/
    private ArrayList<String> trackedFiles = new ArrayList<String>();
    /**File object of the stage folder.*/
    private File stageFolder = new File(repoSTAGE);
    /**File object of the remove folder.*/
    private File removeFolder = new File(repoREMOVE);
    /**File object of the commits folder.*/
    private File commitsFolder = Utils.join(repoPATH, "commits");
    /**File object of the blob folder.*/
    private File blobFolder = Utils.join(repoBLOBS, "blobs");
    /**Stores the head for peristence.*/
    private File savedHead = Utils.join(repoPATH, ".savedHeads.txt");
    /**Stores the commits for peristence.*/
    private File savedCommits = Utils.join(repoPATH, ".savedCommits.txt");
    /**Stores the branches for peristence.*/
    private File savedBranches = Utils.join(repoPATH, ".savedBranches.txt");
    /**Stores the current branch for peristence.*/
    private File savedCurrBranch = Utils.join(repoPATH, ".savedCurrBranch.txt");
}
