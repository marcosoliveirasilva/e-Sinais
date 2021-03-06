package esinais;

import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.ImageIcon;

public class Edit extends java.awt.Dialog {

    String palavra;
    String type;
    Color color = Color.white;
    BufferedImage returnedImage;
    BufferedImage new_img;
    BufferedImage img;

    /**
     * Creates new form Edit
     */
    public Edit(java.awt.Frame parent, String path, String x) {
        super(parent, true);
        initComponents();

        palavra = x;
        String pathEdit = path;
        type = pathEdit.substring(pathEdit.length() - 3, pathEdit.length());

        try {
            img = ImageIO.read(new File(pathEdit));
            jLabel1.setIcon(new ImageIcon(pathEdit));

            int size = 1;
            if (size == 1) {
                resize.setEnabled(true);
                withText.setEnabled(true);
                withoutText.setEnabled(true);
                colorComboBox.setEnabled(true);
                fontSizeComboBox.setEnabled(true);
            } else {
                jLabel2.setText("Imagens animadas não podem");
                jLabel3.setText("ser editadas");
            }

            returnedImage = img;
            new_img = img;
        } catch (IOException ex) {
            Logger.getLogger(Edit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public BufferedImage getImage() {

        int bigwidth = returnedImage.getWidth();
        int bigheight = returnedImage.getHeight();
        int cropwidth = 249;
        int cropheight = 380;

        if (bigwidth < cropwidth) {
            cropwidth = bigwidth;
        }

        if (bigheight < cropheight) {
            cropheight = bigheight;
        }

        BufferedImage rightSize = returnedImage.getSubimage((bigwidth - cropwidth) / 2, (bigheight - cropheight) / 2, cropwidth, cropheight);

        return rightSize;
    }

    void draw() throws IOException {
        withoutText.setSelected(false);
        withText.setSelected(true);

        save.setEnabled(true);

        BufferedImage tmp;

        if (!"jpg".equals(type)) {
            tmp = new BufferedImage(new_img.getWidth(), new_img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        } else {
            tmp = new BufferedImage(new_img.getWidth(), new_img.getHeight(), BufferedImage.TYPE_INT_RGB);
          
        }

        Graphics2D g = tmp.createGraphics();
        g.drawImage(new_img, 0, 0, null);
//        ImageIO.write(new_img,"jpg", new File("dados/images/DB_images/" + palavra + "." + type));
        g.setFont(new Font("TimesRoman", Font.PLAIN, Integer.parseInt(fontSizeComboBox.getSelectedItem().toString())));
        g.setColor(color);

        FontMetrics fm = g.getFontMetrics();

        int down = 180;
        if (new_img.getHeight() < 380) {
            down = new_img.getHeight() / 2 - 10;
        }

        g.drawString(palavra, (new_img.getWidth() / 2) - (fm.stringWidth(palavra) / 2), new_img.getHeight() / 2 + down);

        new_img = tmp;

        jLabel1.setIcon(new ImageIcon(new_img));
           
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        resize = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        save = new javax.swing.JButton();
        original = new javax.swing.JButton();
        withText = new javax.swing.JCheckBox();
        withoutText = new javax.swing.JCheckBox();
        originalSize = new javax.swing.JButton();
        colorComboBox = new javax.swing.JComboBox();
        fontSizeComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setResizable(false);
        setTitle("Editor de Imagem");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        resize.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        resize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/resize.png"))); // NOI18N
        resize.setText("Redimensionar Imagem");
        resize.setEnabled(false);
        resize.setFocusable(false);
        resize.setRequestFocusEnabled(false);
        resize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resizeActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        save.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        save.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/ok.png"))); // NOI18N
        save.setText("Salvar e Sair");
        save.setEnabled(false);
        save.setFocusable(false);
        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });

        original.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        original.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/exit.png"))); // NOI18N
        original.setText("Sair sem Salvar");
        original.setFocusable(false);
        original.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                originalActionPerformed(evt);
            }
        });

