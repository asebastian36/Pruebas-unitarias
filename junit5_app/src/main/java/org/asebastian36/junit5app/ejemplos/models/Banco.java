package org.asebastian36.junit5app.ejemplos.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Banco {
    private String nombre;
    private List<Cuenta> cuentas;

    public void addCuenta(Cuenta cuenta) {
        cuentas.add(cuenta);
        cuenta.setBanco(this);
    }

    public Banco() {
        this.cuentas = new ArrayList<>();
    }

    public Banco(String nombre) {
        this();
        this.nombre = nombre;
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void transferir(Cuenta origen, Cuenta destino, BigDecimal valor) {
        origen.debito(valor);
        destino.credito(valor);
    }
}
