/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import juego.Mecanica;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class Server {
    String palabra = "sistemas";
    int puerto = 8076;
    int intentos = 7;
    
    public static void main(String[] args) throws IOException  {
        Server servidor = new Server();
        
        if (args.length == 0) {
            
            System.out.println("Argumentos: server.jar palabra intentos [puerto]");
            System.out.println("El puerto es opcional por default es: 8076");
            System.out.println("Iniciando con configuracion predeterminada");
            
            servidor.iniciaServidor();
        } else if (args.length == 1) {
            servidor.palabra = args[0];
            servidor.iniciaServidor();

        } else if (args.length == 2) {
            servidor.palabra = args[0];
            try {
                servidor.intentos = Integer.parseInt(args[1]);
            } catch (Exception e) {
                System.out.println("Intento contiene letras");
            }
            servidor.iniciaServidor();
            
        } else if (args.length > 2) {
            servidor.palabra = args[0];
            try {
                servidor.intentos = Integer.parseInt(args[1]);
            } catch (Exception e) {
                System.out.println("Intento contiene letras");
            }
            servidor.iniciaServidor();
            try {
                servidor.puerto = Integer.parseInt(args[2]);
            } catch (Exception e) {
                System.out.println("El puerto no es numerico");
            }
        }
        
    }

    
    public void iniciaServidor() throws IOException  {
        
        // inicia el socket servidor
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Server iniciado con la palabra: " + palabra + " puerto: " + puerto +
                    " intentos: " + intentos);
            
            
            // aceptar cliente
            Socket sCliente = serverSocket.accept();
            System.out.println("Socket aceptado con direccion: " + sCliente.getInetAddress());
            
            // preparar salida y entrada de datos
            PrintWriter out = new PrintWriter(sCliente.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(sCliente.getInputStream()));
            Scanner entrada = new Scanner(input);
            
            // instancia de la partida y asignacion de palabra
            Mecanica partida = new Mecanica();
            partida.setPalabra(palabra);
            partida.setIntentos(intentos);
            String ingreso;

            // instrucciones
            out.println("INSTRUCCIONES");
            out.println("Juego del ahorcado");
            out.println("\t-Tienes " + intentos +" intentos para adivinar una letra");
            out.println("\t-Se gana al acompletar la palabra o al adivinar la palabra\n");
            
            // partida
            out.println("INICIO");
            out.println("Iniciando una partida..\n\n");
            out.println("La palabra tiene " + partida.getPalabra().length() + " letras:");
            out.println(partida.getPalabraActual());
            
            String msj;
            boolean enviarIntentos = false;
            // ciclar para el manejo del protocolo
            while (true) {
                // out -> al cliente
                out.println("INTENTOS");
                out.println("" + partida.intentosRestantes());
                if (partida.intentosRestantes() == 7) {
                    enviarIntentos = true;
                }
                
                out.println("INGRESO");
                ingreso = entrada.next().toLowerCase();

                // comprobacion
                out.println("COMPROBAR");
                if (ingreso.length() == 1) {
                    // por letra
                    msj = partida.comprobarLetra(ingreso.charAt(0));
                    out.println(msj);
                    
                    // si no esta enviar el intento para mostrar algo del lado del cliente
                    if (msj.startsWith("No") && enviarIntentos) {
                        out.println(partida.getIntento());
                    }
                    
                } else {
                    // por palabra
                    if (partida.comprobarPalabra(ingreso)) {
                        out.println("Adivinaste la palabra, Ganaste!");
                        break;
                    } else {
                        out.println("Palabra Incorrecta");
                    }
                }
                
                // termino de juego
                if (partida.palabraAcompletada() || partida.acabaronIntentos()) {
                    out.println("FINAL");
                    if (partida.palabraAcompletada()) {
                        out.println("Acompletaste la palabra, ganaste!");
                        break;
                    } else if (partida.acabaronIntentos()){
                        msj = "Se terminaron los intentos, perdiste\n" + "La palabra era: " + partida.getPalabra();
                        out.println(msj);
                        break;
                    }  
                }

                if (ingreso.equals("")) {
                    System.out.println("Cliente desconectado");
                    break;
                }
                
            }
            sCliente.close();
        }
    }
}

