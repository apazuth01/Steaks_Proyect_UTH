package com.uth.steaks.ui.pedidos;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class PedidosConstructor {
    private String doc_estado,doc_orden,doc_cliente,doc_direccion;
    private GeoPoint doc_geo;
    private Double doc_total;
    private Date doc_fecha;

    public PedidosConstructor() {}

    public PedidosConstructor(String doc_estado, String doc_orden, String doc_cliente, String doc_direccion, GeoPoint doc_geo, Double doc_total, Date doc_fecha) {
        this.doc_estado = doc_estado;
        this.doc_orden = doc_orden;
        this.doc_cliente = doc_cliente;
        this.doc_direccion = doc_direccion;
        this.doc_geo = doc_geo;
        this.doc_total = doc_total;
        this.doc_fecha = doc_fecha;
    }

    public String getDoc_cliente() {
        return doc_cliente;
    }

    public void setDoc_cliente(String doc_cliente) {
        this.doc_cliente = doc_cliente;
    }

    public String getDoc_direccion() {
        return doc_direccion;
    }

    public void setDoc_direccion(String doc_direccion) {
        this.doc_direccion = doc_direccion;
    }

    public GeoPoint getDoc_geo() {
        return doc_geo;
    }

    public void setDoc_geo(GeoPoint doc_geo) {
        this.doc_geo = doc_geo;
    }

    public String getDoc_estado() {
        return doc_estado;
    }

    public void setDoc_estado(String doc_estado) {
        this.doc_estado = doc_estado;
    }

    public String getDoc_orden() {
        return doc_orden;
    }

    public void setDoc_orden(String doc_orden) {
        this.doc_orden = doc_orden;
    }

    public Double getDoc_total() {
        return doc_total;
    }

    public void setDoc_total(Double doc_total) {
        this.doc_total = doc_total;
    }

    public Date getDoc_fecha() {
        return doc_fecha;
    }

    public void setDoc_fecha(Date doc_fecha) {
        this.doc_fecha = doc_fecha;
    }
}
