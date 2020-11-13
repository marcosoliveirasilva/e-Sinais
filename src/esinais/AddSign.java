package esinais;

import conexao.ConexaoBD;
import conexao.VetorPesquisa;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import javax.imageio.ImageReader;

public class AddSign extends javax.swing.JDialog {

    JFrame pai;
    ConexaoBD conexaoAdd;
    BufferedImage returnedImage;

    /**
     * Creates new form AddSign
     */
    public AddSign(JFrame parent, String word, ConexaoBD conexao) {
        super(parent, true);
        pai = parent;
        initComponents();
        conexaoAdd = conexao;
        jTextField1.setText(word);
    }

    /*Esse método copia um arquivo que será adicionado ao banco para a pasta onde o programa consegue reconhecer. 
    Como parâmetros temos o caminho atual do arquivo, e a resposta do usuário (true ou false). Esse método será chamado 
    após o programa identificar que já há um arquivo (imagem) cadastrada para determinado sinal pesquisado, caso o usuário deseje alterar
    a resposta deverá ser true, caso contrário a resposta será false
     */
    public void CopiarArquivo(String path, boolean resposta) throws IOException, SQLException {
        String palavra = jTextField1.getText();
        String type = path.substring(path.length() - 3, path.length());
        String newPath = "/images/gif/" + palavra + "." + type;
        if (resposta) {
            File image = new File(path);
            File newImage = new File("dados" + newPath);
            newImage.delete();
            int size = 1;
            if ("gif".equals(type)) {
                ImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
                ir.setInput(ImageIO.createImageInputStream(image));
                size = ir.getNumImages(true);
            }
            if (size >= 1) {
                FileChannel sourceChannel = null;
                FileChannel destinationChannel = null;
                try {
                    sourceChannel = new FileInputStream(image).getChannel();
                    destinationChannel = new FileOutputStream(newImage).getChannel();
                    sourceChannel.transferTo(0, sourceChannel.size(),
                            destinationChannel);
                } finally {
                    if (sourceChannel != null && sourceChannel.isOpen()) {
                        sourceChannel.close();
                    }
                    if (destinationChannel != null && destinationChannel.isOpen()) {
                        destinationChannel.close();
                    }
                }
            } else {
                ImageIO.write(returnedImage, type, newImage);
            }
        } else {
            File image = new File(path);
            File newImage = new File("dados" + newPath);

            int size = 1;
            if ("gif".equals(type)) {
                ImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
                ir.setInput(ImageIO.createImageInputStream(image));
                size = ir.getNumImages(true);
            }
            if (size >= 1) {
                FileChannel sourceChannel = null;
                FileChannel destinationChannel = null;
                try {
                    sourceChannel = new FileInputStream(image).getChannel();
                    destinationChannel = new FileOutputStream(newImage).getChannel();
                    sourceChannel.transferTo(0, sourceChannel.size(),
                            destinationChannel);
                } finally {
                    if (sourceChannel != null && sourceChannel.isOpen()) {
                        sourceChannel.close();
                    }
                    if (destinationChannel != null && destinationChannel.isOpen()) {
                        destinationChannel.close();
                    }
                }
            } else {
                ImageIO.write(returnedImage, type, newImage);
            }
        }
    }

