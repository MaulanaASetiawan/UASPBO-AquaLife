package com.example.gui;

import javafx.beans.property.*;
public class Hewan {
    private final IntegerProperty id;
    private String nama;
    private String jenis;
    private int harga;
    private int stok;
    private String status;
    private String tanggal;
    private String image;


    public Hewan(int id, String nama,  String jenis, int harga, int stok, String status, String tanggal, String image) {
        this.id = new SimpleIntegerProperty(id);
        this.nama = nama;
        this.jenis = jenis;
        this.harga = harga;
        this.stok = stok;
        this.status = status;
        this.tanggal = tanggal;
        this.image = image;
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int habitat) {
        this.harga = habitat;
    }

    public int getStok(){
        return stok;
    }

    public void setStok(int stok){
        this.stok = stok;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
