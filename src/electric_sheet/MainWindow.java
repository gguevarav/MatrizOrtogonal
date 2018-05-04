/*
 * MainWindow.java
 *
 * Created on 23 de febrero de 2006, 07:42 PM
 */

/*TODO
 * HECHO Meter el Workplace en Jpanel1
 *   HECHO Hacer que sea visible la tabla
 *      -[Debug:]
 *          -[Hecho] Casos de insercion
 *      
 *      
 * -[hecho] FIXME: Arreglar metodo IrA(x,y) cuando es llamado por Insertar
 * -[hecho] revisar la manera en que se manejan las celdas, ya que la tabla usara celdas como indices
 * -[hecho] implementar metodo que retorne un subconjunto de la matriz en forma de lista
 * - [HECHO] Implementar funciones aplicando las listas de datos
 *     -[hecho] Sumatoria
 *     -[hecho] Multiplicatoria
 *     -[hecho] Media
 * -[] Implementar analizador sintactico para
 *   -[] expresiones dentro de alguna celda
 *     -[hecho] COMPONER LA GRAMATICA PARA QUE NO GENERE ERRORES AL OPERAR Y METER CADENAS
 *     -[hecho] Evaluar asignaciones
 *     -[hecho] Evaluar funciones
 *          -[hecho] Suma con rangos
 *          -[hecho] Suma con lista de numeros
 *          -[hecho] Mult con rangos
 *          -[hecho] Mult con lista de numeros
 *          -[hecho] Prom con rangos
 *          -[hecho] Prom con lista de numeros
 *          -[bug] Resolver conflictos de actualizacion
 *     -[hecho] Crear funcion para recuperacoin de errores
 *     -[Hecho] Funciones multihojas( SUMA(Sheet1,...) cuando se esta otra hoja que no es 'sheet1')
 *    -[hecho]: Ingreso de cadenas con espacios (definidas como Token CADENA)
 *   -[Hecho] expresiones desde archivo de entrada
 *      -[Hecho] Analizador Sintactico
 *      -[Hecho] Acciones semanticas de la clase Kreator 
 *          -[Hecho Metodo estatico para creacion e insercion de hojas y datos que pueda ser llamado desde
 *           el parser cada vez que se reduzca
 * -[hecho] Hacer funcion que genere un nuevo Workplace
 * -[hecho] Implementar la interfaz para el editor de funciones
 * -[hecho] Implementar cuadro de dialogo para insertar tablas
 * [deprecated] Implementar un TableColumnModel apropiado
 * - USAR XMLEncoder para guardar tabla
 * - Usar JAva Web Start para el deploying
 */
package electric_sheet;
import java.io.*;
import java.util.*;
import javax.swing.*;
import lex.*;
/**
 * Clase principal que contiene la ventana principal de la aplicacion, contiene metodos para leer y escribir
 * archivos, pasando los streams a otros objetos para que lo analizen.
 * @author  Erik Giron
 */
public class MainWindow extends javax.swing.JFrame {
    
    Workplace wp = new Workplace(); // crea nuevo espacio de trabajo
    Kreator kr; // Creador de datos
    /*Para el parsing del archivo de entrada*/
    private FileReader fr = null;
    private parser parser = null;
    private Lexer lexer = null;
    private java_cup.runtime.Symbol parse_tree;
    private boolean debugear = false;
    
