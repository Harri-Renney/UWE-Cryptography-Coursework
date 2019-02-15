/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptography.rainbow.table;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import static java.lang.Math.pow;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Harri Renney
 */
public class MainForm extends javax.swing.JFrame {
    private char[] alphabet = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
    private HashMap rainbowTable = new HashMap();
    private int chainLength = 5000;
    private int passwordLength = 9;
    private int sizeOfPasswordSpace;
    private int rainbowTableSize;
    BigInteger primeModulus;
    
    //Takes a byte array as message digest givees and converts to string of hex represenation.
    private static String convertToHex(byte[] data) 
    { 
        StringBuffer buf = new StringBuffer(); 
        for (int i = 0; i < data.length; i++) { 
        	int halfbyte = (data[i] >>> 4) & 0x0F; 
        	int two_halfs = 0; 
        	do { 
	            if ((0 <= halfbyte) && (halfbyte <= 9)) 
	                buf.append((char) ('0' + halfbyte)); 
	            else 
	            	buf.append((char) ('a' + (halfbyte - 10))); 
	            halfbyte = data[i] & 0x0F; 
        	} while(two_halfs++ < 1); 
        } 
        return buf.toString(); 
    } 
    
    //Produces SHA-1 hash of text given.
    public static String SHA1(String text)  
    throws NoSuchAlgorithmException, UnsupportedEncodingException  
    { 
	MessageDigest md; 
	md = MessageDigest.getInstance("SHA-1"); //Get SHA-1 as instance for message digest.
	byte[] sha1hash = new byte[40]; 
	md.update(text.getBytes("iso-8859-1"), 0, text.length()); //Update message digest with the text.
	sha1hash = md.digest(); 
	return convertToHex(sha1hash); //Convert the byte array to string of hex values.
    }
    
    //Converts the integer of hashed value into a password.
    public String intToPassword(int n)
    {
        int base = alphabet.length;
        int r;
        String ret = "";
        while(n >= 0)
        {
            r = n % base;
            n = n / base;
            ret = alphabet[r] + ret;
            n = n - 1;  //Take 1 each time to stop 1 to 1 mappings.
        }
        return ret;
    }
    
    //For each new chain generate a password of password length.
    public String generatePassword()
    {
        String ret = "";
        Random rand = new Random();
        for(int i = 0; i != passwordLength; ++i)
        {
            ret = alphabet[rand.nextInt(alphabet.length)] + ret;
        }
        return ret;
    }
    
    //Reduce function converts hashed value into some plain text.
    public String reduce(String hash, int pos)
    {
       //Take hash string and convert it to big integer.
       BigInteger bigNumber = new BigInteger(Integer.toString(hash.hashCode()));
       //Add the position in chain to avoid collisions.
       bigNumber = bigNumber.add(new BigInteger(Integer.toString(pos)));
       //Mod value by prime greater than search space.
       int n = bigNumber.mod(primeModulus).intValue();  
       return intToPassword(n);  
    }
    
    //Chain reduce that works along the chain applying reduction and ahshing until it reaches the end.
    public String chainReduce(String hash, int pos)
    {
        String password = reduce(hash, pos);
        while(pos != chainLength)
        {
            try {
                password = SHA1(password);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }

            ++pos;
            password = reduce(hash, pos);
        }
        return password;
    }

    /**
     * Creates new form MainForm
     */
    public MainForm() {
        initComponents();
        
        ////////////////////////////////////////////////
        //Initalize ready for rainbow table generation//
        ////////////////////////////////////////////////
        
        //Work out size of search space.
        BigInteger sizeOfPasswordSpaceBI = new BigInteger("0");
        for(int i = 0; i != passwordLength; ++i)
        {
            BigInteger value = new BigInteger(Integer.toString(alphabet.length));
            int exponent = i;
            sizeOfPasswordSpaceBI = sizeOfPasswordSpaceBI.add(value.pow(exponent));
        }
        sizeOfPasswordSpace = sizeOfPasswordSpaceBI.intValue();
        rainbowTableSize = (int)((sizeOfPasswordSpace / chainLength) * 1.5);
        System.out.println("Size of Password Space: " + sizeOfPasswordSpaceBI.toString());
        
        //Work out the next greatest prime above search space.
        primeModulus = new BigInteger(Integer.toString(sizeOfPasswordSpace));
        while(!primeModulus.isProbablePrime(100))
            {primeModulus = primeModulus.add(new BigInteger("1"));}
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Generate Rainbow Table");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //////////////////////////////////
    //Button Generates Rainbow Table//
    //////////////////////////////////
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        //Need to add possible "00", "000" etc.
        for(int i = 0; i != rainbowTableSize; ++i)
        {
            try {
                //Generate a start of chain.
                String iString = generatePassword();
                
//Use this for unique first chain value.
//                while(rainbowTable.containsValue(iString))
//                    iString = generatePassword();
                
                //Begin hasing and reducing for chain length to find last value.
                String hashCode = SHA1(iString);
                String reducedCode = reduce(hashCode, 0);
                for(int j = 1; j != chainLength; ++j)
                {
                    hashCode = SHA1(reducedCode);
                    reducedCode = reduce(hashCode, j);
                }
                
//And this to have unique end of chain. (Takes longer to compute so commented out)
//                if(rainbowTable.containsKey(reducedCode))
//                    --i;
//                else
                
                //Place the end as they key and the beginning as the value for hashmap
                rainbowTable.put(reducedCode, iString);
                
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        //Write new rainbow table hash map into file.
        try {
            FileOutputStream fileOut = new FileOutputStream("RainbowTable.map");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(rainbowTable);
            out.close();
            fileOut.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
}