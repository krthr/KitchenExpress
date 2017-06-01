package kitchenexpress;

import java.awt.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;

/**
 * Esta clase contiene definidas las propiedades y los métodos que tendrán los
 * objetos tipo Plato, así mismo, contiene métodos que permiten el manejo de la
 * lista de Platos.
 *
 * @author José Polo
 * @author Wilson Tovar
 */
public class Plato {

    int cod;
    String nombre;
    String foodType;
    long value;
    Ingrediente foodIngredientes;
    File imagen;
    String details;
    Plato link;

    Plato() {
    }

    /**
     * Añade un plato a la lista de platos.
     *
     * @param frame JFrame que llamó al método.
     * @param listOfIngredientes Primger ingrediente de la lista de ingredientes
     * del plato.
     * @param nuevo Plato que será agregado a la lista.
     * @return La lista ordenada por nombres.
     */
    Plato addPlato(JFrame frame, Ingrediente listOfIngredientes, Plato nuevo) {
        Plato Ptr = this;
        nuevo.nombre = nuevo.nombre.toUpperCase();
        //Añade platos a una lista de platos
        if (Ptr.nombre == null) {
            Ptr = nuevo;
            Ptr.foodIngredientes = listOfIngredientes;
        } else {
            //Comprueba que no existan el plato en la lista
            Plato Buscar = Ptr;
            while (Buscar.link != null && Buscar.nombre.equals(nuevo.nombre) == false) {
                Buscar = Buscar.link;
            }

            if (Buscar.link == null && Buscar.nombre.equals(nuevo.nombre) == false) {
                Buscar.link = nuevo;//En este momento Buscar sería el último elemento de la lista  
                Buscar.link.foodIngredientes = listOfIngredientes;
            } else {
                JOptionPane.showMessageDialog(frame, "¡El plato ya existe, no será añadido!", "Advertencia", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        return Ptr.sortByName();
    }

    /**
     * Borra un JList y lo llena con los nombres de los platos de una lista tipo
     * Plato.
     *
     * @param lista JList que será usada.
     */
    void toJList(JList lista) {
        Plato Buscar = this;
        DefaultListModel listModel = new DefaultListModel();
        lista.setModel(listModel);
        while (Buscar != null) {
            listModel.addElement(Buscar.nombre);
            Buscar = Buscar.link;
        }
    }

    /**
     * Pasa los datos de la lista de plato a un archivo.
     *
     * @param who JFrame que llamó al método.
     * @param archivoPlato Archivo donde se guardará la lista.
     */
    void toFile(JFrame who, File archivoPlato) {
        Plato Buscar = this;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoPlato))) {
            while (Buscar != null) {
                bw.write(Buscar.toLine());
                bw.newLine();
                Buscar = Buscar.link;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(who, "Algo inesperado ha ocurrido al escribir el archivo.", "Sorry", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Convierte el plato recibido en un String.
     *
     * @return Un String con los datos del plato parseados.
     */
    String toLine() {
        String Cadena = new String();
        Cadena += this.cod;
        Cadena += ";" + this.nombre;
        Cadena += ";" + this.foodType;
        Cadena += ";" + this.value;
        Cadena += ";"; //Indica que empieza la lista de ingredientes
        Ingrediente temp = this.foodIngredientes;

        while (temp != null) {
            Cadena += "[" + temp.toLine() + "]";
            temp = temp.link;
        }
        Cadena += ";" + this.imagen;
        Cadena += ";" + this.details;

        return Cadena;
    }

    /**
     * Cargar los platos de un archivo. Este método sebe ser llamado por el
     * principio de la lista previamente inicializado.
     *
     * @param who JFrame que llamó al método.
     * @param filePlatos Archivo que será leído.
     * @return Lista ordenada por nombres;
     */
    Plato loadFile(JFrame who, File filePlatos) {
        Plato Ptr = this;
        Plato Buscar;
        try {
            Scanner lector = new Scanner(filePlatos);
            while (lector.hasNextLine()) {
                if (Ptr.nombre == null) {
                    Ptr = Ptr.fromLine(lector.nextLine());
                    if (Ptr == null) {
                        Ptr = new Plato();
                    }
                } else {
                    Buscar = Ptr;
                    while (Buscar.link != null) {
                        Buscar = Buscar.link;
                    }
                    Buscar.link = new Plato();
                    Buscar.link = Buscar.link.fromLine(lector.nextLine());
                    if (Buscar.link == null) {
                        JOptionPane.showMessageDialog(who, "Una línea del archivo platos presenta errores y será omitida.", "Lo sentimos", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (FileNotFoundException | HeadlessException e) {
            System.out.println("ERROR (Plato) : " + e);
        }
        return Ptr.sortByName();
    }

    /**
     * Obtener datos de una cadena de texto y crear un Plato a partir de ello.
     *
     * @param line Cadena parseada.
     * @return Objeto plato, si todo salió bien, si no, null.
     */
    Plato fromLine(String line) {
        Plato nuevo = new Plato();
        int separaciones = 0, corchetes = 0;
        String cadenaIngredientes;
        int pos = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.substring(i, i + 1).equals(";")) {
                separaciones++;
            }
            if (line.substring(i, i + 1).equals("[") || line.substring(i, i + 1).equals("]")) {
                corchetes++;
            }
        }
        //Las separaciones sdenrtro de lis ngredientes seran ingual a la mitad de la cantidad de corchetes
        separaciones -= (corchetes / 2);
        if (separaciones == 6) {
            separaciones = 0;
            for (int i = 0; i < line.length(); i++) {
                if (line.substring(i, i + 1).equals("[") || line.substring(i, i + 1).equals("]")) {
                    corchetes++;
                }
                if (line.substring(i, i + 1).equals(";") && corchetes % 2 == 0) {
                    separaciones++;
                    switch (separaciones) {
                        case 1:
                            try {
                                nuevo.cod = Integer.parseInt(line.substring(0, i));
                            } catch (NumberFormatException e) {
                                System.out.println(e + "case 1");
                                return null;
                            }
                            break;
                        case 2:
                            nuevo.nombre = line.substring(pos + 1, i).toUpperCase();
                            break;
                        case 3:
                            nuevo.foodType = line.substring(pos + 1, i);
                            break;
                        case 4:
                            try {
                                nuevo.value = Integer.parseInt(line.substring(pos + 1, i));
                            } catch (NumberFormatException e) {
                                System.out.println(e + "case 4");
                                return null;
                            }
                            break;
                        case 5:
                            cadenaIngredientes = line.substring(pos + 1, i);
                            int position = 0;
                            Ingrediente Buscar;
                            for (int j = 0; j < cadenaIngredientes.length(); j++) {
                                if (cadenaIngredientes.substring(j, j + 1).equals("]")) {
                                    if (nuevo.foodIngredientes == null) {
                                        nuevo.foodIngredientes = new Ingrediente();
                                        nuevo.foodIngredientes = nuevo.foodIngredientes.fromLine(cadenaIngredientes.substring(position + 1, j));
                                    } else {
                                        Buscar = nuevo.foodIngredientes;
                                        while (Buscar.link != null) {
                                            Buscar = Buscar.link;
                                        }
                                        Buscar.link = new Ingrediente();
                                        Buscar.link = nuevo.foodIngredientes.link.fromLine(cadenaIngredientes.substring(position + 1, j));
                                    }
                                } else if (cadenaIngredientes.substring(j, j + 1).equals("[")) {
                                    position = j;
                                }
                            }
                            if (nuevo.foodIngredientes == null) {
                                return null;
                            }
                            break;
                        case 6:
                            if (line.substring(pos + 1, i).equals("null")) {
                                nuevo.imagen = null;
                            } else {
                                nuevo.imagen = new File(line.substring(pos + 1, i));
                            }
                            break;
                    }
                    pos = i;
                }
            }
            nuevo.details = line.substring(pos + 1, line.length());
            return nuevo;
        }
        return null;
    }

    /**
     * Ordenar lista por nombre.
     *
     * @return Primer elemento de la lista.
     */
    Plato sortByName() {
        Plato Buscar1 = this, Buscar2, ant = null, Ptr = this;

        //En cada iteración del ciclo más externo quedará al principio de la lista un número menor que todos los que le siguen
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
     * Puede ser necesario colocar este método entr de un try-catch porque puede
     * dar error
     *
     * @param label es el JLabel en el cual se colocará la imagén
     */
    void sendImage(JLabel label) throws NullPointerException { //throws NullPointerException lanzará una excepción si la imagen del plato es nula
        if (this.imagen == null) {
            throw new NullPointerException("El campo de imagen es nulo.");
        } else {
            try {
                //Extrae la imagen de la ruta
                ImageIcon Picture = new ImageIcon(this.imagen.getAbsolutePath());
                //Cambia el tamaño de la imagen a el tañano del label y lo hace en el tipo Imagen ya que es el que contiene un métoto para tal fin
                Image newImage = Picture.getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
                //Finalmente se coloca el icono en el frame
                label.setIcon(new ImageIcon(newImage));
            } catch (Exception ex) {
                label.setIcon(new ImageIcon(""));
            }
        }
    }

    /**
     * Dice cuantas veces es fabricable un plato de acuerdo con las existencias
     * en bodega.
     *
     * @param plato Es el plato que se pretende realizar. this Es el principio
     * de la lista de platos de la cocina.
     * @param ingredientesBodega es la lista de ingredientes en bodega
     * @return Retorna la candidad de platos realizables.
     */
    int howMuchPlato(PlatoPedido plato, Ingrediente ingredientesBodega) {
        boolean first = true;
        int cant = 0;
        Plato Buscar = this;
        //Busca el plato del pedido en la lista de platos para obtener los ingredientes
        while (Buscar.nombre.equals(plato.nombre) == false && plato.cod != Buscar.cod) {
            Buscar = Buscar.link;
        }
        Ingrediente ingrPlatoFab = Buscar.foodIngredientes;
        //Busca los ingredientes del plato para comprobar que sea fabricable
        while (ingrPlatoFab != null) {
            Ingrediente BuscarIngrediente = ingredientesBodega;
            //Busca el ingrediente el la lista de ingredientes de la bodega para evaluar la cantidad de unidades disponibles.
            while (BuscarIngrediente != null && BuscarIngrediente.nombre.equals(ingrPlatoFab.nombre) == false) {
                BuscarIngrediente = BuscarIngrediente.link;
            }
            if (BuscarIngrediente != null) {
                if (first) {
                    System.out.println(BuscarIngrediente.cant / ingrPlatoFab.cant);
                    cant = BuscarIngrediente.cant / ingrPlatoFab.cant;
                    first = false;
                } else if (cant > BuscarIngrediente.cant / ingrPlatoFab.cant) {
                    cant = BuscarIngrediente.cant / ingrPlatoFab.cant;
                }
            } else {
                //Si uun ingrediente no es encontrado el plato no se puede fabricar
                return 0;
            }
            ingrPlatoFab = ingrPlatoFab.link;
        }
        System.out.println(cant);
        return cant;
    }

    /**
     * Modificar los ingredientes un plato.
     *
     * @param plato Es el plato a relizar
     * @param bodega Es la lista de ingredientes en bodega
     * @return La lista de ingredientes de la bodega con menos ngredientes
     */
    Ingrediente modifIngredientesPlato(PlatoPedido plato, Ingrediente bodega, boolean agregar) {
        Plato Ptr = this, Buscar = this;
        int cant = plato.cant;
        //Busca el plato en la lista de platos de cocina para mirar los ingredientes
        while (Buscar != null && Buscar.cod != plato.cod) {
            Buscar = Buscar.link;
        }

        if (Buscar != null) {
            Ingrediente ingrPlatoFab = Buscar.foodIngredientes;

            while (ingrPlatoFab != null) {
                Ingrediente enBodega = bodega;
                //Busca el ingrediente el la lista de ingredientes de la bodega para descantarlos que se usaran en el plato.
                while (enBodega != null && enBodega.nombre.equals(ingrPlatoFab.nombre) == false) {
                    enBodega = enBodega.link;
                }
                if (enBodega != null) {
                    if (agregar) {
                        enBodega.cant += ingrPlatoFab.cant * cant;
                    } else {
                        enBodega.cant -= ingrPlatoFab.cant * cant;
                    }
                } else {
                    //Si un ingrediente no es encontrado el plato no se puede fabricar
                    System.out.println("Faltan ingredientes.");
                    return null;
                }
                ingrPlatoFab = ingrPlatoFab.link;
            }
        }
        return bodega;
    }
}
