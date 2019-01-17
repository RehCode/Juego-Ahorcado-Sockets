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
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author reneh
 */
public class ServicioCliente implements Runnable{
    private PrintWriter sOut;
    private BufferedReader sInput;
    private Scanner entrada;
    protected Socket sCliente = null;
    private String palabra;
    
    public ServicioCliente( Socket sCliente, String palabra ) {
        this.sCliente = sCliente;
        this.palabra = palabra;
    }
    
    @Override
    public void run() {
        try {
            // preparar salida y entrada de datos
            sOut = new PrintWriter(sCliente.getOutputStream(), true);
            sInput = new BufferedReader(new InputStreamReader(sCliente.getInputStream()));
            entrada = new Scanner(sInput);
            
            // instancia de la partida
            Mecanica partida = new Mecanica();
            partida.setPalabra(palabra);
            String ingreso;

            // instrucciones
            sOut.println("INSTRUCCIONES");
            sOut.println("Juego del ahorcado");
            sOut.println("\t-Se tienen 7 intentos para adivinar una letra");
            sOut.println("\t-Se gana al acompletar la palabra o al adivinar la palabra\n");
            
            // partida
            sOut.println("INICIO");
            sOut.println("Iniciando una partida..\n\n");
            sOut.println("La palabra tiene " + partida.getPalabra().length() + " letras:");
            sOut.println(partida.getPalabraActual());
            
            String msj;
            
            // ciclar para el manejo del protocolo
            while (true) {
                // ingreso y comprobacion
                sOut.println("INTENTOS");
                sOut.println("" + (7 - partida.getIntento()));
                
                sOut.println("INGRESO");
                ingreso = entrada.next().toLowerCase();

                // comprobacion
                sOut.println("COMPROBAR");
                if (ingreso.length() == 1) {
                    // por letra
                    msj = partida.comprobarLetra(ingreso.charAt(0));
                    sOut.println(msj);
                    if (msj.startsWith("No")) {
                        sOut.println(partida.getIntento());
                    }
                } else {
                    // por palabra
                    if (partida.comprobarPalabra(ingreso)) {
                        sOut.println("Adivinaste la palabra, Ganaste!");
                        break;
                    } else {
                        sOut.println("Palabra Incorrecta");
                    }
                }
                
                // termino de juego
                if (partida.palabraAcompletada() || partida.acabaronIntentos()) {
                    sOut.println("FINAL");
                    if (partida.palabraAcompletada()) {
                        sOut.println("Acompletaste la palabra, ganaste!");
                        break;
                    } else if (partida.acabaronIntentos()){
                        msj = "Se terminaron los intentos, perdiste\n" + "La palabra era: " + partida.getPalabra();
                        sOut.println(msj);
                        break;
                    }  
                }

                if (ingreso.equals("")) {
                    System.out.println("Cliente desconectado");
                    break;
                }
                
            }
            sCliente.close();
        } catch (IOException ex) {
            Logger.getLogger(ServicioCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
