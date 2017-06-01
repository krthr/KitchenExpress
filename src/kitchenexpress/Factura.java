package kitchenexpress;

import java.awt.HeadlessException;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * Es la clase contiene definidas las propiedades y los métodos que tendrán los objetos tipo Factura.
 * Se usa para alamacenar los datos de los pedidos una vez son facturados y contiene métodos para facilitar
 * el trabajo con listas de esta clase y archivos manipulables por la misma.
 * @author José Polo
 * @author Wilson Tovar
 */
public class Factura {

    int cod;
    int mesa;
    int camarero;
    double total;
    String tiempo;
    String fecha_exp;
    PlatoPedido platos;
    Factura link;

    /**
     * Constructor por defecto.
     */
    public Factura() {
    }

    /**
     * Constructor de la clase Factura para un Pedido dado.
     *
     * @param p Pedido inicial de la lista de pedidos.
     */
    public Factura(Pedido p) {
        this.fecha_exp = getDate();
        this.tiempo = p.tiempo;
        this.cod = p.cod;
        this.platos = p.Platos;
        this.mesa = p.mesa;
        this.camarero = p.camarero;
        this.total = (p.valortotal * 0.1) + (p.valortotal * 0.19) + p.valortotal;
    }

    /**
     * Analiza la fecha actual obtenida de una instancia del GregorianCalendar para obtener únicamente de esta
     * la fecha y otra actual.
     *
     * @return Retorna la fecha y la hora actual
     */
    static String getDate() {
        String date = new String();
        GregorianCalendar calendario = new GregorianCalendar();
        //Fecha y hora actual
        String x = calendario.getTime().toString();
        int cont = 0, i = 0;
        //Separará la fecha y hora actuales de forma util
        while (i < x.length()) {
            if (x.substring(i, i + 1).equals(" ")) {
                cont++;
                switch (cont) {
                    case 1:
                        date += x.substring(i + 1, i + 4);
                        break;
                    case 2:
                        date = x.substring(i + 1, i + 3) + "/" + date;
                        break;
                    case 3:
                        date = x.substring(i + 1, i + 9) + " " + date;
                        break;
                    case 5:
                        date += "/" + x.substring(i + 1, i + 5);
                        i = x.length();
                        break;
                }
            }
            i++;
        }
        return (date);
    }

    /**
     * Añadir nueva factura a la lista de facturas.
     *
     * @param nuevo Es la instancia de Factura que se añadirá a la lista de esta clase.
     * @return Retorna una lista que contiene la nueva instancia ordenada por el código.
     */
    Factura addFactura(Factura nuevo) {
        Factura Ptr = this;
        if (this.camarero == 0) {
            return nuevo;
        } else {
            Factura Buscar = Ptr;
            while (Buscar.link != null) {
                Buscar = Buscar.link;
            }
            Buscar.link = nuevo;
        }
        return Ptr.sortByOrden();
    }

