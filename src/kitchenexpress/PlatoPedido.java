package kitchenexpress;

import javax.swing.*;

/**
 * Esta clase contiene definidas las propiedades y los métodos que tendrán los
 * objetos tipo PlatoPedido, así mismo, contiene métodos que permiten el manejo
 * de la lista de PlatoPedido. Un PlatoPedido es un Plato que fue agregado a un
 * Pedido.
 *
 * @author José y Wilson
 */
public class PlatoPedido {

    String nombre;
    int cod;
    int cant;
    long value;
    PlatoPedido link;

    /**
     * Constructor.
     */
    public PlatoPedido() {
    }

    /**
     * Pasa el contenido de un nodo de PlatoPedido a una Cadena
     *
     * @return La cadena.
     */
    String toLine() {
        String Cadena = new String();
        Cadena += this.cod;
        Cadena += ";" + this.nombre;
        Cadena += ";" + this.value;
        Cadena += ";" + this.cant;
        return Cadena;
    }

    /**
     * Transformar, si es posible, el contenido de una cadena a un PlatoPedido.
     *
     * @param line Cadena
     * @return El PlatoPedido
     */
    PlatoPedido fromLine(String line) {
        PlatoPedido nuevo = this;
        int separaciones = 0, pos = -1; //El valor de pos es -1 ya que la cadena empieza en pos + 1
        //Se comprueba que la cadena ingresada sea válida de acuerdo con las separaciones
        for (int i = 0; i < line.length(); i++) {
            if (line.substring(i, i + 1).equals(";")) {
                separaciones++;
            }
        }
        if (separaciones == 3) {
            separaciones = 0;
            for (int i = 0; i < line.length(); i++) {
                if (line.substring(i, i + 1).equals(";")) {
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
                            nuevo.nombre = line.substring(pos + 1, i);
                            break;
                        case 3:
                            try {
                                nuevo.value = Integer.parseInt(line.substring(pos + 1, i));
                            } catch (NumberFormatException ex) {
                                return null;
                            }
                            break;
                    }
                    pos = i;
                }
            }
            try {
                nuevo.cant = Integer.parseInt(line.substring(pos + 1, line.length()));
            } catch (NumberFormatException ex) {
                return null;
            }
            return nuevo;
        }
        return null;
    }

    /**
     * Añade un PlatoPedido a una lista.
     *
     * @param frame
     * @param nuevo Nuevo PlatoPedido.
     * @return Primer elemento de la lista.
     */
    PlatoPedido addPlato(JFrame frame, PlatoPedido nuevo) {
        PlatoPedido Ptr = this;
        //Añade platos a una lista de platos
        if (Ptr.nombre == null) {
            Ptr = nuevo;
        } else {
            //Comprueba que no exista el plato en la lista
            PlatoPedido Buscar = Ptr;
            while (Buscar.link != null && Buscar.nombre.equals(nuevo.nombre) == false) {
                Buscar = Buscar.link;
            }

            if (Buscar.link == null && Buscar.nombre.equals(nuevo.nombre) == false) {
                Buscar.link = nuevo;//En este momento Buscar sería el último elemento de la lista                
            } else {
                JOptionPane.showMessageDialog(
                        frame,
                        "¡El plato ya existe, no será añadido!",
                        "Advertencia",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
        return Ptr;
    }

    /**
     * Borrar una JList y llenar la misma con los nombre de los platos de una
     * lista tipo Plato. this es el principio de la lista.
     *
     * @param lista
     */
    void toJList(JList lista) {
        PlatoPedido Buscar = this;
        DefaultListModel listModel = new DefaultListModel();
        lista.setModel(listModel);
        //listModel = (DefaultListModel) JListMenúOrdenes.getModel();        
        while (Buscar != null) {
            listModel.addElement(Buscar.nombre);
            Buscar = Buscar.link;
        }
    }

    /**
     * Suma los costos de una lista de platos.
     *
     * @return Suma total.
     */
    int calculateTot() {
        int Total = 0;
        PlatoPedido Buscar = this;
        while (Buscar != null) {
            Total += Buscar.value * Buscar.cant;
            Buscar = Buscar.link;
        }
        return Total;
    }
}
