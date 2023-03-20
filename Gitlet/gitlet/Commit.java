package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Date;


public class Commit implements Serializable {
    public Commit() {
        _message = "initial commit";
        SimpleDateFormat formatter = new
                SimpleDateFormat("EEE MMM dd kk:mm:ss YYYY Z");
        _timeStamp = formatter.format(new Date(0));
        _parentID = null;
        _blobs = new HashMap<String, String>();
        _commitID = generateID();
        _log = "===" + "\n" + "commit " + _commitID + "\n"
                + "Date: " + _timeStamp + "\n" + _message + "\n";


    }

    public Commit(String userMessage, String parentID,
                  HashMap<String, String> hashedBlobs) {
        SimpleDateFormat formatter = new
                SimpleDateFormat("EEE MMM dd kk:mm:ss YYYY Z");
        _message = userMessage;
        _timeStamp = formatter.format(new Date());
        _parentID = parentID;
        _blobs = hashedBlobs;
        _commitID = generateID();
        _log = "===" + "\n" + "commit " + _commitID + "\n"
                + "Date: " + _timeStamp + "\n" + _message + "\n";
    }


    public HashMap<String, String> getFileContents() {
        return _blobs;
    }

    public String getMessage() {
        return _message;
    }

    public String getTimeStamp() {
        SimpleDateFormat formatter = new
                SimpleDateFormat("EEE MMM dd kk:mm:ss YYYY Z");
        if (getparentID() == null) {
            _timeStamp = formatter.format(new Date(0));
        } else {
            _timeStamp = formatter.format(new Date());
        }
        return _timeStamp;
    }

    public String getCommitID() {
        return _commitID;
    }

    /**Stores the message of the commit.*/
    private String _message;
    /**Stores the time stamp of the commit.*/
    private static String _timeStamp;
    /**Stores the ID of the commit.*/
    private String _commitID;
    /**Stores the ID of the commit's parent.*/
    private String _parentID;
    /**Stores the printable log of the commit.*/
    private String _log;
    /**Stores the ID of the commit's second parent.*/
    private String parent2ID = null;
    /**HashMap that stores the blobs associated with the commit.*/
    private HashMap<String, String> _blobs;

    public String generateID() {
        byte[] serializedCommit = Utils.serialize(this);
        return Utils.sha1(serializedCommit);
    }

    public String getparentID() {
        return _parentID;
    }

    boolean sameCommit(Commit commit) {
        if (this.getCommitID().equals(commit.getCommitID())) {
            return true;
        }
        return false;
    }

    boolean sameCommitID(String commit) {
        if (this.getCommitID().equals(commit)) {
            return true;
        }
        return false;
    }

    public String getlog() {
        return _log;
    }

    public String commitLog() {
        return "===\n" + "commit " + _commitID + "\n"
                + "Date: " + getTimeStamp() + "\n" + _message + "\n";

    }

    public String gloablCommitLog() {
        return "===" + "\n" + "commit " + _commitID + "\n"
                + "Date: " + getTimeStamp() + "\n" + _message + "\n";

    }
}
