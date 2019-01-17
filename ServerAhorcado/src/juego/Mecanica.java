
package juego;

public class Mecanica {
    private String palabra;
    private StringBuilder palabraActual;
    private String historial = "";
    int intento = 0;
    private int intentos = 7;
    
    public String getPalabra() {
        return palabra;
    }
    
    public int getIntento(){
        return intento;
    }
    
    public String getPalabraActual() {
        return palabraActual.toString();
    }
    
    public String getHistorial() {
        return historial;
    }
 
    public void setPalabra(String pal) {
        palabra = pal;
        palabraActual = new StringBuilder(pal.length());
        for (int i = 0; i < pal.length(); i++) {
            palabraActual.append('_');
        }
    }
    
    public boolean letraEnHistorial(char letra) {
        if (historial.contains(Character.toString(letra))) {
            return true;
        } else {
            historial = historial + letra;
            return false;
        }
    }
    
    public boolean comprobarPalabra(String pal) {
        return palabra.equalsIgnoreCase(pal);
    }
    
    private boolean letraEnPalabra(char letra) {
        if (palabra.contains(Character.toString(letra))) {
            int pos = 0;
            // coloca la letra adivinada segun avanza en la cadena
            for (int i = 0; i < palabra.length(); i++) {
                pos = palabra.indexOf(letra, i);
                if (pos != -1) {
                    palabraActual.setCharAt(pos, letra);
                }
            }
            return true;
        } else {
            return false;
        }
    }
    
    public String comprobarLetra(char letra) {
        if (letraEnHistorial(letra)) {
            return "Letra en historial";
        }
        if (letraEnPalabra(letra)) {
            return "Existe en: " + palabraActual;
        } else {
            intento++;
            return "No existe la letra: " + letra + "\n";
        }
    }
        
    public boolean palabraAcompletada() {
        return palabra.equals(palabraActual.toString());
    }
    
    public boolean acabaronIntentos() {
        return intento >= getIntentos();
    }
    
    public int intentosRestantes() {
        return intentos - intento;
    }

    /**
     * @return the intentos
     */
    public int getIntentos() {
        return intentos;
    }

    /**
     * @param intentos the intentos to set
     */
    public void setIntentos(int intentos) {
        this.intentos = intentos;
    }

}
