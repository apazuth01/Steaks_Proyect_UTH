package com.uth.steaks.ui.pedidos;

import java.util.Date;

public class PedidosConstructor {
    private String doc_estado,doc_total,doc_orden;
    private Date doc_fecha;

    public PedidosConstructor() {}

    public PedidosConstructor(String doc_estado, String doc_total, String doc_orden, Date doc_fecha) {
        this.doc_estado = doc_estado;
        this.doc_total = doc_total;
        this.doc_orden = doc_orden;
        this.doc_fecha = doc_fecha;
    }

    public String getDoc_estado() {
        return doc_estado;
    }

    public void setDoc_estado(String doc_estado) {
        this.doc_estado = doc_estado;
    }

    public String getDoc_total() {
        return doc_total;
    }

    public void setDoc_total(String doc_total) {
        this.doc_total = doc_total;
    }

    public String getDoc_orden() {
        return doc_orden;
    }

    public void setDoc_orden(String doc_orden) {
        this.doc_orden = doc_orden;
    }

    public Date getDoc_fecha() {
        return doc_fecha;
    }

    public void setDoc_fecha(Date doc_fecha) {
        this.doc_fecha = doc_fecha;
    }
}