        withText.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        withText.setText("Com Texto");
        withText.setEnabled(false);
        withText.setFocusable(false);
        withText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withTextActionPerformed(evt);
            }
        });

        withoutText.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        withoutText.setSelected(true);
        withoutText.setText("Sem Texto");
        withoutText.setEnabled(false);
        withoutText.setFocusable(false);
        withoutText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withoutTextActionPerformed(evt);
            }
        });

        originalSize.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        originalSize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Icons/Undo.png"))); // NOI18N
        originalSize.setText("Voltar ao Tamanho Original");
        originalSize.setEnabled(false);
        originalSize.setFocusable(false);
        originalSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                originalSizeActionPerformed(evt);
            }
        });

        colorComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Branco", "Preto", "Vermelho", "Amarelo" }));
        colorComboBox.setEnabled(false);
        colorComboBox.setFocusable(false);
        colorComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorComboBoxActionPerformed(evt);
            }
        });

        fontSizeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "20", "22", "24", "26", "28", "30", "32", "34", "36", "38", "40", "42", "44", "46", "48", "50" }));
        fontSizeComboBox.setSelectedItem("30");
        fontSizeComboBox.setEnabled(false);
        fontSizeComboBox.setFocusable(false);
        fontSizeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontSizeComboBoxActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setToolTipText("");

        jLabel3.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(save)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(original))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(resize)
                            .addComponent(originalSize, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(colorComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(withoutText, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                .addComponent(withText, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(fontSizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(resize)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(originalSize)
                        .addGap(70, 70, 70)
                        .addComponent(withText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(withoutText)
                        .addGap(14, 14, 14)
                        .addComponent(colorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fontSizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(original)
                    .addComponent(save))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        dispose();
    }//GEN-LAST:event_closeDialog

    private void originalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_originalActionPerformed
        dispose();
    }//GEN-LAST:event_originalActionPerformed

    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
        returnedImage = new_img;
        dispose();
    }//GEN-LAST:event_saveActionPerformed

    private void resizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeActionPerformed
        originalSize.setEnabled(true);
        resize.setEnabled(false);
        save.setEnabled(true);
        if (!"jpg".equals(type)) {
            new_img = new BufferedImage(249, 380, BufferedImage.TYPE_INT_ARGB);
        } else {
            new_img = new BufferedImage(249, 380, BufferedImage.TYPE_INT_RGB);
        }
        new_img.getGraphics().drawImage(img, 0, 0, 249, 380, null);
        if (withText.isSelected()) {
            try {
                draw();
            } catch (IOException ex) {
                Logger.getLogger(Edit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        jLabel1.setIcon(new ImageIcon(new_img));
    }//GEN-LAST:event_resizeActionPerformed

    private void originalSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_originalSizeActionPerformed
        originalSize.setEnabled(false);
        resize.setEnabled(true);

        if (withoutText.isSelected()) {
            save.setEnabled(false);
        }

        new_img = img;

        if (withText.isSelected()) {
            try {
                draw();
            } catch (IOException ex) {
                Logger.getLogger(Edit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        jLabel1.setIcon(new ImageIcon(new_img));
    }//GEN-LAST:event_originalSizeActionPerformed

    private void withTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withTextActionPerformed
        try {
            draw();
        } catch (IOException ex) {
            Logger.getLogger(Edit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_withTextActionPerformed

    private void withoutTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withoutTextActionPerformed
        withoutText.setSelected(true);
        withText.setSelected(false);

        if (!originalSize.isEnabled()) {
            save.setEnabled(false);
        }

        if (!"jpg".equals(type)) {
            new_img = new BufferedImage(new_img.getWidth(), new_img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        } else {
            new_img = new BufferedImage(new_img.getWidth(), new_img.getHeight(), BufferedImage.TYPE_INT_RGB);
        }

        Graphics2D g = new_img.createGraphics();
        g.drawImage(img, 0, 0, new_img.getWidth(), new_img.getHeight(), null);
        
        jLabel1.setIcon(new ImageIcon(new_img));
    }//GEN-LAST:event_withoutTextActionPerformed

    private void colorComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorComboBoxActionPerformed
        switch (colorComboBox.getSelectedItem().toString()) {
            case "Branco":
                color = Color.white;
                break;
            case "Preto":
                color = Color.black;
                break;
            case "Vermelho":
                color = Color.red;
                break;
            case "Amarelo":
                color = Color.yellow;
                break;
        }

        if (withText.isSelected()) {
            try {
                draw();
            } catch (IOException ex) {
                Logger.getLogger(Edit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_colorComboBoxActionPerformed

    private void fontSizeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontSizeComboBoxActionPerformed

        if (!"jpg".equals(type)) {
            new_img = new BufferedImage(new_img.getWidth(), new_img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        } else {
            new_img = new BufferedImage(new_img.getWidth(), new_img.getHeight(), BufferedImage.TYPE_INT_RGB);
        }

        Graphics2D g = new_img.createGraphics();
        g.drawImage(img, 0, 0, new_img.getWidth(), new_img.getHeight(), null);

        if (withText.isSelected()) {
            try {
                draw();
            } catch (IOException ex) {
                Logger.getLogger(Edit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_fontSizeComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox colorComboBox;
    private javax.swing.JComboBox fontSizeComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton original;
    private javax.swing.JButton originalSize;
    private javax.swing.JButton resize;
    private javax.swing.JButton save;
    private javax.swing.JCheckBox withText;
    private javax.swing.JCheckBox withoutText;
    // End of variables declaration//GEN-END:variables
}
