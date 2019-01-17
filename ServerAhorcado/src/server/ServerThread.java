/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ServerThread implements Runnable {
    public List<String> palabras;
    public int puerto = 8076;
    public boolean sirviendo = true;
    private Thread runningThread;
    public ServerSocket serverSocket;


    public ServerThread() {
        this.palabras = new ArrayList<>();
    }

    
    public void addPalabra(String palabra) {
        this.palabras.add(palabra);
    }
    

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }
    
    private void iniciarServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.puerto);
            System.out.println("creando socket");
        } catch (IOException e) {
            throw new RuntimeException("No es posible abrir el puerto " + String.valueOf(puerto), e);
        }
    }
    
    private synchronized boolean estaSirviendo() {
        return this.sirviendo;
    }

    public synchronized void parar(){
        this.sirviendo = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }
    
    @Override
    public void run() {
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        
        iniciarServerSocket();
        
        Socket sCliente;
        while (estaSirviendo()) {
            
            // aceptar cliente
            try {
                sCliente = this.serverSocket.accept();
                
            } catch (IOException e) {
                throw new RuntimeException(
                    "Error aceptando al cliente", e);
            }
            
            // mensaje dentro del server
            System.out.println("Socket aceptado con direccion: " + sCliente.getInetAddress());

            // preparar palabra
            Random rand = new Random();
            int indiceRand = rand.nextInt(palabras.size());

            // instancia de la partida
            new Thread(
                    new ServicioCliente(sCliente, palabras.get(indiceRand))
            ).start();
        }
    }
}

