
package parqueadero;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Vehiculo {
    private String placa;
    private String marca;
    private String modelo;
    private LocalDateTime entrada;

    public Vehiculo(String placa, String marca, String modelo) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.entrada = LocalDateTime.now();
    }
    
    public Vehiculo(String placa, String marca, String modelo, LocalDateTime entrada) {
    	this.placa = placa;
    	this.marca = marca;
    	this.modelo = modelo;
    	this.entrada = entrada;
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public double calcularCobro(LocalDateTime salida) {
        long minutos = java.time.Duration.between(
            entrada, salida
        ).toMinutes();

        long horas = Math.max(1, (minutos + 59) / 60);
        return horas * 0.50;
    }

    @Override
    public String toString() {
        return "Placa: " + placa
            + " | Marca: " + marca
            + " | Modelo: " + modelo
            + " | Entrada: " + entrada.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            );
    }
}