    public void add() throws IOException {
        String newPathImagem = "";
        String palavra = jTextField1.getText();
        palavra = palavra.replaceAll("[^A-Za-z0-9áàãâäéèêëíìîïóòõôöúùûüçÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÖÔÚÙÛÜÇ]", "");
        //Pegar dados para a imagem
        String pathImagem = jTextField3.getText();
        if (pathImagem.equals("")) {
            pathImagem = "null";
        } else {
            String typeImagem = pathImagem.substring(pathImagem.length() - 3, pathImagem.length());
            newPathImagem = "/images/gif/" + palavra + "." + typeImagem;
        }
        //Pegar dados do sinal
        String pathSinal = jTextField2.getText();
        String typeSinal = pathSinal.substring(pathSinal.length() - 3, pathSinal.length());
        String newPathSinal = "/images/gif/" + palavra + "." + typeSinal;
        try {
            VetorPesquisa dados = new VetorPesquisa();
            conexaoAdd.query("SELECT endereco_sinal FROM PUBLIC.SINAL WHERE nome_sinal = '"
                    + palavra.toLowerCase() + "'", dados);
            Vector linhas = dados.getLinhas();
            System.out.println(linhas.toString());
            if (!"[]".equals(linhas.toString())) {
                int result = JOptionPane.showConfirmDialog(this, "Esta palavra já está cadastrada\n"
                        + "Deseja Substitui-la?", "e-Sinais", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    //Copiar o sinal da pasta de origem para a pasta onde do banco de dados
                    if (pathImagem.equals("null")) {
                        CopiarArquivo(pathSinal, true);
                    } else {
                        CopiarArquivo(pathImagem, true);
                        CopiarArquivo(pathSinal, true);
                    }
                    if (!newPathImagem.isEmpty()) {
                        conexaoAdd.update("UPDATE PUBLIC.SINAL SET endereco_sinal = '"
                                + newPathSinal + "', endereco_imagem = '" + newPathImagem + "' WHERE nome_sinal = '" + palavra.toLowerCase() + "'");
                    } else {
                        conexaoAdd.update("UPDATE PUBLIC.SINAL SET endereco_sinal = '"
                                + newPathSinal + "' WHERE nome_sinal = '" + palavra.toLowerCase() + "'");
                    }
                    conexaoAdd.update("COMMIT");
                    dispose();
                }
            } else {
                //Copiar arquivo para a pasta de destino
                if (pathImagem.equals("null")) {
                    CopiarArquivo(pathSinal, true);
                } else {
                    CopiarArquivo(pathImagem, true);
                    CopiarArquivo(pathSinal, true);
                }
                if (!newPathImagem.isEmpty()) {
                    conexaoAdd.update("INSERT INTO SINAL (NOME_SINAL, ENDERECO_SINAL, ENDERECO_IMAGEM) VALUES ('"
                            + palavra.toLowerCase() + "', '" + newPathSinal + "', '" + newPathImagem + "')");
                } else {
                    conexaoAdd.update("INSERT INTO SINAL (NOME_SINAL, ENDERECO_SINAL) VALUES ('"
                            + palavra.toLowerCase() + "', '" + newPathSinal + "')");
                }
                conexaoAdd.update("COMMIT");
                dispose();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddSign.class.getName()).log(Level.SEVERE, null, ex);
            ex.toString();
        }
    }

