package esinais;

import Controller.ControllerDelSign;
import conexao.ConexaoBD;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

public class DelSign extends javax.swing.JDialog {
    String pesquisa = null;
    
    JFrame pai;
    Load2 load;
    BufferedImage returnedImage;
    ControllerDelSign controller;
    ArrayList<Object[]> lista;
    Thread threadLoad;
    boolean allDone = false;
    
    private static DelSign uniqueInstance;
 
    /**
     * Creates new form AddSign
     * @param parent
     * @param conexao
     */
    public static synchronized DelSign getInstance(JFrame parent, ConexaoBD conexao) {
        if (uniqueInstance == null)
            uniqueInstance = new DelSign(parent, conexao);
           
        return uniqueInstance;
    }
    
    private DelSign(JFrame parent, ConexaoBD conexao) {
        super(parent, true);
        pai = parent;
        initComponents();
        controller = ControllerDelSign.getInstance(conexao);
        lista = new ArrayList<>();
    }
    
    private void Load (){
        load = new Load2(pai);

        load.setBackground(new Color(0,0,0,0));
        load.setVisible(true);
    }
    
    public void ClearTable(){
        //LIMPAR TABELA
        if(jTable.getRowCount()>0){
            int nLinhas = jTable.getRowCount();
            
            for(int i = nLinhas-1; i > -1; i--)
                ((DefaultTableModel) jTable.getModel()).removeRow(i);
        }
    }
         
    private void FillTable(){
        ClearTable();
        
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();        
        if(!lista.isEmpty()){
            for(int i = 0; i < lista.size(); i++){
                Object[] linha = new Object[4];
                
                linha[0] = false;
                linha[1] = lista.get(i)[0];
                linha[2] = lista.get(i)[1];
                linha[3] = lista.get(i)[2];
                
                model.addRow(linha);
            } 
        }
    }
    
    private void messageSuccessDeleteSignis(ArrayList<String[]> excludedSigns){
        String message = "";
        if(!excludedSigns.isEmpty()){
            message = "Sinais excluidos com sucesso!"
                    + "\n\nSegue a lista:";
            for(int i = 0; i < excludedSigns.size(); i++){
                message = message + "\n     " + (String) excludedSigns.get(i)[0];
            }
        }        
        
        if(!message.equals("")){
            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setBorder(null);
            textArea.setOpaque(false);
            textArea.append(message);
            JScrollPane scrollPane = new JScrollPane(textArea);  
            textArea.setLineWrap(true);  
            textArea.setWrapStyleWord(true); 
            scrollPane.setPreferredSize( new Dimension( 400, 200 ) );
            scrollPane.setBorder(null);
            JOptionPane.showMessageDialog(this, scrollPane, "e-Sinais", 1);
        }        
    }
    
    private boolean messageDeleteSignis(ArrayList<String[]> repeatedImgList){
        boolean resposta = false;
        
        String message = "";
        if(!repeatedImgList.isEmpty()){
            message = "Deseja realmenete excluir os sinais descritos na lista a seguir?"
                    + "\n\nSegue a lista:";
            for(int i = 0; i < repeatedImgList.size(); i++){
                message = message + "\n     " + (String) repeatedImgList.get(i)[0];
            }
        }        
        
        if(!message.equals("")){
            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setBorder(null);
            textArea.setOpaque(false);
            textArea.append(message);
            JScrollPane scrollPane = new JScrollPane(textArea);  
            textArea.setLineWrap(true);  
            textArea.setWrapStyleWord(true); 
            scrollPane.setPreferredSize( new Dimension( 400, 200 ) );
            scrollPane.setBorder(null);
            int updateSignal = JOptionPane.showConfirmDialog(this, scrollPane, "e-Sinais",  
                                                   JOptionPane.YES_NO_OPTION);
            
            if (updateSignal == JOptionPane.YES_OPTION){
                resposta = true;
            }
        }        
        return resposta;
    }
    
    private void ThreadSearch(){
        threadLoad = new Thread(){
            @Override
            public void run(){
                lista.clear();
            if(!querySinal.getText().equals(""))
                lista.addAll(controller.selectSearch(querySinal.getText()));
            else
                lista.addAll(controller.selectAll());

            FillTable();
                
                try {
                    Thread.currentThread().sleep(100);
                    load.setVisible(false);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AddBathSign.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        };
        
        threadLoad.start();
    }
    
    private ArrayList<String[]> ThreadDelete(ArrayList<String[]> list){
        ArrayList<String[]> successDeleted = new ArrayList<>();
        
        threadLoad = new Thread(){
            @Override
            public void run(){
                successDeleted.addAll(controller.delete(list));
                
                try {
                    Thread.currentThread().sleep(100);
                    load.setVisible(false);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AddBathSign.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        };
        
        threadLoad.start();
        return successDeleted;
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
        querySinal = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        openSinalButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        deleteButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Remover Sinais");
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel4.setText("Buscar Sinal:");

        querySinal.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        querySinal.setPreferredSize(new java.awt.Dimension(4, 26));
        querySinal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                querySinalActionPerformed(evt);
            }
        });
        querySinal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                querySinalKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Comic Sans MS", 1, 28)); // NOI18N
        jLabel2.setText("Remover Sinais");

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
        openSinalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSinalButtonActionPerformed(evt);
            }
        });
 
        jTable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "NOME SINAL", "IMAGEM SINAL", "IMAGEM ASSOCIATIVA"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false
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
            jTable.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        }

        deleteButton.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/delete.png"))); // NOI18N
        deleteButton.setText("Excluir");
        deleteButton.setRequestFocusEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 777, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(querySinal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(openSinalButton, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                        .addComponent(querySinal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(openSinalButton))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(deleteButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void openSinalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSinalButtonActionPerformed
        ThreadSearch();
        Load();
    }//GEN-LAST:event_openSinalButtonActionPerformed

    private void querySinalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_querySinalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_querySinalActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int nLines = jTable.getModel().getRowCount();
        ArrayList<String[]> list = new ArrayList();
        
        for(int i=0; i<nLines ;i++){
            String [] signalData = new String[3];
            
            if((boolean) jTable.getModel().getValueAt(i ,0)){
                signalData [0] = (String) jTable.getModel().getValueAt(i ,1);
                signalData [1] = (String) jTable.getModel().getValueAt(i ,2);
                if(jTable.getModel().getValueAt(i ,3) == null)
                    signalData [2] = "";
                else
                    signalData [2] = (String) jTable.getModel().getValueAt(i ,3);
                
                list.add(signalData);
            }
        }
        if(messageDeleteSignis(list)){
            ArrayList<String[]> messageSuccess = ThreadDelete(list);
            Load();
            messageSuccessDeleteSignis(messageSuccess);
            ThreadSearch();
            Load();
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void querySinalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_querySinalKeyPressed
        if (evt.getKeyCode() == 10)
            openSinalButton.doClick();
    }//GEN-LAST:event_querySinalKeyPressed
   
    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    private javax.swing.JButton openSinalButton;
    private javax.swing.JTextField querySinal;
    // End of variables declaration//GEN-END:variables
}
