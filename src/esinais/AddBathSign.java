package esinais;

import Controller.ControllerAddBathSign;
import conexao.ConexaoBD;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class AddBathSign extends javax.swing.JDialog {
    String pesquisa = null;
    
    JFrame pai;
    Load2 load;
    BufferedImage returnedImage;
    ControllerAddBathSign controller;
    ArrayList<Object[]> lista;
    Thread threadLoad;
    boolean allDone = false;
    
    private static AddBathSign uniqueInstance;
 
    /**
     * Creates new form AddSign
     * @param parent
     * @param conexao
     */
    public static synchronized AddBathSign getInstance(JFrame parent, ConexaoBD conexao) {
        if (uniqueInstance == null)
            uniqueInstance = new AddBathSign(parent, conexao);
           
        return uniqueInstance;
    }
    
    private AddBathSign(JFrame parent, ConexaoBD conexao) {
        super(parent, true);
        pai = parent;
        initComponents();
        controller = ControllerAddBathSign.getInstance(conexao);
        lista = new ArrayList<>();
        System.out.println(pesquisa);
    }
    
    private void Load (){
        load = new Load2(pai);

        load.setBackground(new Color(0,0,0,0));
        load.setVisible(true);
    }
    
    private void ThreadUpdate(ArrayList<String[]> repeatedImgList){
        threadLoad = new Thread(){
            @Override
            public void run(){
                controller.Update(repeatedImgList);
                try {
                    Thread.currentThread().sleep(2000);
                    load.setVisible(false);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AddBathSign.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        threadLoad.start();
    }
    
    private void ThreadAdd(ArrayList<String[]> imgList){
        threadLoad = new Thread(){
            @Override
            public void run(){
                controller.Add(imgList);
                
                try {
                    Thread.currentThread().sleep(2000);
                    load.setVisible(false);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AddBathSign.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        };
        threadLoad.start();
    }
    
    private void RepeatedSigns(ArrayList<String[]> repeatedImgList){
        String repeatedSigns = controller.ListRepeatedSigns(repeatedImgList);
        if(repeatedSigns != null){
            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setBorder(null);
            textArea.setOpaque(false);
            textArea.append(repeatedSigns);
            JScrollPane scrollPane = new JScrollPane(textArea);  
            textArea.setLineWrap(true);  
            textArea.setWrapStyleWord(true); 
            scrollPane.setPreferredSize( new Dimension( 400, 200 ) );
            scrollPane.setBorder(null);
            int updateSignal = JOptionPane.showConfirmDialog(this, scrollPane, "e-Sinais",  
                                                   JOptionPane.YES_NO_OPTION);
            
            if (updateSignal == JOptionPane.YES_OPTION){
                ThreadUpdate(repeatedImgList);
                Load();
            }
        }        
    }
    
    private void MessageSuccess(){
        String messageSuccess = controller.SuccessSavedImg();
        if(messageSuccess != null){
            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setBorder(null);
            textArea.setOpaque(false);
            textArea.append(messageSuccess);
            JScrollPane scrollPane = new JScrollPane(textArea);  
            textArea.setLineWrap(true);  
            textArea.setWrapStyleWord(true); 
            scrollPane.setPreferredSize( new Dimension( 400, 200 ) );
            scrollPane.setBorder(null);
            JOptionPane.showMessageDialog(this, scrollPane, "e-Sinais",  
                                                   JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void MessageError(){
        String messageError = controller.ErrorSavingImg();
        if(messageError != null){
            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setBorder(null);
            textArea.setOpaque(false);
            textArea.append(messageError);
            JScrollPane scrollPane = new JScrollPane(textArea);  
            textArea.setLineWrap(true);  
            textArea.setWrapStyleWord(true); 
            scrollPane.setPreferredSize( new Dimension( 400, 200 ) );
            scrollPane.setBorder(null);
            JOptionPane.showMessageDialog(this, scrollPane, "e-Sinais",  
                                                   JOptionPane.INFORMATION_MESSAGE);
        }
    }
   
    private void QueryTableRows() throws SQLException, IOException{
        int nLines = jTable.getModel().getRowCount();
        
        ArrayList<String[]> imgList = new ArrayList();
        ArrayList<String[]> repeatedImgList = new ArrayList();
        
        for(int i=0; i<nLines ;i++){
            String [] signalData = new String[3];
            
            if((boolean) jTable.getModel().getValueAt(i ,0)){
                signalData [0] = (String) jTable.getModel().getValueAt(i ,3);
                signalData [1] = folderPathSinais.getText()+"\\"+(String) jTable.getModel().getValueAt(i ,1);
                signalData [2] = (String) jTable.getModel().getValueAt(i ,2);
                
                if(!signalData [2].equals(""))
                    signalData [2] = folderPathImgAssociatives.getText()+"\\"+ signalData [2];
                
                if(!controller.checkWordSignal(signalData [0]))
                    imgList.add(signalData);
                else
                    repeatedImgList.add(signalData);
            }
        }
        
        ThreadAdd(imgList);
        Load();
        RepeatedSigns(repeatedImgList);        
        MessageSuccess();        
        MessageError();
       
        lista = controller.UpdateRows(lista);
        controller.ClearArray();
        if(lista.isEmpty())
            dispose();
        else
            FillTable();
        
    }

    private void UpdateButtons() { 
        if (jTable.getRowCount() > 0){
            addButton.setEnabled(true);
            openAuxImgButton.setEnabled(true);
        }else{
            addButton.setEnabled(false);
            openAuxImgButton.setEnabled(false);
        }
               
    }
    
    public void ClearTable(){
        //LIMPAR TABELA
        if(jTable.getRowCount()>0){
            int nLinhas = jTable.getRowCount();
            
            for(int i = nLinhas-1; i > -1; i--)
                ((DefaultTableModel) jTable.getModel()).removeRow(i);
        }
    }
    
    private void AssociativeImg(){
        ArrayList<Object[]> auxiliaryList = controller.AssociativeImg(folderPathImgAssociatives.getText(), lista);
        
        lista = auxiliaryList;
        FillTable();        
    }
     
    private void FillTable(){
        ClearTable();
        
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();        

        if(!lista.isEmpty()){
            for(int i = 0; i < lista.size(); i++){
                Object[] linha = lista.get(i);
                
                model.addRow(linha);
            } 
        }
        
        UpdateButtons();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        folderPathSinais = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        openSinalButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        folderPathImgAssociatives = new javax.swing.JTextField();
        openAuxImgButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Adicionar Sinais em Lote");
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel4.setText("Pasta Sinais em LIBRAS:");

        folderPathSinais.setEditable(false);
        folderPathSinais.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        folderPathSinais.setPreferredSize(new java.awt.Dimension(4, 26));
        
        addButton.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/plus.png"))); // NOI18N
        addButton.setText("Adicionar");
        addButton.setEnabled(false);
        addButton.setRequestFocusEnabled(false);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Comic Sans MS", 1, 28)); // NOI18N
        jLabel2.setText("Adicionar Sinais em Lote");

        cancelButton.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/exit.png"))); // NOI18N
        cancelButton.setText("Cancelar");
        cancelButton.setRequestFocusEnabled(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        openSinalButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/search.png"))); // NOI18N
        openSinalButton.setRequestFocusEnabled(false);
        openSinalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSinalButtonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel3.setText("Resolução ideal (249 x 380)");

        jTable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "ARQUIVO IMAGEM SINAL EM LIBRAS", "ARQUIVO IMAGEM ASSOCIATIVA", "PALAVRA ESCRITA EM PORTUGUÊS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable.setInheritsPopupMenu(true);
        jTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable);
        if (jTable.getColumnModel().getColumnCount() > 0) {
            jTable.getColumnModel().getColumn(0).setMinWidth(40);
            jTable.getColumnModel().getColumn(0).setMaxWidth(40);
            jTable.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        }

        jLabel5.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel5.setText("Pasta Imagens Associativas:");

        folderPathImgAssociatives.setEditable(false);
        folderPathImgAssociatives.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        folderPathImgAssociatives.setPreferredSize(new java.awt.Dimension(4, 26));
        
        openAuxImgButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/search.png"))); // NOI18N
        openAuxImgButton.setEnabled(false);
        openAuxImgButton.setRequestFocusEnabled(false);
        openAuxImgButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAuxImgButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(folderPathSinais, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(openSinalButton, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addButton)
                        .addGap(18, 18, 18)
                        .addComponent(cancelButton))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(folderPathImgAssociatives, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(openAuxImgButton)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(folderPathSinais, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(openSinalButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(openAuxImgButton)
                    .addComponent(folderPathImgAssociatives, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        try {
            QueryTableRows();
        } catch (SQLException | IOException ex) {
            Logger.getLogger(AddBathSign.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void openSinalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSinalButtonActionPerformed
        JFileChooser openFile = new JFileChooser();

        UIManager.put("FileChooser.fileNameLabelText", "Nome da Imagem:");
        //UIManager.put("FileChooser.filesOfTypeLabelText", "Mostrar imagens do tipo:");
        SwingUtilities.updateComponentTreeUI(openFile);

        openFile.setDialogTitle("Selecionar Pasta");
        openFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);        

        int returnVal = openFile.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION){ 
            folderPathSinais.setText(openFile.getSelectedFile().toString());
            pesquisa = openFile.getSelectedFile().toString();
            System.out.println(pesquisa);
        }
                
        lista = controller.sinalImg(folderPathSinais.getText());
        FillTable();     
    }//GEN-LAST:event_openSinalButtonActionPerformed

    private void openAuxImgButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openAuxImgButtonActionPerformed
        JOptionPane.showMessageDialog(rootPane, "Para que as imagens sejam associadas,"
                + " estas devem ter o mesmo nome!");       
        
        JFileChooser openFile = new JFileChooser();
        UIManager.put("FileChooser.fileNameLabelText", "Nome da Imagem:");
        //UIManager.put("FileChooser.filesOfTypeLabelText", "Mostrar imagens do tipo:");
        SwingUtilities.updateComponentTreeUI(openFile);

        openFile.setDialogTitle("Selecionar Pasta");

        openFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);        

        int returnVal = openFile.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            folderPathImgAssociatives.setText(openFile.getSelectedFile().toString());
        }
        
        AssociativeImg();        
    }//GEN-LAST:event_openAuxImgButtonActionPerformed
    
    
    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField folderPathImgAssociatives;
    private javax.swing.JTextField folderPathSinais;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    private javax.swing.JButton openAuxImgButton;
    private javax.swing.JButton openSinalButton;
    // End of variables declaration//GEN-END:variables
}
