#Classes and Respective Methods
## Main Class
* Will have a method called main which takes in String[] args arguments and indexes through them to identify what the respective command the user is trying to call is.
* Calls the init method to initialize the staging area and commit 0. The commit 0 will have only the metadat from the first day computers were born and null blob. The init method will also initialize the stage for addand removing potential commits. Init method also readjusts the HEAD and master pointers to point to the same commit 0.
* gitSerialFile() - creates the hidden repository file that keeps track of the file changes (different blobs) commited to the repository.

##init Class
* Instance variables >> HEAD, Master, directoryFile
* makeZeroCommit - creates Commiit 0 with no blob and timestamp as 00:00:00 UTC, Thursday, 1 January 1970.
* makegitFile - creates the hidden git repository folder to store the sequence changes to the files within (commits), which ones have been added and which ones have not.
* Parent of commit should be null.
* Message "inital commit"
* makeCWD()

##Commit class
Instance variables - parent, parent 2 (for merge operations) master, message, timestamp, blob.
The commit ID is the overarching log of what files are in what state. Thus the commit ID must be some kind of indexable sturcture that you can pull file versions from based off the specification given by the user.

* Timestamp will potentially use the java.Util.Date set of imports to consistently give the exact time of creation of the commit.
* The message will contain information from the author about the nature of the commit
* The parent points to the preceeding commmit of the data structure.
  * A commit can have 2 parents if it is being merged as there may have been changes to old files and a need to restore the current state of files.
* Store Sha1 Value of the current commit and link it to the current commit
* clones parent commit and changes the metadata using a newCommit(String Message, BLOB) function? 
  * Advances the HEAD pointer of the repo to the latest commit.
* addToMaster(Commit commit) - extends the master tree/Hashmap by one and makes the master pointer point to this node in the data structure.
* moveHEAD() to point to the most recent commit version of the file
* trackedFiles(Dictionary <FileName: Serializable BLOB>) - the most recent files tracked by the commit after having been added form the staging area this would essentially be the comparable bit value using the   keys that are also stored in dictionary using the key files to make the comparison the those in the staging area.  
#### Might potentially need a folder to hold all of the commits


##Stage Class
Instance variables - blobs
* Add Stage - Folder of the staging area for the changed files to be added. Functions as a holding area.
  * Checks if the file to be added is valid.
  * Matches clones of existing commited files and creates new blobs for them (trackCommit() method??
  * must be clearable - i.e. the removal of the different files being staged should be possible.
* Deletion Stage - Folder of the staging area for the changed files to be deleted/removed. Functions as a holding area.
  * must be clearable - i.e. the removal of the different files being staged should be possible.

#Blob
Is just a serializable file object that whose serializable code can be generated and interchangeably attached to different keys in the branching hash maps nodes containing names of files as keys}
* Blobs attached to commits have to be the
* Stores the text content of files as bytes? SERIALIZABLE Implemented!?!

##Error Handling
* checkCommitBlob() - checks whether the commit's internal information has changed before adding it to the staging area for commiting. If so it registers this as a new potential commit. This could also be done by comparing the timestamp metadata.
* trackCommit() - tracks what changes were made to a commit incase multiple changes were made.
* find a way to track the files being changed and bring error message if they havent been added to the stage before being committed


#Algorithms
##add
* Checks the files in the Add Stage and its contents
* Hello.txt will be tracked as something that has been changed
  * If changes are made then the new file is replaced with the old when it comes time to commit
    * FILE is REMOVED from the staging area after it is commited
  * Otherwise nothing happens
    * FILE doesnt enter the staging area
##log
* Travels backwards to identify all the commits relevant to the commit
* Might have to feed name of the relevant file into a helper function
* Prints all the commits that have been made for that file
* How to store? Array? LinkedList?

##checkout
* By File Name
  * Takes in a filename and finds the corresponding SHA-1 value for it
  * Finds the HEAD commit for that file
  * Converts the bytes into readable objects and then overwrites the corresponding files before saving them to their master branch

* By Branch
  * identifies the branch of the master thats stores the relevant commit
  * Finds the HEAD commit for that file
  * Converts the bytes into readable objects and then
  * Then overwrites the corresponding files before saving them to their master branch

* By commit ID -- filname
  * Goes through the hidden gitlet file and finds the relevant commit
  * Then overwrites the corresponding files before saving them to their master branch

##rm-rf
    * Finds the staged with/without any commits and removes it. 
- How to handle if the file has been commit? How does this affect the master and the branch containing that commit?


##commit
Takes in a message first
Changes the metadata of the file to be committed.
* References the file being staged and uses new timestamp to compare it to older commits
* Finds where the new commit fits in the master and makes the necessary changes to the master
* Moves the HEAD of the commit to point to the latest one
* Updates the master pointer

## Persistence
* The SHA-1 keys of files added to and removed from the staging areas will be saved
* Commit will store the SHA-1 value, its parent(s) and the blob it contains
* New versions of files will be added to staging area and stored for holding as new blobs


