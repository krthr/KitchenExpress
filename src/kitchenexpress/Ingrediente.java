package kitchenexpress;

import java.awt.HeadlessException;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;

/**
 * Es la clase contiene definidas las propiedades y los métodos que tendrán los objetos tipo Ingrediente.
 * Se usa para alamacenar los datos de los Ingredientes una vez son creados y contiene métodos para facilitar
 * el trabajo con listas de esta clase y archivos manipulables por la misma.
 * @author José Polo
 * @author Wilson Tovar
 */
public class Ingrediente {

    String nombre;
    int cant;
    Ingrediente link;

    /**
     * Constructor.
     */
    Ingrediente() {
    }

    /**
     * Pasar los datos de la lista de ingredientes a un archivo.
     *
     * @param who Frame que ha llamado al metodo.
     * @param archivoIngrediente
     */
    void toFile(JFrame who, File archivoIngrediente) {
        Ingrediente Buscar = this;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoIngrediente))) {
            while (Buscar != null) {
                bw.write(Buscar.toLine());
                bw.newLine();
                Buscar = Buscar.link;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    who,
                    "Algo inesperado ha ocurrido al escribir el archivo.",
                    "Lo sentimos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Añadir ingrediente a la lista de ingredientes.
     *
     * @param who JFrame que llamó el método
     * @param nuevo
     * @param advice en caso de ser true se mostrrá un aviso al sumar los
     * ingredientes por haber encontrado otro igual.
     * @param sumar en caso ser true sumará los ingredientes si encuentra otro ingrediente igual.
     * @return
     * @throws NullPointerException
     */
    Ingrediente addIngredientes(JFrame who, Ingrediente nuevo, boolean sumar, boolean advice) throws NullPointerException {
        nuevo.nombre = nuevo.nombre.toUpperCase();
        //Ptr es el principio de la lista
        Ingrediente Ptr = this;
        if (nuevo == null) {
            throw new NullPointerException("ERROR : El nuevo ingrediente es nulo.");
        } else //Añadir nuevos elementos a las listas de ingredientes
        //Ptr.nombre solo será "" cuando Ptr no tiene nada
         if (Ptr.nombre == null) {
                Ptr = nuevo;
                Ptr.link = null;
            } else {
                //Comprueba que no existan los ingredientes en la lista
                Ingrediente Buscar = Ptr;
                while (Buscar.link != null && Buscar.nombre.equals(nuevo.nombre) == false) {
                    Buscar = Buscar.link;
                }

                if (Buscar.link == null && Buscar.nombre.equals(nuevo.nombre) == false) {
                    Buscar.link = nuevo;
                } else if (sumar) {
                    if (advice) {
                        JOptionPane.showMessageDialog(
                                who,
                                "¡El ingrediente ya existe, se sumarán las unidades anteriores a las nuevas!",
                                "¡Atención!",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                    Buscar.cant += nuevo.cant;
                }
            }

        // Organiza los elementos de la lista de ingredientes por nombre y los retorna                
        return Ptr.sortByName();
    }

    /**
     * Convierte el ingrediente recibido a una cadena.
     *
     * @return El ingrediente parseado.
     */
    String toLine() {
        String Cadena = new String();
        Cadena += this.nombre;
        Cadena += ";" + this.cant;

        return Cadena;
    }

    /**
     * Este metodo debe ser llamado por el principio de la lista previamente
     * inicializado.
     *
     * @param who
     * @param fileIngredientes
     * @return
     */
    Ingrediente loadFile(JFrame who, File fileIngredientes) {
        Ingrediente Ptr = this;
        Ingrediente Buscar;
        try {
            Scanner lector = new Scanner(fileIngredientes);
            while (lector.hasNextLine()) {
                if (Ptr.nombre == null) {
                    Ptr = Ptr.fromLine(lector.nextLine());
                    if (Ptr == null) {
                        Ptr = new Ingrediente();
                    }
                } else {
                    Buscar = Ptr;
                    while (Buscar.link != null) {
                        Buscar = Buscar.link;
                    }
                    Buscar.link = new Ingrediente();
                    Buscar.link = Buscar.link.fromLine(lector.nextLine());
                    if (Buscar.link == null) {
                        JOptionPane.showMessageDialog(
                                who,
                                "Una línea del archivo ingredientes presenta errores y será omitida.",
                                "Lo sentimos",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        } catch (FileNotFoundException | HeadlessException e) {
            System.out.println("ERROR (Ingrediente) : " + e);
        }

        //Organiza los elementos de la lista de ingredientes por nombre y los retorna  
        return Ptr.sortByName();
    }

    /**
     * Asigna a @this (un ingrediente) el contenido de una cadena.
     *
     * @param line
     * @return
     */
    Ingrediente fromLine(String line) {
        Ingrediente nuevo = new Ingrediente();
        int separaciones = 0;
        int pos = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.substring(i, i + 1).equals(";")) {
                separaciones++;
                pos = i;
            }
        }
        if (separaciones == 1) {
            nuevo.nombre = line.substring(0, pos).toUpperCase();
            try {
                nuevo.cant = Integer.parseInt(line.substring(pos + 1, line.length()));
                return nuevo;
            } catch (NumberFormatException e) {
                System.out.println(e);
            }
        }
        return null;
    }

    /**
     * Ordenar la lista de ingredientes por el nombre.
     *
     * ant es el anterior a Burscar1
     *
     * @return Ptr de la lista de ingredientes.
     */
    Ingrediente sortByName() {
        Ingrediente Buscar1 = this, Buscar2, ant = null, Ptr = this;

        /**
         * En cada iteración del ciclo más externo quedará al principiode la
         * lista el menor que todos los que le siguen
         */
        while (Buscar1 != null) {
            Buscar2 = Buscar1.link;
            while (Buscar2 != null) {
                if (Buscar1.nombre.trim().compareTo(Buscar2.nombre.trim()) > 0) {
                    if (Buscar1 == Ptr) {
                        Ptr = Buscar1.link;
                        Buscar1.link = Buscar2.link;
                        Buscar2.link = Buscar1;

                        Buscar1 = Ptr;
                    } else {
                        ant.link = Buscar1.link;
                        Buscar1.link = Buscar2.link;
                        Buscar2.link = Buscar1;
                        Buscar1 = ant.link;
                    }
                }
                Buscar2 = Buscar2.link;
            }
            ant = Buscar1;
            Buscar1 = Buscar1.link;
        }

        return Ptr;
    }
    
    /**
     * Ordenar la lista de ingredientes por la cantidad de ingredientes.
     *
     * ant es el anterior a Burscar1
     *
     * @return Ptr de la lista de ingredientes.
     */
    Ingrediente sortByCant() {
        Ingrediente Buscar1 = this, Buscar2, ant = null, Ptr = this;

        /**
         * En cada iteración del ciclo más externo quedará al principiode la
         * lista el menor que todos los que le siguen
         */
        while (Buscar1 != null) {
            Buscar2 = Buscar1.link;
            while (Buscar2 != null) {
                if (Buscar1.cant < Buscar2.cant) {
                    if (Buscar1 == Ptr) {
                        Ptr = Buscar1.link;
                        Buscar1.link = Buscar2.link;
                        Buscar2.link = Buscar1;

                        Buscar1 = Ptr;
                    } else {
                        ant.link = Buscar1.link;
                        Buscar1.link = Buscar2.link;
                        Buscar2.link = Buscar1;
                        Buscar1 = ant.link;
                    }
                }
                Buscar2 = Buscar2.link;
            }
            ant = Buscar1;
            Buscar1 = Buscar1.link;
        }

        return Ptr;
    }
}