    /**
     * Ordenar las facturas por el código de menor a mayor.
     *
     * @return La lista de factura en cuention ordenada.
     */
    Factura sortByOrden() {
        Factura Buscar1 = this, Buscar2, ant = null, Ptr = this;

        // En cada iteración del ciclo más externo quedará al principio de la lista un número menor que todos los que le siguen
        while (Buscar1 != null) {
            Buscar2 = Buscar1.link;
            while (Buscar2 != null) {
                if (Buscar1.cod > Buscar2.cod) {
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
     * Pasar la factura a un archivo.
     *
     * @param who Ventana que llamó al método.
     * @param archivoFactura Archivo de las facturas.
     */
    void toFile(JFrame who, File archivoFactura) {
        Factura Buscar = this;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoFactura))) {
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
     * Intenta exraer datos de facturas de un archivo dado.
     * Este método sebe ser llamado por el principio de la lista previamente
     * inicializado.
     * @param fileFacturas Es el archivo que contiene las facturas.
     * @param who Es el JFrame que llama la función
     * @return Retorna una lista con las facturas que se pudieron cargar del archivo dado.
     */
    Factura loadFile(JFrame who, File fileFacturas) {
        Factura Ptr = this;
        Factura Buscar;
        try {
            Scanner lector = new Scanner(fileFacturas);
            while (lector.hasNextLine()) {
                if (Ptr.camarero == 0) {
                    Ptr = Ptr.fromLine(who, lector.nextLine());
                    if (Ptr == null) {
                        Ptr = new Factura();
                    }
                } else {
                    Buscar = Ptr;
                    while (Buscar.link != null) {
                        Buscar = Buscar.link;
                    }
                    Buscar.link = new Factura();
                    Buscar.link = Buscar.link.fromLine(who, lector.nextLine());
                    if (Buscar.link == null) {
                        JOptionPane.showMessageDialog(
                                who,
                                "Una línea del archivo facturas presenta errores y será omitida.",
                                "Lo sentimos",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        } catch (FileNotFoundException | HeadlessException e) {
            System.out.println("ERROR (Factura) : " + e);
        }
        return Ptr;
    }

    /**
     * Leer los datos de una factura a partir de una linea de texto.
     *
     * this (un Pedido) le es asignado el contenido de una cadena.
     * @param who es el JFrame que llama la función Extrae la información de un
     * pedido de una cadena si es posible.
     * @return Retorna si es posible un pedido con los datos de una cadena.
     */
    Factura fromLine(JFrame who, String line) {
        Factura nuevo = new Factura();
        int separaciones = 0, corchetes = 0, pos = -1; //Pos es -1 porque se usa pos+1 como el principio del substring
        for (int i = 0; i < line.length(); i++) {
            if (line.substring(i, i + 1).equals("[") || line.substring(i, i + 1).equals("]")) {
                corchetes++;
            }
            if (line.substring(i, i + 1).equals(";") && corchetes % 2 == 0) {
                separaciones++;
            }
        }

        //Es necesario que los corchetes sean pares porque es una que abre los platos y la segunda que indica el final de los platos
        if (separaciones == 6 && corchetes % 2 == 0) {
            separaciones = 0;
            corchetes = 0;
            for (int i = 0; i < line.length(); i++) {
                if (line.substring(i, i + 1).equals("[") || line.substring(i, i + 1).equals("]")) {
                    corchetes++;
                }
                if (line.substring(i, i + 1).equals(";") && corchetes % 2 == 0) {
                    separaciones++;
                    switch (separaciones) {
                        case 1:
                            try {
                                nuevo.cod = Integer.parseInt(line.substring(pos + 1, i));
                            } catch (NumberFormatException ex) {
                                return null;
                            }
                            break;
                        case 2:
                            try {
                                nuevo.mesa = Integer.parseInt(line.substring(pos + 1, i));
                            } catch (NumberFormatException ex) {
                                return null;
                            }
                            break;
                        case 3:
                            try {
                                nuevo.camarero = Integer.parseInt(line.substring(pos + 1, i));
                            } catch (NumberFormatException ex) {
                                return null;
                            }
                            break;
                        case 4:
                            try {
                                nuevo.total = Double.parseDouble(line.substring(pos + 1, i));
                            } catch (NumberFormatException ex) {
                                return null;
                            }
                            break;
                        case 5:
                            try {
                                //Comprueba si es posible tratar ese tiempo como entero
                                Integer.parseInt(line.substring(pos + 1, i));
                                nuevo.tiempo = line.substring(pos + 1, i);
                            } catch (NumberFormatException ex) {
                                return null;
                            }
                            break;
                        case 6:
                            nuevo.fecha_exp = line.substring(pos + 1, i);
                            break;
                    }
                    pos = i;
                }
            }
            String CadenaplatosPedidos = line.substring(pos + 1, line.length());
            for (int i = 0; i < CadenaplatosPedidos.length(); i++) {
                if (CadenaplatosPedidos.substring(i, i + 1).equals("[")) {
                    pos = i;
                } else if (CadenaplatosPedidos.substring(i, i + 1).equals("]")) {
                    if (nuevo.platos == null) {
                        nuevo.platos = new PlatoPedido();
                        nuevo.platos = nuevo.platos.fromLine(CadenaplatosPedidos.substring(pos + 1, i));
                    } else {
                        PlatoPedido Buscar = nuevo.platos;
                        while (Buscar.link != null) {
                            Buscar = Buscar.link;
                        }
                        Buscar.link = new PlatoPedido();
                        Buscar.link = Buscar.link.fromLine(CadenaplatosPedidos.substring(pos + 1, i));
                    }
                }
            }
            if (nuevo.platos == null) {
                return null;
            }
            return nuevo;
        }
        return null;
    }

    /**
     * Parsear los datos de una factura a una cadena de textos.
     *
     * @return Retorna una cadena con los datos de una factura.
     */
    String toLine() {
        String Cadena = new String();

        Cadena += this.cod;
        Cadena += ";" + this.mesa;
        Cadena += ";" + this.camarero;
        Cadena += ";" + this.total;
        Cadena += ";" + this.tiempo;
        Cadena += ";" + this.fecha_exp + ";";
        PlatoPedido temp = this.platos;

        while (temp != null) {
            Cadena += "[" + temp.toLine() + "]";
            temp = temp.link;
        }
        return Cadena;
    }
    
    /**
     * Obtener el código de la factura.
     * @return Código de la factura (entero).
     */
    public int getCod() {
        return this.cod;
    }
    
    /**
     * Obtener la mesa de la factura.
     * @return Número de la mesa.
     */
    public int getMesa() {
        return this.mesa;
    }
    
    /**
     * Obtener el valor total del pedido.
     * @return Total del pedido (real).
     */
    public double getTotal() {
        return this.total;
    }
    
    /**
     * Obtener el enlace del pedido.
     * @return Siguiente Factura en la lista.
     */
    public Factura getLink() {
        return this.link;
    }
}
