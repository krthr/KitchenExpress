package kitchenexpress;

import java.awt.HeadlessException;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * Es la clase contiene definidas las propiedades y los métodos que tendrán los objetos tipo Pedido.
 * Se usa para alamacenar los datos de los pedidos una vez son solicitados y contiene métodos para facilitar
 * el trabajo con listas de esta clase y archivos manipulables por la misma.
 * @author José Polo
 * @author Wilson Tovar
 */
public class Pedido {

    String tiempo;
    int cod;
    int mesa;
    long valortotal;
    int camarero;
    PlatoPedido Platos;
    Pedido link;

    Pedido() {
    }

    /**
     * Constructor.
     *
     * @param recibido es la hora en la que se recibió el pedido
     */
    Pedido(String recibido) {
        tiempo = calculateTime(recibido, getHour());
    }

    /**
     * Calcular la diferencia de tiempos entre dos instantes diferentes.
     *
     * @param ini
     * @param fin
     * @return El cambio del tiempo.
     */
    static String calculateTime(String ini, String fin) {
        System.out.println("INFO (Pedido) : Tiempo inicial " + ini);
        System.out.println("INFO (Pedido) : Tiempo final " + fin);
        int iniSeconds = 0, finSeconds = 0, result;
        
        if (ini.length() == 6 && fin.length() == 6) {
            iniSeconds += Integer.parseInt(ini.substring(0, 2)) * Math.pow(60, 2);
            iniSeconds += Integer.parseInt(ini.substring(2, 4)) * 60;
            iniSeconds += Integer.parseInt(ini.substring(4, 6));
            System.out.println("INFO (Pedido) : " + iniSeconds);

            finSeconds += Integer.parseInt(fin.substring(0, 2)) * Math.pow(60, 2);
            finSeconds += Integer.parseInt(fin.substring(2, 4)) * 60;
            finSeconds += Integer.parseInt(fin.substring(4, 6));
            System.out.println(finSeconds);

            result = finSeconds - iniSeconds;

            return String.valueOf(result);
        } else if (Integer.parseInt(ini) == 0 && fin.length() == 6) {
            
            finSeconds += Integer.parseInt(fin.substring(0, 2)) * Math.pow(60, 2);
            finSeconds += Integer.parseInt(fin.substring(2, 4)) * 60;
            finSeconds += Integer.parseInt(fin.substring(4, 6));
            
            return String.valueOf(iniSeconds);
        }
        return null;
    }

    /**
     * Obtener la hora exacta.
     *
     * @return La hora como una cadena sin separación.
     */
    static String getHour() {
        GregorianCalendar calendario = new GregorianCalendar();
        String x = calendario.getTime().toString(); //Fecha y hora actual
        int cont = 0, i = 0;
        //Se obtendrá la hora actual
        while (cont < 3) {
            if (x.substring(i, i + 1).equals(" ")) {
                cont++;
            }
            i++;
        }

        String result = x.substring(i, i + 8);

        cont = 0;
        i = 0;
        while (cont < 2) {
            if (result.substring(i, i + 1).equals(":")) {
                result = result.substring(0, i) + result.substring(i + 1, result.length());
                cont++;
            } else {
                i++;
            }
        }
        return result;
    }

    /**
     * Añadir los códigos de una lista de pedidos que comienza en this a un
     * JList.
     *
     * @param lista
     */
    void toJListByCod(JList lista) {
        DefaultListModel listModel = new DefaultListModel();
        lista.setModel(listModel);
        Pedido Buscar = this;
        //Añade los nombre a la lista
        while (Buscar != null) {
            listModel.addElement(Buscar.cod);
            Buscar = Buscar.link;
        }
    }

    /**
     * Añadir los pedidos de una mesa a una lista.
     *
     * @param lista
     * @param mesa
     */
    void toJListByMesa(JList lista, int mesa) {
        DefaultListModel listModel = new DefaultListModel();
        lista.setModel(listModel);
        Pedido Buscar = this;
        //Añade los nombre a la lista
        while (Buscar != null) {
            if (Buscar.mesa == mesa) {
                listModel.addElement(Buscar.cod);
            }
            Buscar = Buscar.link;
        }
    }

    //Añade las mesas de una lista de Pedidos que comienza en @param this a un JList
    //@ mesero es el código del camarero que atendió las mesas a mostrar
    void toJListByCamarero(JList lista, int camarero) {
        DefaultListModel listModel = new DefaultListModel();
        lista.setModel(listModel);
        Pedido Buscar = this;
        //Añade los nombre a la lista
        while (Buscar != null) {
            if (Buscar.camarero == camarero) {
                if (Buscar.mesa <= 9) {
                    listModel.addElement("0" + Buscar.mesa + " - " + Buscar.cod);
                } else {
                    listModel.addElement(Buscar.mesa + " - " + Buscar.cod);
                }
            }
            Buscar = Buscar.link;
        }
    }

