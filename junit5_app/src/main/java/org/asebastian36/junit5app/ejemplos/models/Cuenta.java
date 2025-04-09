package org.asebastian36.junit5app.ejemplos.models;

import org.asebastian36.junit5app.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

public class Cuenta {
    private String persona;
    private BigDecimal saldo;
    private Banco banco;

    public void debito(BigDecimal valor) {
        BigDecimal nuevoSaldo = this.saldo.subtract(valor);

        if(nuevoSaldo.compareTo(BigDecimal.ZERO) < 0)  {
            throw new DineroInsuficienteException("Dinero insuficiente");
        }

        this.saldo = nuevoSaldo;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public void credito(BigDecimal valor) {
        this.saldo = this.saldo.add(valor);
    }

    public Cuenta(String persona, BigDecimal saldo) {
        this.saldo = saldo;
        this.persona = persona;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    @Override
    public boolean equals(Object obj) {
        if( !(obj instanceof Cuenta) ) return false;

        Cuenta c = (Cuenta) obj;

        if(this.persona == null || this.saldo == null) return false;

        //  compara el valor de los atributos entre objetos
        return this.persona.equals(c.getPersona()) && this.saldo.equals(c.getSaldo());
    }
}
