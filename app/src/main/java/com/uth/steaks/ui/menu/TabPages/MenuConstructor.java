package com.uth.steaks.ui.menu.TabPages;

public class MenuConstructor {
    private String doc_titulo,doc_descripcion,doc_image;
    private Double doc_precio;

    public MenuConstructor() {
    }

    public MenuConstructor(String doc_titulo, String doc_descripcion, String doc_image, Double doc_precio) {
        this.doc_titulo = doc_titulo;
        this.doc_descripcion = doc_descripcion;
        this.doc_image = doc_image;
        this.doc_precio = doc_precio;
    }

    public String getDoc_titulo() {
        return doc_titulo;
    }

    public void setDoc_titulo(String doc_titulo) {
        this.doc_titulo = doc_titulo;
    }

    public String getDoc_descripcion() {
        return doc_descripcion;
    }

    public void setDoc_descripcion(String doc_descripcion) {
        this.doc_descripcion = doc_descripcion;
    }

    public String getDoc_image() {
        return doc_image;
    }

    public void setDoc_image(String doc_image) {
        this.doc_image = doc_image;
    }

    public Double getDoc_precio() {
        return doc_precio;
    }

    public void setDoc_precio(Double doc_precio) {
        this.doc_precio = doc_precio;
    }
}