    /**
     * Pasa los datos de la lista de Pedudos a un archivo.
     *
     * @param who Es quien ha llamado a este método.
     * @param filePedidos
     */
    void toFile(JFrame who, File filePedidos) {
        Pedido Buscar = this;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePedidos))) {
            while (Buscar != null) {
                bw.write(Buscar.toLine());
                bw.newLine();
                Buscar = Buscar.link;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    who,
                    "Algo inesperado ha ocurrido al escribir el archivo.",
                    "Oops",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Añade un pedido a la lista de pedidos que empieza en @this.
     *
     * @param frame listofPlatos
     * @param nuevo
     * @return Una lista de pedido con el pedido añadido y ordenada por el código.
     */
    Pedido addPedido(JFrame frame, Pedido nuevo) {
        Pedido ptr = this, Buscar;
        if (ptr.camarero == 0) {
            ptr = nuevo;
        } else {
            Buscar = ptr;
            while (Buscar.link != null) {
                Buscar = Buscar.link;
            }
            Buscar.link = nuevo;
        }
        if (frame.getClass().equals(Menú.class)) {
            JOptionPane.showMessageDialog(
                    frame,
                    "¡Pedido añadido correctamente!",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
        return ptr.sortByOrden();
    }

    /**
     * Convierte el Pedido recibido a una línea String;
     * @return Retorna una cadena con la información del plato.
     */
    String toLine() {
        String Cadena = new String();

        Cadena += this.cod;
        Cadena += ";" + this.mesa;
        Cadena += ";" + this.camarero;
        Cadena += ";" + this.valortotal;
        Cadena += ";" + this.tiempo + ";";
        PlatoPedido temp = this.Platos;

        while (temp != null) {
            Cadena += "[" + temp.toLine() + "]";
            temp = temp.link;
        }

        //Cadena += ";" + this.notes;
        return Cadena;
    }

    //Este método sebe ser llamado por el principio de la lista previamente inicializado
    //@param who es el JFrame que llama la función
    Pedido loadFile(JFrame who, File filePedidos) {
        Pedido Ptr = this;
        Pedido Buscar;
        try {
            Scanner lector = new Scanner(filePedidos);
            while (lector.hasNextLine()) {
                if (Ptr.camarero == 0) {
                    Ptr = Ptr.fromLine(who, lector.nextLine());
                    if (Ptr == null) {
                        Ptr = new Pedido();
                    }
                } else {
                    Buscar = Ptr;
                    while (Buscar.link != null) {
                        Buscar = Buscar.link;
                    }
                    Buscar.link = new Pedido();
                    Buscar.link = Buscar.link.fromLine(who, lector.nextLine());
                    if (Buscar.link == null) {
                        JOptionPane.showMessageDialog(
                                who,
                                "Una línea de alguno de los archivos pedidos presenta errores y será omitida.",
                                "Atención",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        } catch (FileNotFoundException | HeadlessException e) {
            System.out.println(e);
        }
        return Ptr;
    }

    /**
     * Extrae la información de un pedido de una cadena si es posible Asigna a
     * this (un pedido) el contenido de una cadena.
     *
     * @param who Es el JFrame que llama la función
     * @param line
     * @return una pedido con la información de una cadena.
     */
    Pedido fromLine(JFrame who, String line) {
        Pedido nuevo = new Pedido();
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
        if (separaciones == 5 && corchetes % 2 == 0) {
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
                                nuevo.valortotal = Integer.parseInt(line.substring(pos + 1, i));
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
                    }
                    pos = i;
                }
            }
            String CadenaPlatosPedidos = line.substring(pos + 1, line.length());
            for (int i = 0; i < CadenaPlatosPedidos.length(); i++) {
                if (CadenaPlatosPedidos.substring(i, i + 1).equals("[")) {
                    pos = i;
                } else if (CadenaPlatosPedidos.substring(i, i + 1).equals("]")) {
                    if (nuevo.Platos == null) {
                        nuevo.Platos = new PlatoPedido();
                        nuevo.Platos = nuevo.Platos.fromLine(CadenaPlatosPedidos.substring(pos + 1, i));
                    } else {
                        PlatoPedido Buscar = nuevo.Platos;
                        while (Buscar.link != null) {
                            Buscar = Buscar.link;
                        }
                        Buscar.link = new PlatoPedido();
                        Buscar.link = Buscar.link.fromLine(CadenaPlatosPedidos.substring(pos + 1, i));
                    }
                }
            }
            if (nuevo.Platos == null) {
                return null;
            }
            return nuevo;
        }
        return null;
    }

    /**
     * Ordena la lista de ingredientes por el número de la mesa
     * @param ant es el anterior a Burscar1
     * @return Retorna la lista oordenada por mesa.
     */
    Pedido sortByMesa() {
        Pedido Buscar1 = this, Buscar2, ant = null, Ptr = this;

        //En cada iteración del ciclo más externo quedará al principiode la lista un número menor que todos los que le siguen
        while (Buscar1 != null) {
            Buscar2 = Buscar1.link;
            while (Buscar2 != null) {
                if (Buscar1.mesa > Buscar2.mesa) {
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
     * Ordena la lista de ingredientes por el en número de la orden
     * @param ant es el anterior a Burscar1
     * @return Retorna la lista ordenada por código.
     */
    Pedido sortByOrden() {
        Pedido Buscar1 = this, Buscar2, ant = null, Ptr = this;

        //En cada iteración del ciclo más externo quedará al principiode la lista un número menor que todos los que le siguen
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
     * Elimimina un pedido de una lista.
     * @param elem Pedido a eliminar.
     * @return Una lista sin elem.
     */
    Pedido eliminarPedido(Pedido elem) {
        Pedido ptr = this, Buscar = this;
        Pedido ant = null;

        while (Buscar != null && Buscar.cod != elem.cod) {
            ant = Buscar;
            Buscar = Buscar.link;
        }

        if (Buscar != null) {
            if (Buscar.cod == elem.cod) {
                if (ant == null) {
                    return ptr.link;
                } else {
                    ant.link = Buscar.link;
                }
                System.out.println("INFO (Pedido) : Eliminado");
            }
        } else {
            System.out.println("INFO (Pedido) : No eliminado");
        }
        return ptr;
    }
}
