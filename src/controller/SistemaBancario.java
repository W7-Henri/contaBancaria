package controller;

import java.util.concurrent.Semaphore;
import java.util.Random;

class Banco {
    private double saldo;
    private final Semaphore semaforoSaque;
    private final Semaphore semaforoDeposito;

    public Banco(double saldoInicial) {
        this.saldo = saldoInicial;
        this.semaforoSaque = new Semaphore(1);
        this.semaforoDeposito = new Semaphore(1);
    }

    public void transacao(String tipo, double valor) {
        try {
            if ("saque".equals(tipo)) {
                semaforoSaque.acquire();
                if (saldo >= valor) {
                    saldo -= valor;
                    System.out.println(Thread.currentThread().getId() + " realizou saque de " + valor + ". Saldo: " + saldo);
                } else {
                    System.out.println(Thread.currentThread().getId() + " tentou sacar " + valor + " mas saldo insuficiente!");
                }
                semaforoSaque.release();
            } else if ("deposito".equals(tipo)) {
                semaforoDeposito.acquire();
                saldo += valor;
                System.out.println(Thread.currentThread().getId() + " realizou depósito de " + valor + ". Saldo: " + saldo);
                semaforoDeposito.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Transacao extends Thread {
    private Banco banco;
    private String tipo;
    private double valor;

    public Transacao(Banco banco, String tipo, double valor) {
        this.banco = banco;
        this.tipo = tipo;
        this.valor = valor;
    }

    @Override
    public void run() {
        banco.transacao(tipo, valor);
    }
}

public class SistemaBancario {
    public static void main(String[] args) {
        Banco banco = new Banco(1000);
        Random random = new Random();
        
        for (int i = 0; i < 20; i++) {
            String tipo = random.nextBoolean() ? "saque" : "deposito";
            double valor = random.nextInt(200) + 50; // Valores entre 50 e 250
            new Transacao(banco, tipo, valor).start();
        }
    }
}
