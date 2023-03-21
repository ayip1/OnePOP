package com.example.myloginapp.File;

import com.example.myloginapp.R;

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
        Receipt r = new Receipt.Builder().setMetaData(3,"d","d")
                        .setBlob("3")
                        .setReceiptData(1,"3","3","3","3")
                        .setStoreData("3","3","3")
                        .build();

        childReceipts.add(receipt);
    }
}