    //private StringWriter sw;
    private FileWriter sw;
    
    
    private int numhojas = 1;
    /** Creates new form MainWindow */
    public MainWindow() {
        initComponents(); 
        inicializar();
        
    }
    /**Crea e Inicializa los componentes*/
    protected void inicializar(){
        kr = new Kreator(wp);
        jPanel1.add(wp,0); // inserta el workspace en el panel principal
        wp.insertarHoja("Hoja 1");
        wp.insertarHoja("Hoja 2");
        wp.insertarHoja("Hoja 3");
    }
    /**Genera un nuevo Workplace con hojas vacias, es llamado por el menu nuevo*/
    protected void nuevo(){
        wp = new Workplace();
        System.runFinalization();
        inicializar();
    }
/**Metodo para crear nueva hoja en el espacio de trabajo
 * @deprecated
 * @return 1 si se creo satisfactoriamente
  */    
    public boolean nuevaHoja() {
        boolean ret;
        if(ret = nuevaHoja("Hoja" + numhojas));
            numhojas++;
        return ret;
    }

/**Metodo para crear nueva hoja en el espacio de trabajo
 * @deprecated
 * @return 1 si se creo satisfactoriamente
  */        
    public boolean nuevaHoja(String nombre) {
        if(wp.insertarHoja(nombre)){
           numhojas ++; 
           return true;
        }
        return false;
    }
    /**Metodo para eliminar hojas en el espacio de trabajo
 * @return 1 si se creo satisfactoriamente*/
    public boolean borrarHoja(String nombre){
        return (wp.eliminarHoja(nombre));
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    /**Abre cuadro de dialogo de abrir archivo*/
    private void fileChooser(){
                JFileChooser chooser = new JFileChooser();
    // Note: source for ExampleFileFilter can be found in FileChooserDemo,
    // under the demo/jfc directory in the JDK.
    //ExampleFileFilter filter = new ExampleFileFilter();
//    filter.addExtension("jpg");
//    filter.addExtension("gif");
//    filter.setDescription("JPG & GIF Images");
  //  chooser.setFileFilter(new javax.swing.plaf.basic.BasicFileChooserUI.AcceptAllFileFilter());
        int returnVal = chooser.showOpenDialog(this.getContentPane());
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " +
                chooser.getSelectedFile().getName());
            try{
                this.leerArchivo(chooser.getSelectedFile());

            }
            catch (Exception e){
                System.out.println("Imposible abrir archivo " + e);
            }
        }
    }
    /**Abre cuadro de dialogo de salvar archivo*/
     private void saveFileChooser(){
     JFileChooser chooser = new JFileChooser();
    // Note: source for ExampleFileFilter can be found in FileChooserDemo,
    // under the demo/jfc directory in the JDK.
    //ExampleFileFilter filter = new ExampleFileFilter();
//    filter.addExtension("jpg");
//    filter.addExtension("gif");
//    filter.setDescription("JPG & GIF Images");
  //  chooser.setFileFilter(new javax.swing.plaf.basic.BasicFileChooserUI.AcceptAllFileFilter());
        int returnVal = chooser.showSaveDialog(this.getContentPane());
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " +
                chooser.getSelectedFile().getName());
            try{
                this.generarReporte(chooser.getSelectedFile().getAbsolutePath());
            }
            catch (Exception e){
                System.out.println("Imposible abrir archivo " + e);
            }
        }
    }

    
        public void setFileName(String filename) throws FileNotFoundException{
        try{
        fr = new FileReader(filename);
        }
        catch(FileNotFoundException e){
            System.out.println(e);
            throw (e);
        }
        catch(Exception e){
            System.out.println(e);
            throw (FileNotFoundException) e;
        }
    }
    
    /**Analiza el texto*/
    private int parse(){
        boolean huboError = false;
        lexer = new Lexer(fr);
        parser = new parser(lexer);
        parser.setCreador(kr); // asigna clase creadora
        try {
          if(debugear)
            parse_tree = parser.debug_parse();
          else
            parse_tree = parser.parse();
        } catch (Exception e) {
        /* do cleanup here - - possibly rethrow e */
          System.out.println("Algo pasa " + e.toString());
          huboError = true;
        } finally {
	/* do close out here */
        }
        if(!huboError){
            return 0;
        
        }
        else{ /**Si hubo error de parse*/
            return -1;
        }
        
    }
    /**Lee y parsea el File dado*/
    protected int leerArchivo(File f){
        try{
            fr = new FileReader(f);
            return parse();
        } catch(Exception e){
            System.out.println("Error: " + e);
        }
        return -1;
    }
    /**Guarda y sobreescribe reporte de los documentos abiertos*/
    protected void generarReporte(String fileNameOut){
        LinkedList datos;
        LinkedList listaHojas;
        Sheet tmpSheet;
        tda.Dato tmpDato;
        int numRows = 0;
        int numCols = 0;
        try{
            sw = new FileWriter(fileNameOut,true);
            //obtener lista de hojas desde el workplace
            listaHojas = wp.getListaHojas();

            //escribir encabezado de HTML a stream
            sw.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
            sw.write("<html>\n<head>\n<meta content=\"text/html; charset=ISO-8859-1\" http-equiv=\"content-type\">\n");
            sw.write("<title>ElectricSheet Report</title>\n");
            sw.write("</head>\n<body>\n");
            sw.write("<big><span style=\"font-weight: bold;\">Hoja generada por ElectricSheet</span></big><br>\n");
            sw.write("<small>(C) 2006 Erik Giron (c# 200313492)</small><br>\n<br>\n");
            // Iterar sobre cada hoja en listaSheets
            for (int i = 0; i < listaHojas.size();i++){
                // obtener maximo numero de datos
                tmpSheet = (Sheet) listaHojas.get(i);

                numRows = tmpSheet.getHoja().getRowCount();
                numCols = tmpSheet.getHoja().getColumnCount();
                sw.write("<table style=\"text-align: left; width: 100%;\" border=\"1\" cellpadding=\"2\" cellspacing=\"2\">\n");
                sw.write("<tbody>\n");
                sw.write("    <tr> <td colspan=\"4\" rowspan=\"1\"><big><span style=\"font-weight: bold;\">");
                sw.write("Nombre de la Hoja: " + tmpSheet.getId());
                sw.write("</span></big></td>\n</tr>\n");
                sw.write("    <tr><td>Fila</td><td>Columna</td><td>Expresi&oacute;n</td> <td>Valor Num&eacute;rico</td>    </tr>\n");
                // obtener lista de datos desde matriz
                if( tmpSheet.getHoja().getTabla().estaVacia() == false){ // si la tabla tiene datos
                    datos = tmpSheet.getHoja().getTabla().getSubset(1,1,numCols,numRows);
                    //escribir tabla  con datos generados
                    for (int j = 0; j < datos.size();j++){
                        tmpDato = (tda.Dato) datos.get(j);                        
                        sw.write("<tr>\n");
                        sw.write("<td>" + tmpDato.getRow() + "</td>\n");
                        sw.write("<td>" + tmpDato.getCol() + "</td>\n");
                        sw.write("<td>" + tmpDato.getExpr() + "</td>\n");
                        sw.write("<td>" + tmpDato.eval() + "</td>\n");
                        sw.write("</tr>\n");
                    }
                }
                else{
                    sw.write("<tr><td colspan=\"4\" rowspan=\"1\"> Hoja Vacia... </td></tr>");
                }
                sw.write("  </tbody></table>\n<br><br><br>");
    //            sw.write();
    //            sw.write();
            }
            //escribir fin de datos
            sw.write("</body></html>");
            //escribir a archivo de salida
            sw.close();
        }
        catch(Exception e){
            System.out.println("No se pudo guardar archivo:" + e);
        }
                   
    }
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jDialogNewSheet = new javax.swing.JDialog();
        jPanelNewSheet = new javax.swing.JPanel();
        jLabelNewSheet = new javax.swing.JLabel();
        jTextFieldNewSheet = new javax.swing.JTextField();
        jButtonCancelNS = new javax.swing.JButton();
        jButtonOKNS = new javax.swing.JButton();
        jDialogAbout = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenuArchivo = new javax.swing.JMenu();
        jMenuNuevo = new javax.swing.JMenuItem();
        jMenuAbrir = new javax.swing.JMenuItem();
        jMenuGuardar = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuSalir = new javax.swing.JMenuItem();
        jMenuInsertar = new javax.swing.JMenu();
        JMenuInsertarHoja = new javax.swing.JMenuItem();
        jMenuAyuda = new javax.swing.JMenu();
        jMenuAbout = new javax.swing.JMenuItem();

        jLabelNewSheet.setText("Ingrese nombre de la hoja:");

        jButtonCancelNS.setText("Cancelar");
        jButtonCancelNS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonCancelNSMouseReleased(evt);
            }
        });

        jButtonOKNS.setText("Aceptar");
        jButtonOKNS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonOKNSMouseReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanelNewSheetLayout = new org.jdesktop.layout.GroupLayout(jPanelNewSheet);
        jPanelNewSheet.setLayout(jPanelNewSheetLayout);
        jPanelNewSheetLayout.setHorizontalGroup(
            jPanelNewSheetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelNewSheetLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelNewSheetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabelNewSheet)
                    .add(jTextFieldNewSheet, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanelNewSheetLayout.createSequentialGroup()
                        .add(jButtonOKNS)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonCancelNS)))
                .addContainerGap())
        );
        jPanelNewSheetLayout.setVerticalGroup(
            jPanelNewSheetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelNewSheetLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabelNewSheet)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextFieldNewSheet, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanelNewSheetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonCancelNS)
                    .add(jButtonOKNS))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jDialogNewSheetLayout = new org.jdesktop.layout.GroupLayout(jDialogNewSheet.getContentPane());
        jDialogNewSheet.getContentPane().setLayout(jDialogNewSheetLayout);
        jDialogNewSheetLayout.setHorizontalGroup(
            jDialogNewSheetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 198, Short.MAX_VALUE)
            .add(jPanelNewSheet, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        jDialogNewSheetLayout.setVerticalGroup(
            jDialogNewSheetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 95, Short.MAX_VALUE)
            .add(jPanelNewSheet, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        jLabel1.setText("ElectricSheet V. 1.0");

        jLabel2.setText("(c) 2006 Erik Giron");

        jLabel3.setText("Para el Primer proyecto de Estructuras de Datos.");

        jLabel4.setText("Desarrollado por: Erik Vladimir Gir\u00f3n M\u00e1rquez");

        jLabel5.setText("Carnet No 200313492");

        jLabel6.setText("1er Ciclo 2006, Facultad de Ingenieria, USAC Guatemala.");

        jButton1.setMnemonic('C');
        jButton1.setText("Cerrar");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jDialogAboutLayout = new org.jdesktop.layout.GroupLayout(jDialogAbout.getContentPane());
        jDialogAbout.getContentPane().setLayout(jDialogAboutLayout);
        jDialogAboutLayout.setHorizontalGroup(
            jDialogAboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogAboutLayout.createSequentialGroup()
                .addContainerGap()
                .add(jDialogAboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel2)
                    .add(jLabel3)
                    .add(jLabel6)
                    .add(jDialogAboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(jLabel5)
                        .add(jLabel4)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialogAboutLayout.createSequentialGroup()
                .addContainerGap(289, Short.MAX_VALUE)
                .add(jButton1)
                .addContainerGap())
        );
        jDialogAboutLayout.setVerticalGroup(
            jDialogAboutLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialogAboutLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5)
                .add(14, 14, 14)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 47, Short.MAX_VALUE)
                .add(jButton1)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ElectricSheet");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jMenuArchivo.setMnemonic('a');
        jMenuArchivo.setText("Archivo");
        jMenuNuevo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuNuevo.setMnemonic('n');
        jMenuNuevo.setText("Nuevo");
        jMenuNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuNuevoActionPerformed(evt);
            }
        });
        jMenuNuevo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jMenuNuevoMouseReleased(evt);
            }
        });

        jMenuArchivo.add(jMenuNuevo);

        jMenuAbrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuAbrir.setMnemonic('a');
        jMenuAbrir.setText("Abrir...");
        jMenuAbrir.setToolTipText("Abre documento");
        jMenuAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAbrirActionPerformed(evt);
            }
        });
        jMenuAbrir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jMenuAbrirMouseReleased(evt);
            }
        });

        jMenuArchivo.add(jMenuAbrir);

        jMenuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuGuardar.setMnemonic('g');
        jMenuGuardar.setText("Guardar");
        jMenuGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jMenuGuardarMouseReleased(evt);
            }
        });

        jMenuArchivo.add(jMenuGuardar);

        jMenuArchivo.add(jSeparator1);

        jMenuSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuSalir.setText("Salir");
        jMenuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSalirActionPerformed(evt);
            }
        });
        jMenuSalir.addMenuKeyListener(new javax.swing.event.MenuKeyListener() {
            public void menuKeyPressed(javax.swing.event.MenuKeyEvent evt) {
            }
            public void menuKeyReleased(javax.swing.event.MenuKeyEvent evt) {
                jMenuSalirMenuKeyReleased(evt);
            }
            public void menuKeyTyped(javax.swing.event.MenuKeyEvent evt) {
            }
        });

        jMenuArchivo.add(jMenuSalir);

        jMenuBar2.add(jMenuArchivo);

        jMenuInsertar.setMnemonic('i');
        jMenuInsertar.setText("Insertar");
        JMenuInsertarHoja.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        JMenuInsertarHoja.setMnemonic('h');
        JMenuInsertarHoja.setText("Hoja...");
        JMenuInsertarHoja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JMenuInsertarHojaActionPerformed(evt);
            }
        });
        JMenuInsertarHoja.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                JMenuInsertarHojaMouseReleased(evt);
            }
        });

        jMenuInsertar.add(JMenuInsertarHoja);

        jMenuBar2.add(jMenuInsertar);

        jMenuAyuda.setMnemonic('y');
        jMenuAyuda.setText("Ayuda");
        jMenuAbout.setText("Acerca de...");
        jMenuAbout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jMenuAboutMouseReleased(evt);
            }
        });

        jMenuAyuda.add(jMenuAbout);

        jMenuBar2.add(jMenuAyuda);

        setJMenuBar(jMenuBar2);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuNuevoActionPerformed
            nuevo();
    }//GEN-LAST:event_jMenuNuevoActionPerformed

    private void JMenuInsertarHojaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JMenuInsertarHojaActionPerformed
        JMenuInsertarHojaMouseReleased( null);
    }//GEN-LAST:event_JMenuInsertarHojaActionPerformed

    private void jMenuSalirMenuKeyReleased(javax.swing.event.MenuKeyEvent evt) {//GEN-FIRST:event_jMenuSalirMenuKeyReleased
        System.exit(0);
    }//GEN-LAST:event_jMenuSalirMenuKeyReleased

    private void jMenuGuardarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuGuardarMouseReleased
        saveFileChooser();
    }//GEN-LAST:event_jMenuGuardarMouseReleased

    private void jMenuAboutMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuAboutMouseReleased
        jDialogAbout.setBounds(200,200,400,275);
        jDialogAbout.setVisible(true);
    }//GEN-LAST:event_jMenuAboutMouseReleased

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
     jDialogAbout.setVisible(false);
    }//GEN-LAST:event_jButton1MouseClicked

    private void jMenuNuevoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuNuevoMouseReleased
        nuevo();
    }//GEN-LAST:event_jMenuNuevoMouseReleased

    private void jMenuAbrirMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuAbrirMouseReleased
        fileChooser();
    }//GEN-LAST:event_jMenuAbrirMouseReleased

    private void jButtonCancelNSMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonCancelNSMouseReleased
        jDialogNewSheet.setVisible(false);
    }//GEN-LAST:event_jButtonCancelNSMouseReleased

    private void jMenuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSalirActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuSalirActionPerformed

    private void JMenuInsertarHojaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JMenuInsertarHojaMouseReleased
        jDialogNewSheet.setBounds(100,100,200,150);
        jDialogNewSheet.setVisible(true);
        
    }//GEN-LAST:event_JMenuInsertarHojaMouseReleased

    private void jButtonOKNSMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonOKNSMouseReleased
        if (jTextFieldNewSheet.getText().length() > 0){
            wp.insertarHoja(jTextFieldNewSheet.getText());
            jDialogNewSheet.setVisible(false);
        }
    }//GEN-LAST:event_jButtonOKNSMouseReleased

    private void jMenuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAbrirActionPerformed
        fileChooser();
    
        
    }//GEN-LAST:event_jMenuAbrirActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem JMenuInsertarHoja;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonCancelNS;
    private javax.swing.JButton jButtonOKNS;
    private javax.swing.JDialog jDialogAbout;
    private javax.swing.JDialog jDialogNewSheet;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelNewSheet;
    private javax.swing.JMenuItem jMenuAbout;
    private javax.swing.JMenuItem jMenuAbrir;
    private javax.swing.JMenu jMenuArchivo;
    private javax.swing.JMenu jMenuAyuda;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuGuardar;
    private javax.swing.JMenu jMenuInsertar;
    private javax.swing.JMenuItem jMenuNuevo;
    private javax.swing.JMenuItem jMenuSalir;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelNewSheet;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextFieldNewSheet;
    // End of variables declaration//GEN-END:variables
    
}
