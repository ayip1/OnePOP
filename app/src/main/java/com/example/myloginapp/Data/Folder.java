package com.example.myloginapp.Data;

import java.util.List;

public class Folder {
    private int id, parentID, orgID, userID;
    private String name;
    private List<Folder> childFolders;
    private List<Receipt> childReceipts;


    public Folder(int id, String name, int parentID) {
        this.id = id;
        this.name = name;
        this.parentID = parentID;
    }

    public int getID() {
        return id;
    }

    public int getParentID() {
        return parentID;
    }

    public String getName() { return name; }

    public List<Folder> getChildFolders() {
        return childFolders;
    }

    public List<Receipt>  getChildReceipts() {
        return childReceipts;
    }

    public void addChildFolder(Folder folder) {
        childFolders.add(folder);
    }

    public void addChildReceipt(Receipt receipt) {
        childReceipts.add(receipt);
    }
}
