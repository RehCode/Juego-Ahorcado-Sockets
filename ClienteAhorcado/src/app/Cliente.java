/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package app;

import Juego.Grafico;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Cliente {
    int puerto = 8076;
    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        
        if (args.length > 0) {
            try {
                cliente.puerto = Integer.parseInt(args[0]);
                System.out.println("p:" + cliente.puerto);
            } catch (Exception e) {
                System.out.println("El puerto debe ser numerico");
            }
            cliente.conectaCliente();
        } else {
            System.out.println("Para cambiar el puerto: cliente.jar puerto");
            System.out.println("Iniciando con el puerto por default: 8076 en localhost");
            cliente.conectaCliente();
        }
    }
    
    public void conectaCliente(){
          // envia el dobujo del ahorcado cuando son 7 intentos
        Grafico mono = new Grafico();
        boolean mostrarMono = false;

        try (Socket s = new Socket("localhost", puerto)) {
            // preparar entrada y salida
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter outPrint = new PrintWriter(s.getOutputStream(), true);
            Scanner inServer = new Scanner(in);
            
            // entrada de consola en cliente
            Scanner entrada = new Scanner(System.in);
            String cadenaEnvio;
            String respuestaServer;
            
            // consumir mensajes del server hasta el aviso de intentos
            respuestaServer = inServer.nextLine();
            while (!respuestaServer.startsWith("INTENTOS")) {
                System.out.println(respuestaServer);
                respuestaServer = inServer.nextLine();
            }
            String msj;
            // ciclar para recibir y enviar datos
            while (true) {
                
                if (respuestaServer.equals("INTENTOS")) {
                    respuestaServer = inServer.next();
                    
                    if (respuestaServer.contains("7")) {
                        System.out.println("Siete intentos");
                        mostrarMono = true;
                        
                    }
                    
                    System.out.println("\nCuentas con " + respuestaServer + " intentos");
                } else if (respuestaServer.equals("INGRESO")) {
                    System.out.print("Ingresa una letra o una palabra: ");
                    cadenaEnvio = entrada.next().toLowerCase();
                    outPrint.println(cadenaEnvio);
                } else if (respuestaServer.equals("COMPROBAR")) {
                    respuestaServer = inServer.nextLine();
                    System.out.println(respuestaServer);
                    
                    if (mostrarMono) {
                        if (respuestaServer.startsWith("No") && mostrarMono) {
                            // mostrar grafico de 7 intentos
                            int intento = inServer.nextInt();
                            System.out.println(mono.getDibujo(intento));
                        }
                    }

                    
                } else if (respuestaServer.equals("FINAL")) {
                    respuestaServer = inServer.nextLine();
                    System.out.println(respuestaServer);
                    break;
                }
                try {
                    respuestaServer = inServer.nextLine();
                } catch (Exception e) {
                    break;
                }
                
            }

            
        } catch (IOException ex) {
            
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
