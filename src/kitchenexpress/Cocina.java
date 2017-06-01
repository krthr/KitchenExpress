package kitchenexpress;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

/**
 * Es el frame donde se añaden los ingredientes, los platos, se realiza la elaboración de pedidos y ejecutan
 * los métodos del administrador de cocina al final de día.
 * @author José Polo
 * @author Wilson Tovar 
 */
public class Cocina extends javax.swing.JFrame {

    JFileChooser imageChooser;
    JTable tablaPedidosCocina;
    JTable tablaIngredientesAdmin;
    DefaultTableModel model;
    Ingrediente Ingredientes, nuevoIngrediente;
    Plato Platos, nuevoPlato;
    Pedido Pedidos, PedidosParaEntrega, PedidosEntregados;
    DefaultListModel listModel;
    Factura Facturas; // Primer nodo de la lista de facturas
    int codPlato;
    int NumIngredientes; //Numero de ingredientes de un plato

    /**
     * Crea una nueva intacia de Cocina
     */
    public Cocina() {
        initComponents();
        Ini();
    }
    
    /**
     * Operaciones que se deben realizar al crear una instancia de la clase Cocina como cargar los archivos, entre otras.
     */
    void Ini() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("Icons/Logo7059.png"))); // Icono
        codPlato = 0;

        //Edita el FileChooser
        imageChooser = new JFileChooser();
        imageChooser.setAcceptAllFileFilterUsed(false);
        imageChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "tif"));

        imgDir.setEditable(false);

        listModel = new DefaultListModel();
        tablaPedidosCocina = new JTable();
        tablaIngredientesAdmin = new JTable();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        administradorCocinaFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        añadirIngrediente.setDefaultCloseOperation(EXIT_ON_CLOSE);
        endOfDayFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        endOfDayFrame.setLocationRelativeTo(null);
        añadirIngrediente.setLocationRelativeTo(null);
        administradorCocinaFrame.setResizable(false);
        this.setResizable(false);

        //Quita los bordes de los íconos
        Principal.quitBorder(atrasButton);
        Principal.quitBorder(okButton);
        Principal.quitBorder(atrasButton1);
        Principal.quitBorder(AddPlatoButton);
        Principal.quitBorder(AddIngredienteCallButton);
        Principal.quitBorder(atrasButton2);
        Principal.quitBorder(AddIngredienteButton);
        Principal.quitBorder(EndOfDayButton);
        Principal.quitBorder(atrasButton3);
        administradorCocinaFrame.setLocationRelativeTo(null);

        //Edita modelo de la tabla de pedidos de cocina
        model = new DefaultTableModel();
        model.addColumn("Numero de Orden");
        model.addColumn("Mesa");
        model.addColumn("Platos");
        model.addColumn("V:");
        tablaPedidosCocina.setModel(model);
        tablaPedidosCocina.setFont(new Font("Calibri", 0, 16));

        //Añadiendo la tabla al Scroll Panel
        PedidosCocinaScroll.getViewport().add(tablaPedidosCocina);
        tablaPedidosCocina.setVisible(true);

        model = new DefaultTableModel();
        model.addColumn("Ingrediente");
        model.addColumn("Cantidad");
        model.addColumn("Bodega");
        model.addColumn("V:");
        tablaIngredientesAdmin.setModel(model);
        ingredientesAdminScroll.getViewport().add(tablaIngredientesAdmin);
        tablaIngredientesAdmin.setVisible(true);

        //Cargar los items en el combobox
        TypeComboBox.removeAllItems();
        TypeComboBox.addItem("Bebida");
        TypeComboBox.addItem("Plato");
        TypeComboBox.addItem("Postre");
        TypeComboBox.setSelectedIndex(1);

        //Quitar los items de JList
        JListPlatos.setModel(listModel);

        //Cargar archivo de Ingredientes
        Ingredientes = new Ingrediente();
        Ingredientes = Ingredientes.loadFile(this, Inicio.IngredientesFile);
        if (Ingredientes.nombre != null) {
            cargarIngredientesTablaAdmin();
        }

        //Cargar archivo de Platos
        Platos = new Plato();
        Platos = Platos.loadFile(this, Inicio.PlatosFile);
        if (Platos.nombre != null) {
            //Carga la lista de platos en el menú
            Platos.toJList(Inicio.ventanaMenú.JListMenú);

            //Carga la lista de platos en Ordenes
            Platos.toJList(Inicio.ventanaMenú.JListMenúOrdenesPlatos);

            //Carga la lista de platos en cocina
            Platos.toJList(JListPlatos);

            //Encontrar el código consecutivo
            Plato Buscar = Platos;
            while (Buscar.link != null) {
                if (Buscar.cod > codPlato) {
                    codPlato = Buscar.cod;
                }
                Buscar = Buscar.link;
            }
            codPlato++;
            codTxt.setText("Código: # " + codPlato);
        }

        //Cargar pedidos
        Pedidos = new Pedido();
        Pedidos = Pedidos.loadFile(this, Inicio.PedidosFile);
        if (Pedidos.camarero != 0) {
            cagarPedidos(Pedidos);

            //encontrar código consecutivo
            Pedido Buscar = Pedidos;
            while (Buscar.link != null) {
                Buscar = Buscar.link;
            }
            Inicio.ventanaMenú.pedCod = Buscar.cod + 1;
            //Carga los pedidos dela primera mesa en el respectivo Jlist
            Pedidos.toJListByMesa(Inicio.ventanaMenú.JListPedidosGenerados, 1);
        }

        //Cargar pedidos terminados
        PedidosParaEntrega = new Pedido();
        PedidosParaEntrega = PedidosParaEntrega.loadFile(this, Inicio.PedidosParaEntregar);
        if (PedidosParaEntrega.camarero != 0) {
            PedidosParaEntrega.toJListByCod(PedidosParaEntregarList);

            //encontrar código consecutivo
            Pedido Buscar = PedidosParaEntrega;
            while (Buscar.link != null) {
                Buscar = Buscar.link;
            }
            //Cambia el código de pedidos si no es mayor que el mayor +1 de los pedidos ya hechos
            if (Inicio.ventanaMenú.pedCod < Buscar.cod + 1) {
                Inicio.ventanaMenú.pedCod = Buscar.cod + 1;
            }

            //Actualiza la tabla de pedidos por entregar en las notificaciones
            Inicio.ventana1.actualizarTablaPedidosPorEntregar(PedidosParaEntrega);
        }

        //Cargar pedidos entregados
        PedidosEntregados = new Pedido();
        PedidosEntregados = PedidosEntregados.loadFile(this, Inicio.PedidosEntregados);
        if (PedidosEntregados.camarero != 0) {

            //Actualizar la tabla de pedidos entregados en las notificaciones
            PedidosEntregados.toJListByCamarero(Inicio.ventana1.jlistPedidosEntregados, Inicio.ventana1.camarero);

            //encontrar código consecutivo
            Pedido Buscar = PedidosEntregados;
            while (Buscar.link != null) {
                Buscar = Buscar.link;
            }
            //Cambia el código de pedidos si no es mayor que el mayor +1 de los pedidos ya hechos
            if (Inicio.ventanaMenú.pedCod < Buscar.cod + 1) {
                Inicio.ventanaMenú.pedCod = Buscar.cod + 1;
            }
        }

        //Cargar facturas generadas
        Facturas = new Factura();
        Facturas = Facturas.loadFile(this, Inicio.FacturasFile);
        if (Facturas.camarero != 0) {

            //Encuentra el código consecutivo
            Factura Buscar = Facturas;
            while (Buscar.link != null) {
                Buscar = Buscar.link;
            }
            //Cambia el código de pedidos si no es mayor que el mayor +1 de los pedidos ya hechos
            if (Inicio.ventanaMenú.pedCod < Buscar.cod + 1) {
                Inicio.ventanaMenú.pedCod = Buscar.cod + 1;
            }
        }
    }

    MouseListener mouse = new MouseListener() {
        @Override
        /**
         * Evento que carga los platos del pedido en cuention y los muestra en un JList.
         */
        public void mouseClicked(MouseEvent e) {
            try {
                int numPed = Integer.parseInt(tablaPedidosCocina.getValueAt(tablaPedidosCocina.getSelectedRow(), 0).toString()); //Número del pedido
                Pedido Buscar = Pedidos;
                while (Buscar != null && Buscar.cod != numPed) {
                    Buscar = Buscar.link;
                }

                listModel = new DefaultListModel();
                ListPlatosPedidosCocina.setModel(listModel);

                if (Buscar != null) {
                    PlatoPedido temp = Buscar.Platos;
                    while (temp != null) {
                        listModel.addElement(temp.nombre + " X" + temp.cant);
                        temp = temp.link;
                    }
                }

            } catch (NumberFormatException E) {
                System.out.println("Algo inesperado ha ocurrido al pasar a entero el número de la orden.");
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    };

    /**
     * Carga los pedidos en la tabla de pedidos de cocina recorriendo la lista de Pedido en Cocina.
     *
     * @param Ptr Inicio de la lista de pedidos.
     */
    void cagarPedidos(Pedido Ptr) {
        JCheckBox chek = new JCheckBox();

        //Establecce el modelo de la tabla
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int RowInndex, int columnIndex) {
                return columnIndex >= 3;
            }
        };
        model.addColumn("Numero de Orden");
        model.addColumn("Mesa");
        model.addColumn("Camarero");
        model.addColumn("V:");
        tablaPedidosCocina.setModel(model);
        tablaPedidosCocina.setFont(new Font("Calibri", 0, 16));
        tablaPedidosCocina.setSelectionMode(SINGLE_SELECTION);

        //Establece el tamaño de las filas de la tabla
        tablaPedidosCocina.getColumnModel().getColumn(0).setPreferredWidth(PedidosCocinaScroll.getWidth() / 3 - 7);
        tablaPedidosCocina.getColumnModel().getColumn(0).setResizable(false);
        tablaPedidosCocina.getColumnModel().getColumn(1).setPreferredWidth(PedidosCocinaScroll.getWidth() / 3 - 7);
        tablaPedidosCocina.getColumnModel().getColumn(1).setResizable(false);
        tablaPedidosCocina.getColumnModel().getColumn(2).setPreferredWidth(PedidosCocinaScroll.getWidth() / 3 - 7);
        tablaPedidosCocina.getColumnModel().getColumn(2).setResizable(false);
        tablaPedidosCocina.getColumnModel().getColumn(3).setPreferredWidth(21);
        tablaPedidosCocina.getColumnModel().getColumn(3).setResizable(false);

        //Añade la tabla al ScrollPanel
        PedidosCocinaScroll.getViewport().add(tablaPedidosCocina);
        tablaPedidosCocina.setVisible(true);

        Pedido Buscar = Ptr;
        while (Buscar != null && Buscar.camarero != 0) {
            model.addRow(new Object[]{Buscar.cod, Buscar.mesa, Buscar.camarero, ""});

            Buscar = Buscar.link;
        }

        //Añade los checkbox a cada fila de la tabla
        tablaPedidosCocina.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(chek));
        tablaPedidosCocina.getColumnModel().getColumn(3).setCellRenderer(new Render_CheckBox());
        tablaPedidosCocina.addMouseListener(mouse);
    }

    /**
     * Carga los ingredientes en la tabla de ingredientes del administrador de
     * la cocina recorriendo la lista de Ingrediente en Cocina.
     */
    void cargarIngredientesTablaAdmin() {
        NumIngredientes = 0;
        JCheckBox chek = new JCheckBox();

        //Es necesario usar otro modelo para evitar la aparición de elementos repetidos
        model = new DefaultTableModel() {
            //Edita el modelo para que la primera columna no sea editable
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                if (columnIndex == 0) {
                    return false;
                }
                return true;
            }
        };
        model.addColumn("Ingrediente");
        model.addColumn("Cantidad");
        model.addColumn("Bodega");
        model.addColumn("V:");
        tablaIngredientesAdmin.setModel(model);
        tablaIngredientesAdmin.setSelectionMode(SINGLE_SELECTION);
        //Formatea las columnas
        tablaIngredientesAdmin.getColumnModel().getColumn(0).setPreferredWidth(ingredientesAdminScroll.getWidth() / 3 - 7 + 40);
        tablaIngredientesAdmin.getColumnModel().getColumn(0).setResizable(false);
        tablaIngredientesAdmin.getColumnModel().getColumn(1).setPreferredWidth(ingredientesAdminScroll.getWidth() / 3 - 7 - 20);
        tablaIngredientesAdmin.getColumnModel().getColumn(1).setResizable(false);
        tablaIngredientesAdmin.getColumnModel().getColumn(2).setPreferredWidth(ingredientesAdminScroll.getWidth() / 3 - 7 - 20);
        tablaIngredientesAdmin.getColumnModel().getColumn(2).setResizable(false);
        tablaIngredientesAdmin.getColumnModel().getColumn(3).setPreferredWidth(21);
        tablaIngredientesAdmin.getColumnModel().getColumn(3).setResizable(false);

        //Añade la tabla al scrollPanel
        ingredientesAdminScroll.getViewport().add(tablaIngredientesAdmin);
        tablaIngredientesAdmin.setVisible(true);

        nuevoIngrediente = Ingredientes;
        while (nuevoIngrediente != null) {
            model.addRow(new Object[]{nuevoIngrediente.nombre, 0, String.valueOf(nuevoIngrediente.cant), ""});
            nuevoIngrediente = nuevoIngrediente.link;
        }

        //Añade el checkbox a la tabla
        tablaIngredientesAdmin.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(chek));
        tablaIngredientesAdmin.getColumnModel().getColumn(3).setCellRenderer(new Render_CheckBox());
    }

    /**
     * Checkbox que será añadido a un JTable. 
     */
    private class Render_CheckBox extends JCheckBox implements TableCellRenderer {

        //Clase para renderizar el checkbox
        private final JComponent component = new JCheckBox();

        public Render_CheckBox() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            //Cambiar el color de fonde del CheckBox
            ((JCheckBox) component).setBackground(new Color(98, 138, 183));
            //obtiene valor boolean y coloca valor en el JCheckBox
            if (value.toString().equals("true")) {
                ((JCheckBox) component).setSelected(true);
            } else if (value.toString().equals("false")) {
                ((JCheckBox) component).setSelected(false);
            }
            return ((JCheckBox) component);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        administradorCocinaFrame = new javax.swing.JFrame();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        atrasButton1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        JListPlatos = new javax.swing.JList<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        codTxt = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        imgDir = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        TypeComboBox = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        costoTxt = new javax.swing.JSpinner();
        ingredientesAdminScroll = new javax.swing.JScrollPane();
        AddPlatoButton = new javax.swing.JButton();
        AddIngredienteCallButton = new javax.swing.JButton();
        select_start = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        nameFoodTxt = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        DescripcionTxt = new javax.swing.JTextPane();
        aceptImageCheck = new javax.swing.JCheckBox();
        EndOfDayButton = new javax.swing.JButton();
        añadirIngrediente = new javax.swing.JFrame();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        atrasButton2 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        nameIngredienteText = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        cantSpinner = new javax.swing.JSpinner();
        AddIngredienteButton = new javax.swing.JButton();
        endOfDayFrame = new javax.swing.JFrame();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        atrasButton3 = new javax.swing.JButton();
        ingredientesConsumidosScroll = new javax.swing.JScrollPane();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        ingredientesPedirScroll = new javax.swing.JScrollPane();
        PlatosdePedido = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        atrasButton = new javax.swing.JButton();
        PedidosCocinaScroll = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        ListPlatosPedidosCocina = new javax.swing.JList<>();
        jLabel16 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        PedidosParaEntregarList = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        sort = new javax.swing.JButton();
        jListOrderType = new javax.swing.JComboBox<>();

        administradorCocinaFrame.setTitle("Jefe de cocina - KE");
        administradorCocinaFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("Icons/Logo7059.png")));
        administradorCocinaFrame.setMinimumSize(new java.awt.Dimension(850, 550));
        administradorCocinaFrame.setResizable(false);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setMinimumSize(new java.awt.Dimension(850, 550));
        jPanel7.setPreferredSize(new java.awt.Dimension(850, 550));
        jPanel7.setLayout(null);

        jPanel8.setBackground(new java.awt.Color(45, 108, 223));
        jPanel8.setPreferredSize(new java.awt.Dimension(850, 100));

        jLabel5.setFont(new java.awt.Font("Segoe UI Light", 0, 48)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Jefe de cocina");

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kitchenexpress/Icons/Logo7059.png"))); // NOI18N
        jLabel8.setText("Logo");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(49, 49, 49))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel5))
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE))
                .addGap(19, 19, 19))
        );

        jPanel7.add(jPanel8);
        jPanel8.setBounds(0, 0, 850, 100);

        atrasButton1.setBackground(new java.awt.Color(255, 255, 255));
        atrasButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kitchenexpress/Icons/Flecha5050.png"))); // NOI18N
        atrasButton1.setToolTipText("Atrás");
        atrasButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        atrasButton1.setMaximumSize(new java.awt.Dimension(50, 50));
        atrasButton1.setMinimumSize(new java.awt.Dimension(50, 50));
        atrasButton1.setPreferredSize(new java.awt.Dimension(50, 50));
        atrasButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atrasButton1ActionPerformed(evt);
            }
        });
        jPanel7.add(atrasButton1);
        atrasButton1.setBounds(10, 468, 50, 50);

        JListPlatos.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        JListPlatos.setToolTipText("Lista de platos añadidos");
        jScrollPane3.setViewportView(JListPlatos);

        jPanel7.add(jScrollPane3);
        jScrollPane3.setBounds(28, 163, 182, 299);

        jLabel10.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel10.setText("Lista de platos");
        jPanel7.add(jLabel10);
        jLabel10.setBounds(63, 122, 107, 23);

        jLabel15.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel15.setText("Nuevo plato");
        jPanel7.add(jLabel15);
        jLabel15.setBounds(470, 130, 93, 14);

        codTxt.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        codTxt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        codTxt.setText("Código: # 0");
        jPanel7.add(codTxt);
        codTxt.setBounds(420, 160, 187, 23);

        jLabel17.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Nombre:");
        jPanel7.add(jLabel17);
        jLabel17.setBounds(237, 204, 65, 14);

        imgDir.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        imgDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imgDirActionPerformed(evt);
            }
        });
        jPanel7.add(imgDir);
        imgDir.setBounds(576, 239, 169, 29);

        jLabel18.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Tipo:");
        jPanel7.add(jLabel18);
        jLabel18.setBounds(237, 246, 37, 14);

        TypeComboBox.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        TypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        TypeComboBox.setToolTipText("Seleccione el tipo de Plato a añadir");
        jPanel7.add(TypeComboBox);
        TypeComboBox.setBounds(312, 239, 194, 29);

        jLabel19.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Costo:");
        jPanel7.add(jLabel19);
        jLabel19.setBounds(520, 200, 48, 23);

        costoTxt.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        costoTxt.setToolTipText("Digite el costo del plato");
        costoTxt.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                costoTxtStateChanged(evt);
            }
        });
        costoTxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                costoTxtKeyPressed(evt);
            }
        });
        jPanel7.add(costoTxt);
        costoTxt.setBounds(576, 197, 238, 30);

        ingredientesAdminScroll.setToolTipText("Seleccióne los ingredientes del plato");
        jPanel7.add(ingredientesAdminScroll);
        ingredientesAdminScroll.setBounds(237, 303, 269, 159);

        AddPlatoButton.setBackground(new java.awt.Color(255, 255, 255));
        AddPlatoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kitchenexpress/Icons/Aceptar.png"))); // NOI18N
        AddPlatoButton.setToolTipText("Añadir nuevo plato");
        AddPlatoButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AddPlatoButton.setMaximumSize(new java.awt.Dimension(50, 50));
        AddPlatoButton.setMinimumSize(new java.awt.Dimension(50, 50));
        AddPlatoButton.setPreferredSize(new java.awt.Dimension(50, 50));
        AddPlatoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddPlatoButtonActionPerformed(evt);
            }
        });
        jPanel7.add(AddPlatoButton);
        AddPlatoButton.setBounds(781, 468, 50, 50);

        AddIngredienteCallButton.setBackground(new java.awt.Color(255, 255, 255));
        AddIngredienteCallButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kitchenexpress/Icons/Añadir.png"))); // NOI18N
        AddIngredienteCallButton.setToolTipText("Añadir nuevo Ingrediente");
        AddIngredienteCallButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AddIngredienteCallButton.setMaximumSize(new java.awt.Dimension(50, 50));
        AddIngredienteCallButton.setMinimumSize(new java.awt.Dimension(50, 50));
        AddIngredienteCallButton.setPreferredSize(new java.awt.Dimension(50, 50));
        AddIngredienteCallButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddIngredienteCallButtonActionPerformed(evt);
            }
        });
        jPanel7.add(AddIngredienteCallButton);
        AddIngredienteCallButton.setBounds(780, 120, 50, 50);

        select_start.setBackground(new java.awt.Color(255, 255, 255));
        select_start.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        select_start.setText("Abrir");
        select_start.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        select_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                select_startActionPerformed(evt);
            }
        });
        jPanel7.add(select_start);
        select_start.setBounds(747, 238, 67, 31);

        jLabel20.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Img:");
        jPanel7.add(jLabel20);
        jLabel20.setBounds(520, 240, 32, 30);

        nameFoodTxt.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        nameFoodTxt.setToolTipText("Digite el nombre del plato");
        nameFoodTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameFoodTxtActionPerformed(evt);
            }
        });
        jPanel7.add(nameFoodTxt);
        nameFoodTxt.setBounds(312, 197, 194, 29);

        jLabel21.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Descripción:");
        jPanel7.add(jLabel21);
        jLabel21.setBounds(625, 275, 90, 22);

        jLabel22.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Ingredientes:");
        jPanel7.add(jLabel22);
        jLabel22.setBounds(330, 275, 95, 22);

        DescripcionTxt.setToolTipText("Escriba una breve descripción del plato");
        jScrollPane1.setViewportView(DescripcionTxt);

        jPanel7.add(jScrollPane1);
        jScrollPane1.setBounds(524, 303, 290, 160);

        aceptImageCheck.setBackground(new java.awt.Color(255, 255, 255));
        aceptImageCheck.setToolTipText("Presione si desea añadir la imagén");
        jPanel7.add(aceptImageCheck);
        aceptImageCheck.setBounds(550, 240, 20, 30);

        EndOfDayButton.setBackground(new java.awt.Color(255, 255, 255));
        EndOfDayButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kitchenexpress/Icons/TurnOff.png"))); // NOI18N
        EndOfDayButton.setToolTipText("Fin del día");
        EndOfDayButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        EndOfDayButton.setMaximumSize(new java.awt.Dimension(50, 50));
        EndOfDayButton.setMinimumSize(new java.awt.Dimension(50, 50));
        EndOfDayButton.setPreferredSize(new java.awt.Dimension(50, 50));
        EndOfDayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EndOfDayButtonActionPerformed(evt);
            }
        });
        jPanel7.add(EndOfDayButton);
        EndOfDayButton.setBounds(710, 120, 50, 50);

        javax.swing.GroupLayout administradorCocinaFrameLayout = new javax.swing.GroupLayout(administradorCocinaFrame.getContentPane());
        administradorCocinaFrame.getContentPane().setLayout(administradorCocinaFrameLayout);
        administradorCocinaFrameLayout.setHorizontalGroup(
            administradorCocinaFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        administradorCocinaFrameLayout.setVerticalGroup(
            administradorCocinaFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        añadirIngrediente.setTitle("Nuevo ingrediente - KE");
        añadirIngrediente.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("Icons/Logo7059.png")));
        añadirIngrediente.setMinimumSize(new java.awt.Dimension(427, 467));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setMaximumSize(new java.awt.Dimension(449, 467));
        jPanel9.setMinimumSize(new java.awt.Dimension(427, 467));

        jPanel10.setBackground(new java.awt.Color(45, 108, 223));

        jLabel7.setFont(new java.awt.Font("Segoe UI Light", 0, 48)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Ingredientes");

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kitchenexpress/Icons/Logo7059.png"))); // NOI18N
        jLabel9.setText("Logo");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(31, 31, 31))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel7))
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE))
                .addGap(19, 19, 19))
        );

        atrasButton2.setBackground(new java.awt.Color(255, 255, 255));
        atrasButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kitchenexpress/Icons/Flecha5050.png"))); // NOI18N
        atrasButton2.setToolTipText("Atrás");
        atrasButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        atrasButton2.setMaximumSize(new java.awt.Dimension(50, 50));
        atrasButton2.setMinimumSize(new java.awt.Dimension(50, 50));
        atrasButton2.setPreferredSize(new java.awt.Dimension(50, 50));
        atrasButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atrasButton2ActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel13.setText("Añadir ingrediente");

        nameIngredienteText.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        nameIngredienteText.setToolTipText("Escriba el nombre del ingrediente");

        jLabel27.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("Nombre:");

        jLabel28.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("Número:");

        cantSpinner.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        cantSpinner.setToolTipText("Digite la cantidad de unidades disponibles");
        cantSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cantSpinnerStateChanged(evt);
            }
        });
        cantSpinner.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cantSpinnerKeyPressed(evt);
            }
        });

        AddIngredienteButton.setBackground(new java.awt.Color(255, 255, 255));
        AddIngredienteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kitchenexpress/Icons/Aceptar.png"))); // NOI18N
        AddIngredienteButton.setToolTipText("Añadir nuevo Ingrediente");
        AddIngredienteButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AddIngredienteButton.setMaximumSize(new java.awt.Dimension(50, 50));
        AddIngredienteButton.setMinimumSize(new java.awt.Dimension(50, 50));
        AddIngredienteButton.setPreferredSize(new java.awt.Dimension(50, 50));
        AddIngredienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddIngredienteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(atrasButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(AddIngredienteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(147, 147, 147)
                        .addComponent(jLabel13))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel27)
                            .addComponent(jLabel28))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nameIngredienteText)
                            .addComponent(cantSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(jLabel13)
                .addGap(47, 47, 47)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameIngredienteText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cantSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(atrasButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddIngredienteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout añadirIngredienteLayout = new javax.swing.GroupLayout(añadirIngrediente.getContentPane());
        añadirIngrediente.getContentPane().setLayout(añadirIngredienteLayout);
        añadirIngredienteLayout.setHorizontalGroup(
            añadirIngredienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        añadirIngredienteLayout.setVerticalGroup(
            añadirIngredienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        endOfDayFrame.setMinimumSize(new java.awt.Dimension(850, 550));
        endOfDayFrame.setResizable(false);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setMaximumSize(new java.awt.Dimension(850, 550));
        jPanel11.setMinimumSize(new java.awt.Dimension(850, 550));
        jPanel11.setLayout(null);

        jPanel12.setBackground(new java.awt.Color(45, 108, 223));
        jPanel12.setMaximumSize(new java.awt.Dimension(850, 100));
        jPanel12.setMinimumSize(new java.awt.Dimension(850, 100));

        jLabel11.setFont(new java.awt.Font("Segoe UI Light", 0, 48)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Fin del día");

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kitchenexpress/Icons/Logo7059.png"))); // NOI18N
        jLabel12.setText("Logo");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 514, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addGap(31, 31, 31))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(0, 6, Short.MAX_VALUE)
                        .addComponent(jLabel11))
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(19, 19, 19))
        );

        jPanel11.add(jPanel12);
        jPanel12.setBounds(0, 0, 850, 100);

        atrasButton3.setBackground(new java.awt.Color(255, 255, 255));
        atrasButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kitchenexpress/Icons/Flecha5050.png"))); // NOI18N
        atrasButton3.setToolTipText("Atrás");
        atrasButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        atrasButton3.setMaximumSize(new java.awt.Dimension(50, 50));
        atrasButton3.setMinimumSize(new java.awt.Dimension(50, 50));
        atrasButton3.setPreferredSize(new java.awt.Dimension(50, 50));
        atrasButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atrasButton3ActionPerformed(evt);
            }
        });
        jPanel11.add(atrasButton3);
        atrasButton3.setBounds(10, 458, 50, 50);

        ingredientesConsumidosScroll.setToolTipText("Seleccióne los ingredientes del plato");
        jPanel11.add(ingredientesConsumidosScroll);
        ingredientesConsumidosScroll.setBounds(130, 150, 310, 300);

        jLabel24.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Alimentos para pedir");
        jPanel11.add(jLabel24);
        jLabel24.setBounds(510, 120, 170, 20);

        jLabel25.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("Alimentos consumidos");
        jPanel11.add(jLabel25);
        jLabel25.setBounds(190, 120, 190, 20);
        jPanel11.add(ingredientesPedirScroll);
        ingredientesPedirScroll.setBounds(480, 150, 240, 300);

        javax.swing.GroupLayout endOfDayFrameLayout = new javax.swing.GroupLayout(endOfDayFrame.getContentPane());
        endOfDayFrame.getContentPane().setLayout(endOfDayFrameLayout);
        endOfDayFrameLayout.setHorizontalGroup(
            endOfDayFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        endOfDayFrameLayout.setVerticalGroup(
            endOfDayFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cocina - KE");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("Icons/Logo7059.png")));
        setMinimumSize(new java.awt.Dimension(850, 550));
        setResizable(false);

        PlatosdePedido.setBackground(new java.awt.Color(255, 255, 255));
        PlatosdePedido.setPreferredSize(new java.awt.Dimension(850, 550));

        jPanel5.setBackground(new java.awt.Color(45, 108, 223));

        jLabel3.setFont(new java.awt.Font("Segoe UI Light", 0, 48)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Cocina");

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kitchenexpress/Icons/Logo7059.png"))); // NOI18N
        jLabel6.setText("Logo");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 576, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(42, 42, 42))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(0, 10, Short.MAX_VALUE)
                        .addComponent(jLabel3))
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(19, 19, 19))
        );

        okButton.setBackground(new java.awt.Color(255, 255, 255));
        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kitchenexpress/Icons/Aceptar.png"))); // NOI18N
        okButton.setToolTipText("Realizado");
        okButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        okButton.setMaximumSize(new java.awt.Dimension(50, 50));
        okButton.setMinimumSize(new java.awt.Dimension(50, 50));
        okButton.setPreferredSize(new java.awt.Dimension(50, 50));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        atrasButton.setBackground(new java.awt.Color(255, 255, 255));
        atrasButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kitchenexpress/Icons/Flecha5050.png"))); // NOI18N
        atrasButton.setToolTipText("Atrás");
        atrasButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        atrasButton.setMaximumSize(new java.awt.Dimension(50, 50));
        atrasButton.setMinimumSize(new java.awt.Dimension(50, 50));
        atrasButton.setPreferredSize(new java.awt.Dimension(50, 50));
        atrasButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atrasButtonActionPerformed(evt);
            }
        });

        PedidosCocinaScroll.setBackground(new java.awt.Color(255, 255, 255));
        PedidosCocinaScroll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PedidosCocinaScrollMouseClicked(evt);
            }
        });

        ListPlatosPedidosCocina.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        ListPlatosPedidosCocina.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(ListPlatosPedidosCocina);

        jLabel16.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Platos de pedidos");

        jLabel23.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("Pedidos");

        PedidosParaEntregarList.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        PedidosParaEntregarList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(PedidosParaEntregarList);

        jLabel1.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        jLabel1.setText("Pedidos para entregar");

        sort.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        sort.setText("Ordenar");
        sort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortActionPerformed(evt);
            }
        });

        jListOrderType.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jListOrderType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pedido", "Mesa" }));

        javax.swing.GroupLayout PlatosdePedidoLayout = new javax.swing.GroupLayout(PlatosdePedido);
        PlatosdePedido.setLayout(PlatosdePedidoLayout);
        PlatosdePedidoLayout.setHorizontalGroup(
            PlatosdePedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PlatosdePedidoLayout.createSequentialGroup()
                .addGroup(PlatosdePedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PlatosdePedidoLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(atrasButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(730, 730, 730)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(PlatosdePedidoLayout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(PlatosdePedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PlatosdePedidoLayout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PlatosdePedidoLayout.createSequentialGroup()
                        .addGroup(PlatosdePedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(PlatosdePedidoLayout.createSequentialGroup()
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jListOrderType, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(sort))
                            .addComponent(PedidosCocinaScroll))
                        .addGap(18, 18, 18))
                    .addGroup(PlatosdePedidoLayout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(PlatosdePedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(57, 57, 57))
        );
        PlatosdePedidoLayout.setVerticalGroup(
            PlatosdePedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PlatosdePedidoLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(PlatosdePedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PlatosdePedidoLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(1, 1, 1)
                        .addComponent(jScrollPane4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PlatosdePedidoLayout.createSequentialGroup()
                        .addGap(0, 6, Short.MAX_VALUE)
                        .addGroup(PlatosdePedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sort)
                            .addComponent(jListOrderType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PedidosCocinaScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(11, 11, 11)
                .addGroup(PlatosdePedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(atrasButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(okButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PlatosdePedido, 854, 854, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PlatosdePedido, 553, 553, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Regresa a la instancia de Principal que se cargó al inicio del programa en Inicio.
     * @param evt 
     */
    private void atrasButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atrasButtonActionPerformed
        this.setVisible(false);
        Inicio.ventana1.setVisible(true);
    }//GEN-LAST:event_atrasButtonActionPerformed
    
    /**
     * Carga el frame dispuesto para añadir ingredientes.
     * @param evt 
     */
    private void AddIngredienteCallButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddIngredienteCallButtonActionPerformed
        administradorCocinaFrame.setVisible(false);
        añadirIngrediente.setVisible(true);
    }//GEN-LAST:event_AddIngredienteCallButtonActionPerformed

    /**
     * Hace las validaciones necesarias para añadir un plato a la lista de Plato en Cocina.
     * @param evt 
     */
    private void AddPlatoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddPlatoButtonActionPerformed
        //Verifica que no se halla ingresado letras een los campos numéricos
        int i = 0, k;
        Boolean pocoIngrediente = false; //Indica si los ingredeintes de un plato tienen cantidad igual a 0
        boolean error = false; //Indica si se econtraron errores de formato deltro de la tabla
        while (i < tablaIngredientesAdmin.getRowCount() && error == false && pocoIngrediente == false) {
            model = (DefaultTableModel) tablaIngredientesAdmin.getModel();
            //Se valida el valor de los checkboxes
            if (String.valueOf(model.getValueAt(i, 3)).equals("true")) {
                //Se comprueba que el número de ingredientes sea numérico y mayor que 0
                try {
                    k = Integer.parseInt(String.valueOf(model.getValueAt(i, 1)));
                    if (k <= 0) {
                        pocoIngrediente = true;
                    }
                } catch (NumberFormatException ex) {
                    System.out.println(ex);
                    error = true;
                }
            }
            i++;
        }

        if (pocoIngrediente == false) {
            if (error == false) { // Si no hubo errores
                if (nameFoodTxt.getText().length() != 0 && (int) costoTxt.getValue() > 0) {
                    NumIngredientes = 0;
                    //Lista de ingredientes para el plato
                    Ingrediente listIngredientesForPlato = new Ingrediente();
                    //Toma el modelo de la tabla de ingredientes para el plato
                    model = (DefaultTableModel) tablaIngredientesAdmin.getModel();

                    //Llena una lista con los ingredientes seleccionados                
                    for (i = 0; i < tablaIngredientesAdmin.getRowCount(); i++) {
                        model = (DefaultTableModel) tablaIngredientesAdmin.getModel();
                        if (String.valueOf(model.getValueAt(i, 3)).equals("true")) {
                            nuevoIngrediente = new Ingrediente();
                            nuevoIngrediente.nombre = model.getValueAt(i, 0).toString();

                            //Intentará extraer la cantidad de ingredientes de Ingrediente para este plato
                            try {
                                nuevoIngrediente.cant = Integer.parseInt(model.getValueAt(i, 1).toString());
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(administradorCocinaFrame, "Algo inesperado ha ocurido", "KE", JOptionPane.ERROR_MESSAGE);
                            }

                            nuevoIngrediente.link = null;
                            listIngredientesForPlato = listIngredientesForPlato.addIngredientes(administradorCocinaFrame, nuevoIngrediente, false, false);
                            NumIngredientes++;
                            model.setValueAt("false", i, 3);
                        }
                        model.setValueAt("0", i, 1);
                    }

                    if (NumIngredientes > 0) {
                        //Verifica si se añadirá una imagen
                        if (aceptImageCheck.isSelected() && imgDir.getText().length() > 0 || aceptImageCheck.isSelected() == false) {

                            nuevoPlato = new Plato();
                            if (aceptImageCheck.isSelected()) {
                                nuevoPlato.imagen = imageChooser.getSelectedFile();
                            } else {
                                nuevoPlato.imagen = null;
                            }

                            nuevoPlato.nombre = nameFoodTxt.getText();
                            nuevoPlato.cod = codPlato;
                            nuevoPlato.foodType = TypeComboBox.getSelectedItem().toString();
                            nuevoPlato.value = (int) costoTxt.getValue();
                            nuevoPlato.foodIngredientes = null;
                            nuevoPlato.details = DescripcionTxt.getText();
                            nuevoPlato.link = null;

                            if (Platos == null) {
                                Platos = new Plato();
                            }

                            //Añade los platos a la lista de platos
                            Platos = Platos.addPlato(administradorCocinaFrame, listIngredientesForPlato, nuevoPlato);

                            //Carga la lista de platos en el JListPlatos
                            Platos.toJList(JListPlatos);

                            //Carga la lista de platos en el menú
                            Platos.toJList(Inicio.ventanaMenú.JListMenú);

                            //Carga la lista de platos en Ordenes
                            Platos.toJList(Inicio.ventanaMenú.JListMenúOrdenesPlatos);

                            JOptionPane.showMessageDialog(administradorCocinaFrame, "¡Plato añadido con exito!", "KE", JOptionPane.INFORMATION_MESSAGE);

                            codPlato++;
                            //Actualiza el código del plato en el frame
                            codTxt.setText("Código: # " + codPlato);

                            //Guarda los platos en su archivo
                            Platos.toFile(administradorCocinaFrame, Inicio.PlatosFile);

                            //Borrar los datos del formulario
                            nameFoodTxt.setText(null);
                            costoTxt.setValue(0);
                            TypeComboBox.setSelectedItem(2);
                            DescripcionTxt.setText(null);
                            imgDir.setText(null);
                            aceptImageCheck.setSelected(false);
                        } else {
                            JOptionPane.showMessageDialog(administradorCocinaFrame, "¡El campo de imagén no debe estar vacío!", "KE", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(administradorCocinaFrame, "¡No no puede haber menos de 1 ingrediente para el plato!", "KE", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(administradorCocinaFrame, "No puenen haber campos vacíos ni con 0 como valor", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(administradorCocinaFrame, "La tabla no puede contener carácteres no numéricos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(administradorCocinaFrame, "No pueden haber ingredientes con cantidades menores o iguales a 0", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_AddPlatoButtonActionPerformed

    private void costoTxtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_costoTxtKeyPressed

    }//GEN-LAST:event_costoTxtKeyPressed

    private void costoTxtStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_costoTxtStateChanged
        if ((int) costoTxt.getValue() < 0) {
            costoTxt.setValue((int) costoTxt.getValue() * (-1));
        }
    }//GEN-LAST:event_costoTxtStateChanged

    private void atrasButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atrasButton1ActionPerformed
        administradorCocinaFrame.setVisible(false);
        Inicio.ventana1.setVisible(true);
    }//GEN-LAST:event_atrasButton1ActionPerformed

    private void atrasButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atrasButton2ActionPerformed
        añadirIngrediente.dispose();
        administradorCocinaFrame.setVisible(true);
    }//GEN-LAST:event_atrasButton2ActionPerformed

    private void cantSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cantSpinnerStateChanged
        if ((int) cantSpinner.getValue() < 0) {
            cantSpinner.setValue((int) cantSpinner.getValue() * (-1));
        }
    }//GEN-LAST:event_cantSpinnerStateChanged

    private void cantSpinnerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cantSpinnerKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cantSpinnerKeyPressed

    /**
     *
     * @param evt
     */
    private void AddIngredienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddIngredienteButtonActionPerformed
        //Añade nuevos ingredientes a lalista de ingredientes
        if (nameIngredienteText.getText().length() > 0) {
            if ((int) cantSpinner.getValue() > 0) {
                nuevoIngrediente = new Ingrediente();
                nuevoIngrediente.nombre = nameIngredienteText.getText();
                nuevoIngrediente.cant = (int) cantSpinner.getValue();
                nuevoIngrediente.link = null;
                //Añade los ingredientes a la lista de ingredientes
                if (Ingredientes == null) {
                    Ingredientes = new Ingrediente();
                }

                Ingredientes = Ingredientes.addIngredientes(añadirIngrediente, nuevoIngrediente, true, true);
                nameIngredienteText.setText(null);
                cantSpinner.setValue(0);

                //Guarda los ingredientes en su archivo
                Ingredientes.toFile(añadirIngrediente, Inicio.IngredientesFile);
                cargarIngredientesTablaAdmin();
                JOptionPane.showMessageDialog(administradorCocinaFrame, "!Ingrediente añadido con exito¡", "KE", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(administradorCocinaFrame, "Deben haber más de 0 unidades", "KE", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(administradorCocinaFrame, "No el campo nombre no puede estar vacío", "KE", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_AddIngredienteButtonActionPerformed

    private void select_startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_select_startActionPerformed
        int option = imageChooser.showOpenDialog(administradorCocinaFrame);
        if (option == JFileChooser.APPROVE_OPTION) {
            imgDir.setText(imageChooser.getSelectedFile().getName());
        } else {
            imgDir.setText("");
        }
    }//GEN-LAST:event_select_startActionPerformed

    /**
     *
     * @param evt
     */
    private void imgDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imgDirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_imgDirActionPerformed

    /**
     *
     * @param evt
     */
    private void nameFoodTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameFoodTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameFoodTxtActionPerformed

    /**
     * Enviar pedido terminado pasandolo de la lista Pedidos a la lisa PedidosParaEntrega.
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        boolean listos = false; //Evalúa si hubo pedidos listos
        model = (DefaultTableModel) tablaPedidosCocina.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            if (String.valueOf(model.getValueAt(i, 3)).equals("true")) {

                Pedido Buscar = Pedidos, ant = null;
                //Busca los pedidos para eliminarlos de la lista de pedidos
                while (Buscar != null && String.valueOf(Buscar.cod).equals(String.valueOf(model.getValueAt(i, 0))) == false) {
                    ant = Buscar;
                    Buscar = Buscar.link;
                }
                double time = Integer.parseInt(Pedido.calculateTime(Buscar.tiempo, Pedido.getHour())) / 60;

                if (time > 5 || Integer.parseInt(Buscar.tiempo) > Integer.parseInt(Pedido.getHour())) {
                    listos = true;

                    if (PedidosParaEntrega == null) {
                        PedidosParaEntrega = new Pedido();
                    }

                    Pedido nuevoPedido = new Pedido();
                    nuevoPedido.tiempo = Buscar.tiempo;
                    nuevoPedido.Platos = Buscar.Platos;
                    nuevoPedido.camarero = Buscar.camarero;
                    nuevoPedido.cod = Buscar.cod;
                    nuevoPedido.mesa = Buscar.mesa;
                    nuevoPedido.valortotal = Buscar.valortotal;
                    //Añade un Pedido a la lista de PedidosParaEntrega
                    PedidosParaEntrega = PedidosParaEntrega.addPedido(this, nuevoPedido);

                    //Elimina el pedido de la lista de pedidos
                    if (ant == null) {
                        //Cambia el inicio de la lista a el elemento siguiente
                        Pedidos = Pedidos.link;
                    } else {
                        ant.link = Buscar.link;
                        Buscar.link = null;
                    }
                    model.setValueAt("false", i, 3);

                    //Se terminó un pedido
                    Inicio.ventana1.actualizarTablaPedidosPorEntregar(PedidosParaEntrega);
                    Inicio.ventana1.mostrarNotificacion();

                    if (Pedidos == null) {
                        Pedidos = new Pedido();
                    }
                    Pedidos.toJListByMesa(Inicio.ventanaMenú.JListPedidosGenerados, 5 * (Inicio.ventana1.camarero - 1) + 1 + Inicio.ventanaMenú.mesaBox.getSelectedIndex());
                } else {
                    JOptionPane.showMessageDialog(administradorCocinaFrame, "No puede estar listo, aún el cliente puede cambiar de opinión.", "KE", JOptionPane.INFORMATION_MESSAGE);
                    model.setValueAt("false", i, 3);
                }
            }
        }
        //Cargar pedidos
        if (listos) {
            cargarPedidos();
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void PedidosCocinaScrollMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PedidosCocinaScrollMouseClicked

    }//GEN-LAST:event_PedidosCocinaScrollMouseClicked

    /**
     * Ordena los pedidos de acuerdo con 
     */
    private void sortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortActionPerformed
        if (Pedidos.camarero != 0) {
            if (jListOrderType.getSelectedIndex() == 0) {
                Pedidos.sortByOrden();
            } else {
                Pedidos.sortByMesa();
            }
            cargarPedidos();
        }
    }//GEN-LAST:event_sortActionPerformed

    /**
     * Este evento calculará los alimentos consumidos. Además el mismo se
     * encargará de calcular los alimentos que se necesita pedir.
     *
     * @param evt
     */
    private void EndOfDayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EndOfDayButtonActionPerformed
        Ingrediente AlimentosConsumidos = new Ingrediente(), AlimentosPorPedir = new Ingrediente();//Es la lista que contendrá los alimentos consumidos
        Factura BuscarFactura = Inicio.ventanaCocina.Facturas;
        //Se recorre la lista de facturas 
        while (BuscarFactura != null) {
            PlatoPedido BuscarPlatoPedido = BuscarFactura.platos;
            //Se recorre la lista de platoPedido de la Factura en cuestion
            while (BuscarPlatoPedido != null) {
                Plato BuscarPlato = Inicio.ventanaCocina.Platos;
                //Se Busca el platoPedido en la lista de platos de Cocina
                while (BuscarPlato != null && BuscarPlato.cod != BuscarPlatoPedido.cod) {
                    BuscarPlato = BuscarPlato.link;
                }
                Ingrediente BuscarIngredientesPlato = BuscarPlato.foodIngredientes;
                //Se recorre la lista de ingredientes del plato
                while (BuscarIngredientesPlato != null) {
                    //Se crea un nuevo ingrediente con el nombre antiguo pero con las cantidades gastadas en ese plato
                    nuevoIngrediente = new Ingrediente();
                    nuevoIngrediente.nombre = BuscarIngredientesPlato.nombre;
                    nuevoIngrediente.cant = BuscarIngredientesPlato.cant * BuscarPlatoPedido.cant;
                    //Se añade el nuevoIngrediente a la lista de AlimentosConsumidos
                    AlimentosConsumidos = AlimentosConsumidos.addIngredientes(administradorCocinaFrame, nuevoIngrediente, true, false);

                    BuscarIngredientesPlato = BuscarIngredientesPlato.link;
                }
                BuscarPlatoPedido = BuscarPlatoPedido.link;
            }
            BuscarFactura = BuscarFactura.link;
        }

        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int RowInndex, int columnIndex) {
                return false;
            }
        };
        model.addColumn("Ingrediente");
        model.addColumn("Consumido");
        model.addColumn("En bodega");
        //Se crea la tabla que contendrá los ingredientes consumidos
        JTable tablaIngredientesConsumidos = new JTable(model);
        tablaIngredientesConsumidos.setVisible(true);

        tablaIngredientesConsumidos.getColumnModel().getColumn(0).setResizable(false);
        tablaIngredientesConsumidos.getColumnModel().getColumn(1).setResizable(false);
        tablaIngredientesConsumidos.getColumnModel().getColumn(2).setResizable(false);

        ingredientesConsumidosScroll.getViewport().add(tablaIngredientesConsumidos);

        //Se añaden los alimentos consumidos a la tabla
        Ingrediente BuscarIngCon = AlimentosConsumidos;
        while (BuscarIngCon != null) {
            Ingrediente BuscarIngrediente = Inicio.ventanaCocina.Ingredientes;
            while (BuscarIngrediente != null && BuscarIngrediente.nombre.equals(BuscarIngCon.nombre) == false) {
                BuscarIngrediente = BuscarIngrediente.link;
            }
            if (BuscarIngrediente != null) {
                model.addRow(new Object[]{BuscarIngCon.nombre, BuscarIngCon.cant, BuscarIngrediente.cant});
            }
            BuscarIngCon = BuscarIngCon.link;
        }

        //Analizarán la lista de Platos de la cocina para comprobar que hayan suficientes ingredientes por plato
        Plato BuscarPlato = Inicio.ventanaCocina.Platos;
        while (BuscarPlato != null) {
            Ingrediente BuscarIngredientesPlato = BuscarPlato.foodIngredientes;

            //Se recorre la lista de ingredientes del plato
            while (BuscarIngredientesPlato != null) {
                Ingrediente BuscarIngredienteBodega = Inicio.ventanaCocina.Ingredientes;

                while (BuscarIngredienteBodega != null && BuscarIngredienteBodega.nombre.equals(BuscarIngredientesPlato.nombre) == false) {
                    BuscarIngredienteBodega = BuscarIngredienteBodega.link;
                }

                nuevoIngrediente = new Ingrediente();
                nuevoIngrediente.nombre = BuscarIngredientesPlato.nombre;

                if (BuscarIngredienteBodega != null) {
                    //Añade el ingrediente si la cantidad no es suficiente como para hacer una unidad de un plato
                    if (BuscarIngredienteBodega.cant < BuscarIngredientesPlato.cant) {
                        nuevoIngrediente.cant = BuscarIngredienteBodega.cant;
                        AlimentosPorPedir = AlimentosPorPedir.addIngredientes(administradorCocinaFrame, nuevoIngrediente, false, false);
                    }
                } else {
                    //Añade el ingrediente si no lo encuentra en bodega
                    nuevoIngrediente.cant = 0;
                    AlimentosPorPedir = AlimentosPorPedir.addIngredientes(administradorCocinaFrame, nuevoIngrediente, false, false);
                }
                BuscarIngredientesPlato = BuscarIngredientesPlato.link;
            }
            BuscarPlato = BuscarPlato.link;
        }

        Ingrediente BuscarIngredienteBodega = Inicio.ventanaCocina.Ingredientes;
        //Se añadiran los Ingredientes que tiene 0 en bodega
        while (BuscarIngredienteBodega != null) {
            if (BuscarIngredienteBodega.cant == 0) {
                nuevoIngrediente = new Ingrediente();
                nuevoIngrediente.nombre = BuscarIngredienteBodega.nombre;
                nuevoIngrediente.cant = 0;
                AlimentosPorPedir = AlimentosPorPedir.addIngredientes(administradorCocinaFrame, nuevoIngrediente, false, false);
            }
            BuscarIngredienteBodega = BuscarIngredienteBodega.link;
        }

        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int RowInndex, int columnIndex) {
                return false;
            }
        };

        model.addColumn("Ingrediente");
        model.addColumn("En bodega");
        //Se crea la tabla que contendrá los ingredientes a pedir
        JTable tablaIngredientesPedir = new JTable(model);
        tablaIngredientesPedir.setVisible(true);

        tablaIngredientesPedir.getColumnModel().getColumn(0).setResizable(false);
        tablaIngredientesPedir.getColumnModel().getColumn(1).setResizable(false);

        ingredientesPedirScroll.getViewport().add(tablaIngredientesPedir);

        //Se añaden los alimentos por pedir a la tabla
        BuscarIngCon = AlimentosPorPedir;
        while (BuscarIngCon != null) {
            model.addRow(new Object[]{BuscarIngCon.nombre, BuscarIngCon.cant});
            BuscarIngCon = BuscarIngCon.link;
        }
        administradorCocinaFrame.setVisible(false);
        endOfDayFrame.setVisible(true);

    }//GEN-LAST:event_EndOfDayButtonActionPerformed

    private void atrasButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atrasButton3ActionPerformed
        endOfDayFrame.dispose();
        administradorCocinaFrame.setVisible(true);
    }//GEN-LAST:event_atrasButton3ActionPerformed

    /**
     * Carga los pedidos en su lista y su archivo.
     */
    public void cargarPedidos() {
        //Cargar pedidos
        cagarPedidos(Pedidos);
        if (Pedidos == null) {
            Pedidos = new Pedido();
        }
        Pedidos.toFile(this, Inicio.PedidosFile);

        ListPlatosPedidosCocina.setModel(new DefaultListModel());

        if (PedidosParaEntrega.camarero != 0) {
            PedidosParaEntrega.toJListByCod(PedidosParaEntregarList);
        }
        PedidosParaEntrega.toFile(this, Inicio.PedidosParaEntregar);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            /*
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
             */

        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Cocina.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cocina.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cocina.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cocina.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Cocina().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddIngredienteButton;
    private javax.swing.JButton AddIngredienteCallButton;
    private javax.swing.JButton AddPlatoButton;
    private javax.swing.JTextPane DescripcionTxt;
    private javax.swing.JButton EndOfDayButton;
    private javax.swing.JList<String> JListPlatos;
    private javax.swing.JList<String> ListPlatosPedidosCocina;
    private javax.swing.JScrollPane PedidosCocinaScroll;
    public javax.swing.JList<String> PedidosParaEntregarList;
    private javax.swing.JPanel PlatosdePedido;
    private javax.swing.JComboBox<String> TypeComboBox;
    private javax.swing.JCheckBox aceptImageCheck;
    public javax.swing.JFrame administradorCocinaFrame;
    private javax.swing.JButton atrasButton;
    private javax.swing.JButton atrasButton1;
    private javax.swing.JButton atrasButton2;
    private javax.swing.JButton atrasButton3;
    private javax.swing.JFrame añadirIngrediente;
    private javax.swing.JSpinner cantSpinner;
    private javax.swing.JLabel codTxt;
    private javax.swing.JSpinner costoTxt;
    private javax.swing.JFrame endOfDayFrame;
    private javax.swing.JTextField imgDir;
    private javax.swing.JScrollPane ingredientesAdminScroll;
    private javax.swing.JScrollPane ingredientesConsumidosScroll;
    private javax.swing.JScrollPane ingredientesPedirScroll;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    public javax.swing.JComboBox<String> jListOrderType;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField nameFoodTxt;
    private javax.swing.JTextField nameIngredienteText;
    private javax.swing.JButton okButton;
    private javax.swing.JButton select_start;
    private javax.swing.JButton sort;
    // End of variables declaration//GEN-END:variables
}
