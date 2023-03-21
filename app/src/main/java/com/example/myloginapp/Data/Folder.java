package com.example.myloginapp.Data;

import java.util.List;

public class Folder {
    private int id;
    private String name;
    private int parentID;
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