    void updateButtons() {
        String text = jTextField1.getText();
        text = text.replaceAll("[^A-Za-z0-9áàãâäéèêëíìîïóòõôöúùûüçÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÖÔÚÙÛÜÇ]", "");

        if (text.length() > 0) {
            viewImageButton.setEnabled(true);
        } else {
            viewImageButton.setEnabled(false);
        }

        if (text.length() > 0 && jTextField2.getText().length() > 0) {
            addButton.setEnabled(true);
        } else {
            addButton.setEnabled(false);
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

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        openSinalButton = new javax.swing.JButton();
        viewImageButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        openImageButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Adicionar Sinal");
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel1.setText("Palavra escrita em português:");

        jLabel4.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel4.setText("Imagem do Sinal em LIBRAS:");

        jTextField1.setFont(new java.awt.Font("Monospaced", 0, 20)); // NOI18N
        jTextField1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jTextField1.setPreferredSize(new java.awt.Dimension(4, 26));
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jTextField2.setEditable(false);
        jTextField2.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jTextField2.setPreferredSize(new java.awt.Dimension(4, 26));
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

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
        jLabel2.setText("Adicionar Sinal");

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

        viewImageButton.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        viewImageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/file_search.png"))); // NOI18N
        viewImageButton.setText("Vizualizar Imagem Cadastrada");
        viewImageButton.setEnabled(false);
        viewImageButton.setRequestFocusEnabled(false);
        viewImageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewImageButtonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel3.setText("Resolução ideal (249 x 380)");

        jLabel5.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel5.setText("Imagem associativa do Sinal:");

        jTextField3.setEditable(false);
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        openImageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/search.png"))); // NOI18N
        openImageButton.setRequestFocusEnabled(false);
        openImageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openImageButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(viewImageButton)
                        .addGap(5, 5, 5)
                        .addComponent(cancelButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(openSinalButton)
                                    .addComponent(openImageButton))))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .addComponent(openSinalButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(openImageButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(62, 62, 62)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(cancelButton)
                    .addComponent(viewImageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTextField1.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                //System.out.println("insertUpdate");
                jTextField1InsertText(e);
            }

            public void removeUpdate(DocumentEvent e) {
                // System.out.println("removeUpdate");
                jTextField1RemoveText(e);
            }

            public void changedUpdate(DocumentEvent e) {
                //System.out.println("changeUpdate");
                jTextField1ChangedText(e);
            }
        });
        jTextField2.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                //System.out.println("insertUpdate");
                jTextField2InsertText(e);
            }

            public void removeUpdate(DocumentEvent e) {
                // System.out.println("removeUpdate");
                jTextField2RemoveText(e);
            }

            public void changedUpdate(DocumentEvent e) {
                //System.out.println("changeUpdate");
                jTextField2ChangedText(e);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        try {
            add();
        } catch (IOException ex) {
            Logger.getLogger(AddSign.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void openSinalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSinalButtonActionPerformed
        JFileChooser openFile = new JFileChooser();

        UIManager.put("FileChooser.fileNameLabelText", "Nome da Imagem:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Mostrar imagens do tipo:");
        SwingUtilities.updateComponentTreeUI(openFile);

        openFile.setDialogTitle("Selecionar Imagem");

        openFile.setAcceptAllFileFilterUsed(false);
        openFile.setFileFilter(new FileNameExtensionFilter("*.jpg", "jpg"));
        openFile.setFileFilter(new FileNameExtensionFilter("*.png", "png"));
        openFile.setFileFilter(new FileNameExtensionFilter("*.gif", "gif"));
        openFile.setFileFilter(new AllFiles());

        int returnVal = openFile.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            jTextField2.setText(openFile.getSelectedFile().toString());
        }
    }//GEN-LAST:event_openSinalButtonActionPerformed

    private void viewImageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewImageButtonActionPerformed
        String text = jTextField1.getText();
        text = text.replaceAll("[^A-Za-z0-9áàãâäéèêëíìîïóòõôöúùûüçÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÖÔÚÙÛÜÇ]", "");

        String out = "/dados/images/gif/not_found.gif";

        try {

            VetorPesquisa dados = new VetorPesquisa();
            conexaoAdd.query("SELECT endereco_sinal FROM PUBLIC.SINAL WHERE nome_sinal = '"
                    + text.toLowerCase() + "'", dados);
            Vector linhas = dados.getLinhas();

            if (!"[]".equals(linhas.toString())) {
                out = "/dados" + linhas.toString().substring(2, linhas.toString().length() - 2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddSign.class.getName()).log(Level.SEVERE, null, ex);
        }

        View view = new View(pai, out);
        view.setVisible(true);

    }//GEN-LAST:event_viewImageButtonActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void openImageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openImageButtonActionPerformed
        JFileChooser openFile = new JFileChooser();

        UIManager.put("FileChooser.fileNameLabelText", "Nome da Imagem:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Mostrar imagens do tipo:");
        SwingUtilities.updateComponentTreeUI(openFile);

        openFile.setDialogTitle("Selecionar Imagem");

        openFile.setAcceptAllFileFilterUsed(false);
        openFile.setFileFilter(new FileNameExtensionFilter("*.jpg", "jpg"));
        openFile.setFileFilter(new FileNameExtensionFilter("*.png", "png"));
        openFile.setFileFilter(new AllFiles());

        int returnVal = openFile.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String text = jTextField1.getText();
            text = text.replaceAll("[^A-Za-z0-9áàãâäéèêëíìîïóòõôöúùûüçÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÖÔÚÙÛÜÇ]", "");
            jTextField3.setText(openFile.getSelectedFile().toString());
            if (jTextField3.getText().length() != 0) {
                Edit edit = new Edit(pai, jTextField3.getText(), text);
                edit.setVisible(true);
                returnedImage = edit.getImage();
            }
        } else {
            jTextField3.setText("null");
        }


    }//GEN-LAST:event_openImageButtonActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed

    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField1ChangedText(DocumentEvent e) {
    }

    private void jTextField1InsertText(DocumentEvent e) {
        updateButtons();
    }

    private void jTextField1RemoveText(DocumentEvent e) {
        updateButtons();
    }

    private void jTextField2ChangedText(DocumentEvent e) {
    }

    private void jTextField2InsertText(DocumentEvent e) {
        String text = jTextField1.getText();
        text = text.replaceAll("[^A-Za-z0-9áàãâäéèêëíìîïóòõôöúùûüçÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÖÔÚÙÛÜÇ]", "");
        String path = jTextField2.getText();
        if (path.length() != 0) {
            Edit edit = new Edit(pai, path, text);
            edit.setVisible(true);
            returnedImage = edit.getImage();
        }
        updateButtons();
    }

    private void jTextField2RemoveText(DocumentEvent e) {
        String text = jTextField1.getText();
        text = text.replaceAll("[^A-Za-z0-9áàãâäéèêëíìîïóòõôöúùûüçÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÖÔÚÙÛÜÇ]", "");
        String path = jTextField2.getText();
        if (path.length() != 0) {
            Edit edit = new Edit(pai, path, text);
            edit.setVisible(true);
            returnedImage = edit.getImage();
        }
        updateButtons();
    }
    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JButton openImageButton;
    private javax.swing.JButton openSinalButton;
    private javax.swing.JButton viewImageButton;
    // End of variables declaration//GEN-END:variables
}
