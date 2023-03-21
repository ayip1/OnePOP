package com.example.myloginapp.Data;

public class Receipt {
    private int id;
    private double total;
    private String uploadDate, purchaseDate, barcode, store, payment, phone, category, blob;
    private Address address;

    public Receipt(Builder builder) {
        id = builder.id;
        total = builder.total;;
        uploadDate = builder.uploadDate;
        purchaseDate = builder.purchaseDate;
        barcode = builder.barcode;
        store = builder.store;
        address = builder.address;
        payment = builder.payment;
        phone = builder.phone;
        category = builder.category;
        blob = builder.blob;

    }
    public static class Builder {
        private int id;
        private double total;
        private String uploadDate, purchaseDate, barcode, store, payment, phone, category, blob;
        private Address address;

        public Builder setMetaData(int id, String uploadDate, String category) {
            this.id = id;
            this.uploadDate = uploadDate;
            this.category = category;
            return this;
        }

        public Builder setBlob(String blob) {
            this.blob = blob;
            return this;
        }
        public Builder setReceiptData(double total, String purchaseDate, String barcode, String payment, String category) {
            this.total = total;
            this.purchaseDate = purchaseDate;
            this.barcode = barcode;
            this.payment = payment;
            return this;
        }

        public Builder setStoreData(String store, Address address, String phone) {
            this.store = store;
            this.address = address;
            this.phone = phone;
            return this;
        }

        public Receipt build() {
            return new Receipt(this);
        }
    }

    public int getID() { return id; }
    public double getTotal() { return total; }
    public String getUploadDate() { return uploadDate; }
    public String getPurchaseDate() { return purchaseDate; }
    public String getBarcode() { return barcode; }
    public String getStore() { return store; }
    public Address getAddress() { return address; }
    public String getPayment() { return payment; }
    public String getPhone() { return phone; }
    public String getCategory() { return category; }
    public String getBlob() { return blob; }

    public void setID(int id) { this.id = id; }
    public void setTotal(double total) { this.total = total; }
    public void setUploadDate(String uploadDate) { this.uploadDate = uploadDate; }
    public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public void setStore(String store) { this.store = store; }
    public void setAddress(Address address) { this.address = address; }


}
