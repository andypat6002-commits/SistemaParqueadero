
package parqueadero;

import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {

    private static final String ARCHIVO_VEHICULOS = "vehiculos.txt";
    private static final String ARCHIVO_CAJA = "caja.txt";
    private static final double TARIFA_HORA = 0.50;

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        ArrayList<Vehiculo> vehiculos = new ArrayList<>();

        cargarVehiculos(vehiculos);

        boolean continuar = true;

        while (continuar) {

            System.out.println("\n===== PARQUEADERO =====");
            System.out.println("1. Registrar entrada");
            System.out.println("2. Registrar salida");
            System.out.println("3. Listar vehículos");
            System.out.println("4. Mostrar caja total");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = in.nextLine();

            switch (opcion) {

                case "1":
                    System.out.print("Placa: ");
                    String placa = in.nextLine().trim().toUpperCase();

                    if (placa.isEmpty() || buscar(vehiculos, placa) != null) {
                        System.out.println("Placa vacía o ya registrada.");
                        break;
                    }

                    System.out.print("Marca: ");
                    String marca = in.nextLine().trim();

                    System.out.print("Modelo: ");
                    String modelo = in.nextLine().trim();

                    if (marca.isEmpty() || modelo.isEmpty()
                            || marca.contains(";") || modelo.contains(";")) {
                        System.out.println("Datos inválidos. Intente nuevamente.");
                        break;
                    }

                    Vehiculo nuevo = new Vehiculo(placa, marca, modelo);
                    vehiculos.add(nuevo);

                    guardarVehiculos(vehiculos);

                    System.out.println("Entrada registrada correctamente.");
                    break;

                case "2":
                    System.out.print("Placa del vehículo: ");
                    String placaSalida =
                            in.nextLine().trim().toUpperCase();

                    Vehiculo encontrado = buscar(vehiculos, placaSalida);

                    if (encontrado == null) {
                        System.out.println("Vehículo no encontrado.");
                        break;
                    }

                    LocalDateTime salida = LocalDateTime.now();
                    double cobro = encontrado.calcularCobro(salida);

                    try (PrintWriter archivo = new PrintWriter(
                            new FileWriter(ARCHIVO_CAJA, true))) {

                        archivo.println(
                                encontrado.getPlaca() + ";"
                                + encontrado.getEntrada() + ";"
                                + salida + ";" + cobro
                        );

                    } catch (IOException e) {
                        System.out.println("Error al guardar el cobro.");
                        break;
                    }

                    vehiculos.remove(encontrado);
                    guardarVehiculos(vehiculos);

                    System.out.printf("Total a pagar: $%.2f%n", cobro);
                    System.out.println("Salida registrada correctamente.");
                    break;

                case "3":
                    if (vehiculos.isEmpty()) {
                        System.out.println("No hay vehículos estacionados.");
                    } else {
                        System.out.println("\nVehículos estacionados:");

                        for (Vehiculo auto : vehiculos) {
                            System.out.println(auto);
                        }
                    }
                    break;

                case "4":
                    mostrarCaja();
                    break;

                case "5":
                    continuar = false;
                    System.out.println("Fin del programa.");
                    break;

                default:
                    System.out.println("Opción incorrecta.");
                    break;
            }
        }

        in.close();
    }

    // Busca un vehículo por su placa
    public static Vehiculo buscar(
            ArrayList<Vehiculo> vehiculos, String placa) {

        for (Vehiculo v : vehiculos) {
            if (v.getPlaca().equalsIgnoreCase(placa)) {
                return v;
            }
        }

        return null;
    }

    // Guarda los vehículos que siguen estacionados
    public static void guardarVehiculos(
            ArrayList<Vehiculo> vehiculos) {

        try (PrintWriter archivo = new PrintWriter(
                new FileWriter(ARCHIVO_VEHICULOS))) {

            for (Vehiculo v : vehiculos) {
                archivo.println(
                        v.getPlaca() + ";"
                        + v.getMarca() + ";"
                        + v.getModelo() + ";"
                        + v.getEntrada()
                );
            }

        } catch (IOException e) {
            System.out.println("Error al guardar los vehículos.");
        }
    }

    // Recupera los vehículos cuando inicia el programa
    public static void cargarVehiculos(
            ArrayList<Vehiculo> vehiculos) {

        try (BufferedReader archivo = new BufferedReader(
                new FileReader(ARCHIVO_VEHICULOS))) {

            String linea;

            while ((linea = archivo.readLine()) != null) {
                String[] datos = linea.split(";", -1);

                if (datos.length == 4) {
                    try {
                        Vehiculo v = new Vehiculo(
                                datos[0],
                                datos[1],
                                datos[2],
                                LocalDateTime.parse(datos[3])
                        );

                        if (buscar(vehiculos, datos[0]) == null) {
                            vehiculos.add(v);
                        }

                    } catch (Exception e) {
                        System.out.println(
                                "Se omitió un registro inválido.");
                    }
                }
            }

        } catch (java.io.FileNotFoundException e) {
            // Es normal si el archivo todavía no existe.
        } catch (IOException e) {
            System.out.println("Error al leer los vehículos.");
        }
    }

    // Suma los cobros guardados en caja.txt
    public static void mostrarCaja() {

        double total = 0;

        try (BufferedReader archivo = new BufferedReader(
                new FileReader(ARCHIVO_CAJA))) {

            String linea;

            while ((linea = archivo.readLine()) != null) {
                String[] datos = linea.split(";", -1);

                if (datos.length == 4) {
                    try {
                        total += Double.parseDouble(datos[3]);
                    } catch (NumberFormatException e) {
                        System.out.println("Se omitió un cobro inválido.");
                    }
                }
            }

        } catch (java.io.FileNotFoundException e) {
            // Todavía no hay cobros guardados.
        } catch (IOException e) {
            System.out.println("Error al leer la caja.");
        }

        System.out.printf("Caja total acumulada: $%.2f%n", total);
    }
}
