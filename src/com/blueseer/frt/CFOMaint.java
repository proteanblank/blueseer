/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn 

All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.blueseer.frt;


import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.edi.EDILogBrowse;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import static com.blueseer.adm.admData.addChangeLog;
import static com.blueseer.frt.frtData.addCFOMstr;
import static com.blueseer.frt.frtData.addCFOTransaction;
import com.blueseer.frt.frtData.cfo_det;
import com.blueseer.frt.frtData.cfo_item;
import com.blueseer.frt.frtData.cfo_mstr;
import static com.blueseer.frt.frtData.deleteCFOMstr;
import static com.blueseer.frt.frtData.getCFODet;
import static com.blueseer.frt.frtData.getCFOMstr;
import static com.blueseer.frt.frtData.updateCFOMstr;
import com.blueseer.shp.shpData;
import static com.blueseer.shp.shpData.confirmShipperTransaction;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkLength;
import static com.blueseer.utl.BlueSeerUtils.clog;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.logChange;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
import com.blueseer.utl.EDData;
import com.blueseer.utl.IBlueSeerT;
import static com.blueseer.utl.OVData.updateFreightOrderStatus;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 *
 * @author vaughnte
 */
public class CFOMaint extends javax.swing.JPanel implements IBlueSeerT {
    
    // global variable declarations
    public String globalstatus = "Open";
    public ArrayList<String[]> tablelist = new ArrayList<String[]>();
    public boolean lock_ddshipper = false;
    public int currentstopline = 0;
    boolean isLoad = false;
    public static cfo_mstr x = null;
    public static ArrayList<cfo_det> cfodetlist = null;
    public static ArrayList<cfo_item> cfoitemlist = null;
    public static LinkedHashMap<String, String[]> kvstop = new  LinkedHashMap<String, String[]>();
    public static LinkedHashMap<String, ArrayList<String[]>> itemmap = new  LinkedHashMap<String, ArrayList<String[]>>();
    public static LinkedHashMap<String, ArrayList<String[]>> schedmap = new  LinkedHashMap<String, ArrayList<String[]>>();
    public static LinkedHashMap<String, String> stk = new  LinkedHashMap<String, String>();
    
                
    // global datatablemodel declarations       
    
   // OVData avmdata = new OVData();
    javax.swing.table.DefaultTableModel myorddetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("type"),
                getGlobalColumnTag("date"),
                getGlobalColumnTag("name"), 
                getGlobalColumnTag("line1"), 
                getGlobalColumnTag("city"), 
                getGlobalColumnTag("state"), 
                getGlobalColumnTag("zip")
            });
      
    javax.swing.table.DefaultTableModel itemdetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("stopline"), 
                getGlobalColumnTag("itemline"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("order"),
                getGlobalColumnTag("qty"),
                getGlobalColumnTag("pallets"),
                getGlobalColumnTag("weight"),
                getGlobalColumnTag("ref"),
                getGlobalColumnTag("remarks")
            });
    
   
   
    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(Color.blue);
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
   
    class SomeRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
        
        String status = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 2);  
        
         if ("LD".equals(status)) {
            c.setBackground(Color.blue);
            c.setForeground(Color.WHITE);
        } else if ("UL".equals(status)) {
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        } else if ("PU".equals(status)) {
            c.setBackground(Color.yellow);
            c.setForeground(Color.BLACK);
        }
        else {
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        }       
        
        //c.setBackground(row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
      // c.setBackground(row % 2 == 0 ? Color.GREEN : Color.LIGHT_GRAY);
      // c.setBackground(row % 3 == 0 ? new Color(245,245,220) : Color.LIGHT_GRAY);
       /*
            if (column == 3)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       */
        return c;
    }
    }
   
   
    
    
      
   
    public CFOMaint() {
        initComponents();
        setLanguageTags(this);
    }

    
      // interface functions implemented
    public void executeTask(BlueSeerUtils.dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(BlueSeerUtils.dbaction type, String[] key) { 
              this.type = type.name();
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            
             switch(this.type) {
                case "add":
                    message = addRecord(key);
                    break;
                case "update":
                    message = updateRecord(key);
                    break;
                case "delete":
                    message = deleteRecord(key);    
                    break;
                case "get":
                    message = getRecord(key);    
                    break;    
                default:
                    message = new String[]{"1", "unknown action"};
            }
            
            return message;
        }
 
        
       public void done() {
            try {
            String[] message = get();
           
            BlueSeerUtils.endTask(message);
           if (this.type.equals("delete")) {
             initvars(null);  
           } else if (this.type.equals("get")) {
             updateForm();  
             tbkey.requestFocus();
           } else {
             initvars(null);  
           }
           
            
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"","Running..."});
       Task z = new Task(x, y); 
       z.execute(); 
       
    }
   
    public void setPanelComponentState(Object myobj, boolean b) {
        JPanel panel = null;
        JTabbedPane tabpane = null;
        JScrollPane scrollpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else if (myobj instanceof JScrollPane) {
           scrollpane = (JScrollPane) myobj;    
        } else {
            return;
        }
        
        if (panel != null) {
        panel.setEnabled(b);
        Component[] components = panel.getComponents();
        
            for (Component component : components) {
                if (component instanceof JLabel || component instanceof JTable ) {
                    continue;
                }
                if (component instanceof JPanel) {
                    setPanelComponentState((JPanel) component, b);
                }
                if (component instanceof JTabbedPane) {
                    setPanelComponentState((JTabbedPane) component, b);
                }
                if (component instanceof JScrollPane) {
                    setPanelComponentState((JScrollPane) component, b);
                }
                
                component.setEnabled(b);
            }
        }
            if (tabpane != null) {
                tabpane.setEnabled(b);
                Component[] componentspane = tabpane.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    if (component instanceof JPanel) {
                        setPanelComponentState((JPanel) component, b);
                    }
                    
                    component.setEnabled(b);
                    
                }
            }
            if (scrollpane != null) {
                scrollpane.setEnabled(b);
                JViewport viewport = scrollpane.getViewport();
                Component[] componentspane = viewport.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    component.setEnabled(b);
                }
            }
    } 
    
    public void setLanguageTags(Object myobj) {
       JPanel panel = null;
        JTabbedPane tabpane = null;
        JScrollPane scrollpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else if (myobj instanceof JScrollPane) {
           scrollpane = (JScrollPane) myobj;    
        } else {
            return;
        }
       Component[] components = panel.getComponents();
       for (Component component : components) {
           if (component instanceof JPanel) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".panel." + component.getName())) {
                       ((JPanel) component).setBorder(BorderFactory.createTitledBorder(tags.getString(this.getClass().getSimpleName() +".panel." + component.getName())));
                    } 
                    setLanguageTags((JPanel) component);
                }
                if (component instanceof JLabel ) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JLabel) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    }
                }
                if (component instanceof JButton ) {
                    if (tags.containsKey("global.button." + component.getName())) {
                       ((JButton) component).setText(tags.getString("global.button." + component.getName()));
                    }
                }
                if (component instanceof JCheckBox) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JCheckBox) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
                if (component instanceof JRadioButton) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JRadioButton) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
       }
    }
    
    public void setComponentDefaultValues() {
       isLoad = true;
       
       jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", jPanelMain);
        jTabbedPane1.add("Location", jPanelLocation);
       
       tbkey.setText("");
       cbhazmat.setSelected(false);
       
       ddorderstatus.setBackground(null);
       ddorderstatus.setSelectedItem("open");
       
       ddstopsequence.removeAllItems();
       ddstopsequence.addItem("");
       
       dcdate.setDate(new java.util.Date());
       
        tbkey.setText("");
        tbcustfo.setText("");
        tbcustforev.setText("");
        cbhazmat.setSelected(false);
        ddservicetype.setSelectedIndex(0);
        ddequiptype.setSelectedIndex(0);
        ddvehicle.setSelectedIndex(0);
        tbtrailer.setText("");
        ddorderstatus.setSelectedIndex(0);
        dddriver.setSelectedIndex(0);
        tbdrivercell.setText("");
        ddfotype.setSelectedIndex(0);
        ddbroker.setSelectedIndex(0);
        tbbrokercontact.setText("");
        tbbrokercell.setText("");
        ddratetype.setSelectedIndex(0);
        tbforate.setText("");
        tbmileage.setText("");
        tbdriverrate.setText("");
        cbstandard.setSelected(false);
        tbtotweight.setText("");
        dcloaddate.setDate(bsmf.MainFrame.now);
        dcunloaddate.setDate(bsmf.MainFrame.now);
        tbexpenses.setText("");
        tbcharges.setText("");
        tbcost.setText("");
        
        tablelist.clear();
       
        myorddetmodel.setRowCount(0);
        orddet.setModel(myorddetmodel);
        orddet.getTableHeader().setReorderingAllowed(false);
        /*
        orddet.getColumnModel().getColumn(10).setMaxWidth(0);
        orddet.getColumnModel().getColumn(10).setMinWidth(0);
        orddet.getColumnModel().getColumn(10).setPreferredWidth(0);
        orddet.getColumnModel().getColumn(11).setMaxWidth(0);
        orddet.getColumnModel().getColumn(11).setMinWidth(0);
        orddet.getColumnModel().getColumn(11).setPreferredWidth(0);
        */
        
        
        itemdetmodel.setRowCount(0);
        itemdet.setModel(itemdetmodel);
        
        
       
        ArrayList<String[]> initDataSets = frtData.getCarrierMaintInit();
        
        ddstate.removeAllItems();
        
        
        for (String[] s : initDataSets) {
                      
            if (s[0].equals("states")) {
              ddstate.addItem(s[1]); 
            }
            if (s[0].equals("countries")) {
            
            }
            
        }
        
        ddstate.insertItemAt("", 0);
        ddstate.setSelectedIndex(0);
       
        // add schedule types to stk (schedule type key) LinkedHashMap
        stk.put("", pass);
        dddatetype.removeAllItems();
        dddatetype.addItem("Scheduled Pickup Date");
        dddatetype.addItem("Earliest Pickup Date");
        dddatetype.addItem("Latest Pickup Date");
        dddatetype.addItem("Scheduled Pickup Date, Needs Confirmation");
        dddatetype.addItem("Scheduled Pickup Date, Appointment Confirmed");
        dddatetype.addItem("Scheduled Delivery Date");
        dddatetype.addItem("Scheduled Delivery Date, Needs Confirmation");
        dddatetype.addItem("Scheduled Delivery Date, Appointment Confirmed");
        
        ddtimetype1.removeAllItems();
        ddtimetype1.addItem("Scheduled Pickup Time");
        ddtimetype1.addItem("Earliest Pickup Time");
        ddtimetype1.addItem("Latest Pickup Time");
        ddtimetype1.addItem("Scheduled Delivery Time");
        ddtimetype1.addItem("Earliest Delivery Time");
        ddtimetype1.addItem("Latest Delivery Time");
        
        ddtimetype2.removeAllItems();
        ddtimetype2.addItem("Scheduled Pickup Time");
        ddtimetype2.addItem("Earliest Pickup Time");
        ddtimetype2.addItem("Latest Pickup Time");
        ddtimetype2.addItem("Scheduled Delivery Time");
        ddtimetype2.addItem("Earliest Delivery Time");
        ddtimetype2.addItem("Latest Delivery Time");
        
        ddtime1.removeAllItems();
        ddtime2.removeAllItems();
        String timevar = "";
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 12; m++) {
                timevar = String.format("%02d", (h)) + ":" + String.format("%02d", (m * 5));
                ddtime1.addItem(timevar);
                ddtime2.addItem(timevar);
            }
        }
       
        ddtimezone.removeAllItems();
        ddtimezone.addItem("AST");
        ddtimezone.addItem("EST");
        ddtimezone.addItem("CST");
        ddtimezone.addItem("MST");
        ddtimezone.addItem("PST");
        ddtimezone.addItem("AKST");
        ddtimezone.addItem("HST");
        
       isLoad = false;
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(false);
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
        if (! x.isEmpty()) {
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
        tbkey.requestFocus();
    }
    
    public void setAction(String[] x) {
        String[] m = new String[2];
        if (x[0].equals("0")) {
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
        } else {
                   tbkey.setForeground(Color.red); 
        }
    }
    
    public boolean validateInput(dbaction x) {
       
               
        Map<String,Integer> f = OVData.getTableInfo("cfo_mstr");
        int fc;

        fc = checkLength(f,"cfo_nbr");
        if (tbkey.getText().length() > fc || tbkey.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1032,"1" + "/" + fc));
            tbkey.requestFocus();
            return false;
        }     
         
        fc = checkLength(f,"cfo_rmks");
        if (tbremarks.getText().length() > fc ) {
            bsmf.MainFrame.show(getMessageTag(1032,"0" + "/" + fc));
            tbremarks.requestFocus();
            return false;
        } 
               
        return true;
    }
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
        
        if (arg != null && arg.length > 0) {
            executeTask(dbaction.get,arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
    }
    
    public String[] addRecord(String[] x) {
    // String[] m = addCFOMstr(createRecord());
     String[] m = addCFOTransaction(createDetRecord(), createRecord(), createItemRecord());
         return m;
     }
     
    public String[] updateRecord(String[] x) {
     
     cfo_mstr _x = this.x;
     cfo_mstr _y = createRecord();   
     String[] m = updateCFOMstr(_y);
     
      // change log check
     if (m[0].equals("0")) {
       ArrayList<admData.change_log> c = logChange(tbkey.getText(), this.getClass().getSimpleName(),_x,_y);
       if (! c.isEmpty()) {
           addChangeLog(c);
       } 
     }
         return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteCFOMstr(createRecord()); 
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
        // change log check
        if (m[0].equals("0")) {
            ArrayList<admData.change_log> c = new ArrayList<admData.change_log>();
            c.add(clog(this.x.cfo_nbr(), 
                     this.x.getClass().getName(), 
                     this.getClass().getSimpleName(), 
                     "deletion", 
                     "", 
                     ""));
            if (! c.isEmpty()) {
               addChangeLog(c);
            } 
        }
         return m;
     }
      
    public String[] getRecord(String[] key) {
       x = getCFOMstr(key);
       cfodetlist = getCFODet(key[0]); 
        return x.m();
    }
    
    public cfo_mstr createRecord() { 
        
        cfo_mstr x = new cfo_mstr(null, 
                tbkey.getText(),
                ddcust.getSelectedItem().toString(),
                tbcustfo.getText(),
                tbcustforev.getText(),
                ddservicetype.getSelectedItem().toString(),
                ddequiptype.getSelectedItem().toString(),
                ddvehicle.getSelectedItem().toString(),
                tbtrailer.getText(),
                ddorderstatus.getSelectedItem().toString(),
                "", // delivery status
                dddriver.getSelectedItem().toString(),
                tbdrivercell.getText(),
                ddfotype.getSelectedItem().toString(),
                ddbroker.getSelectedItem().toString(),
                tbbrokercontact.getText(),
                tbbrokercell.getText(),
                ddratetype.getSelectedItem().toString(),
                tbforate.getText(),
                tbmileage.getText(),
                tbdriverrate.getText(),
                String.valueOf(BlueSeerUtils.boolToInt(cbstandard.isSelected())),
                tbtotweight.getText(),
                bsmf.MainFrame.dfdate.format(dcloaddate.getDate()).toString(),
                bsmf.MainFrame.dfdate.format(dcunloaddate.getDate()).toString(),
                String.valueOf(BlueSeerUtils.boolToInt(cbhazmat.isSelected())),
                tbexpenses.getText(),
                tbcharges.getText(),
                tbcost.getText(),
                "", // bol
                tbremarks.getText()
                );
        return x;
    }

    public ArrayList<cfo_det> createDetRecord() {
        ArrayList<cfo_det> list = new ArrayList<cfo_det>();
         //   for (int j = 0; j < orddet.getRowCount(); j++) {               
         for (Map.Entry<String, String[]> z : kvstop.entrySet()) { 
         String[] v = z.getValue();
         cfo_det x = new cfo_det(null, 
                tbkey.getText().toString(),
                v[0],
                v[1],
                v[2],
                v[3],
                v[4],
                v[5],
                v[6],
                v[7],
                v[8],
                v[9],
                v[10],
                v[11],
                v[12],
                v[13],
                v[14],
                v[15],
                v[16],
                v[17],
                v[18],
                v[19].replace(defaultDecimalSeparator, '.'),
                v[20].replace(defaultDecimalSeparator, '.'),
                v[21].replace(defaultDecimalSeparator, '.'),
                v[22],
                v[23],
                v[24],
                v[25],
                v[26],
                v[27],
                v[28],
                v[29]
                );  
                list.add(x);
            }    
           
            
        return list;
    }
     
    public ArrayList<cfo_item> createItemRecord() {
        ArrayList<cfo_item> list = new ArrayList<cfo_item>();
          for (int k = 0; k < orddet.getRowCount(); k++) {                
            for (int j = 0; j < itemdet.getRowCount(); j++) {   
                if (! orddet.getValueAt(k, 0).toString().equals(itemdet.getValueAt(j, 0).toString())) {
                    continue;
                }
                cfo_item x = new cfo_item(null, 
                tbkey.getText().toString(), // nbr
                itemdet.getValueAt(j, 0).toString(), // stopline
                itemdet.getValueAt(j, 1).toString(), // itemline
                itemdet.getValueAt(j, 2).toString(), // item
                itemdet.getValueAt(j, 3).toString(), // desc
                itemdet.getValueAt(j, 4).toString(), // order
                itemdet.getValueAt(j, 5).toString(), // qty
                itemdet.getValueAt(j, 6).toString(), // pallets
                itemdet.getValueAt(j, 7).toString(), // weight
                itemdet.getValueAt(j, 8).toString(), // ref
                itemdet.getValueAt(j, 9).toString() // remarks
                );  
                list.add(x);
            }  
          }
           
            
        return list;
    }
    
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getCFOBrowseUtil(luinput.getText(),0, "cfo_nbr");
        } else {
         luModel = DTData.getCFOBrowseUtil(luinput.getText(),0, "cfo_cust");   
        }
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle(getMessageTag(1001));
        } else {
            ludialog.setTitle(getMessageTag(1002, String.valueOf(luModel.getRowCount())));
        }
        }
        };
        luinput.addActionListener(lual);
        
        luTable.removeMouseListener(luml);
        luml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0) {
                ludialog.dispose();
                initvars(new String[]{target.getValueAt(row,1).toString(), target.getValueAt(row,2).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName()), 
                getClassLabelTag("lblcarrier", this.getClass().getSimpleName())); 
        
    }

    public void updateForm() throws ParseException {
        isLoad = true;
        tbkey.setText(x.cfo_nbr());
        ddcust.setSelectedItem(x.cfo_cust());
        tbcustfo.setText(x.cfo_custfonbr());
        tbcustforev.setText(x.cfo_custfonbrrev());
        cbhazmat.setSelected(BlueSeerUtils.ConvertStringToBool(x.cfo_ishazmat()));
        ddservicetype.setSelectedItem(x.cfo_servicetype());
        ddequiptype.setSelectedItem(x.cfo_equipmenttype());
        ddvehicle.setSelectedItem(x.cfo_truckid());
        tbtrailer.setText(x.cfo_trailernbr());
        ddorderstatus.setSelectedItem(x.cfo_orderstatus());
        dddriver.setSelectedItem(x.cfo_driver());
        tbdrivercell.setText(x.cfo_drivercell());
        ddfotype.setSelectedItem(x.cfo_type());
        ddbroker.setSelectedItem(x.cfo_brokerid());
        tbbrokercontact.setText(x.cfo_brokercontact());
        tbbrokercell.setText(x.cfo_brokercell());
        ddratetype.setSelectedItem(x.cfo_ratetype());
        tbforate.setText(x.cfo_rate());
        tbmileage.setText(x.cfo_mileage());
        tbdriverrate.setText(x.cfo_driverrate());
        cbstandard.setSelected(BlueSeerUtils.ConvertStringToBool(x.cfo_driverstd()));
        tbtotweight.setText(x.cfo_weight());
        dcloaddate.setDate(bsmf.MainFrame.dfdate.parse(x.cfo_loaddate()));
        dcunloaddate.setDate(bsmf.MainFrame.dfdate.parse(x.cfo_unloaddate()));
        tbexpenses.setText(x.cfo_miscexpense());
        tbcharges.setText(x.cfo_misccharges());
        tbcost.setText(x.cfo_cost());
        
        // now detail
        kvstop.clear();
        for (cfo_det cfod : cfodetlist) {
            
            // det table first
            myorddetmodel.addRow(new Object[]{
            cfod.cfod_stopline(), 
            cfod.cfod_type(), 
            cfod.cfod_date(),
            cfod.cfod_name(), 
            cfod.cfod_line1(), 
            cfod.cfod_city(), 
            cfod.cfod_state(),
            cfod.cfod_zip()
            });
            
            // kvstop map
            String[] v = new String[]{
                        cfod.cfod_stopline(),
                        cfod.cfod_seq(),
                        cfod.cfod_type(),
                        cfod.cfod_code(), 
                        cfod.cfod_name(), 
                        cfod.cfod_line1(),
                        cfod.cfod_line2(), 
                        cfod.cfod_line3(),
                        cfod.cfod_city(), 
                        cfod.cfod_state(),
                        cfod.cfod_zip(), 
                        cfod.cfod_country(),
                        cfod.cfod_phone(),
                        cfod.cfod_email(),
                        cfod.cfod_contact(),
                        cfod.cfod_misc(), 
                        cfod.cfod_rmks(),
                        cfod.cfod_reference(), 
                        cfod.cfod_ordnum(), 
                        cfod.cfod_weight(), 
                        cfod.cfod_pallet(), 
                        cfod.cfod_ladingqty(),
                        cfod.cfod_hazmat(), 
                        cfod.cfod_datetype(), 
                        cfod.cfod_date(), 
                        cfod.cfod_timetype1(),
                        cfod.cfod_time1(), 
                        cfod.cfod_timetype2(), 
                        cfod.cfod_time2(),
                        cfod.cfod_timezone()};
            kvstop.put(v[0], v);
            
            // now dropdown sequence
            ddstopsequence.addItem("STOP: " + cfod.cfod_stopline());
        }
        isLoad = false;
        if (ddstopsequence.getItemCount() >= 1) {
            ddstopsequence.setSelectedIndex(0);
        }
        setAction(x.m());
    }
    
    
    // misc
        
    public void getStatus(String nbr) {
        
        
    }  
    
    public void sumweight() {
        double qty = 0;
        for (String[] x : tablelist) {
            qty = qty + Double.valueOf(x[15]); 
        }
        tbtotweight.setText(String.valueOf(qty));
    }
        
    public void sumTotal() {
        double dol = 0;
        if (! tbforate.getText().isBlank() && ! tbcharges.getText().isBlank()) {
        dol = Double.valueOf(tbforate.getText()) * Double.valueOf(tbcharges.getText());
        tbcost.setText(String.valueOf(dol));
        }
    }
        
    public Integer getmaxline() {
        int max = 0;
        int current = 0;
        for (int j = 0; j < orddet.getRowCount(); j++) {
            current = Integer.valueOf(orddet.getValueAt(j, 0).toString()); 
            if (current > max) {
                max = current;
            }
         }
        return max;
    }
      
    public void shipperChangeEvent(String mykey) {
            
          //initialize weight and unites
          
     
           try {

             Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
                res = st.executeQuery("select * from ship_mstr inner join cm_mstr on cm_code = sh_cust inner join cms_det on cms_code = cm_code and cms_shipto = sh_ship " +
                        " inner join wh_mstr on wh_id = sh_wh " +
                        " where sh_id = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    tbname.setText(res.getString("cms_name"));
                    tbaddr1.setText(res.getString("cms_line1"));
                    tbaddr2.setText(res.getString("cms_line2"));
                    tbcity.setText(res.getString("cms_city"));
                    dcdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("sh_shipdate")));
                    tbzip.setText(res.getString("cms_zip"));
                    ddstate.setSelectedItem(res.getString("cms_state"));
                 
                }
                     
            
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
     
    
    }
    
    public void clearStopFields() {
        ddstoptype.setSelectedIndex(0); 
        if (ddshipto.getItemCount() > 0) {
          ddshipto.setSelectedIndex(0); 
        }
        tbname.setText(""); 
        tbaddr1.setText(""); 
        tbaddr2.setText(""); 
        tbcity.setText(""); 
        if (ddstate.getItemCount() > 0) {
          ddstate.setSelectedIndex(0); 
        }
        
        tbzip.setText(""); 
        
        if (ddcountry.getItemCount() > 0) {
          ddcountry.setSelectedIndex(0); 
        }
        tbphone.setText(""); 
        tbemail.setText(""); 
        tbcontact.setText(""); 
        tbmisc.setText(""); 
        tbremarks.setText(""); 
        if (ddtime1.getItemCount() > 0) {
          ddtime1.setSelectedIndex(0); 
        }
        if (ddtime2.getItemCount() > 0) {
          ddtime2.setSelectedIndex(0); 
        }
        if (ddtimetype1.getItemCount() > 0) {
          ddtimetype1.setSelectedIndex(0); 
        }
        if (ddtimetype2.getItemCount() > 0) {
          ddtimetype2.setSelectedIndex(0); 
        }
    }
    
    public void setStopState(boolean state) {
        
        // ddstopsequence.setEnabled(state);
       
        
        ddshipto.setEnabled(state);
        tbname.setEnabled(state);
        tbcity.setEnabled(state);
        tbzip.setEnabled(state);
        tbaddr1.setEnabled(state);
        tbaddr2.setEnabled(state);
        ddstate.setEnabled(state);
        ddcountry.setEnabled(state);
        tbmisc.setEnabled(state);
        tbcontact.setEnabled(state);
        tbemail.setEnabled(state);
        tbphone.setEnabled(state);
        tbremarks.setEnabled(state);
        
        btclearstop.setEnabled(state);
        btaddstop.setEnabled(state);
        btupdatestop.setEnabled(state);
        btdeletestop.setEnabled(state);
        
                
        dddatetype.setEnabled(state);
        dcdate.setEnabled(state);
        ddtimetype1.setEnabled(state);
        ddtime1.setEnabled(state);
        ddtimetype2.setEnabled(state);
        ddtime2.setEnabled(state);
        ddtimezone.setEnabled(state);
        
        tbstopitem.setEnabled(state);
        tbstopqty.setEnabled(state);
        tbstoppallets.setEnabled(state);
        tbstopitemdesc.setEnabled(state);
        tbstopweight.setEnabled(state);
        tbstopref.setEnabled(state);
        
        btdeleteitem.setEnabled(state);
        btadditem.setEnabled(state);
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelMain = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        tbkey = new javax.swing.JTextField();
        btnew = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        btprint = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        ddvehicle = new javax.swing.JComboBox();
        jLabel101 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        ddequiptype = new javax.swing.JComboBox();
        ddservicetype = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        tbtrailer = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        tbcustforev = new javax.swing.JTextField();
        tbforate = new javax.swing.JTextField();
        tbcustfo = new javax.swing.JTextField();
        ddcust = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tbdrivercell = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        ddfotype = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        ddbroker = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        tbbrokercontact = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        tbbrokercell = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        ddratetype = new javax.swing.JComboBox<>();
        jLabel31 = new javax.swing.JLabel();
        tbdriverrate = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        tbtotweight = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        dcloaddate = new com.toedter.calendar.JDateChooser();
        dcunloaddate = new com.toedter.calendar.JDateChooser();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        cbhazmat = new javax.swing.JCheckBox();
        ddorderstatus = new javax.swing.JComboBox();
        jLabel36 = new javax.swing.JLabel();
        tbmileage = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        cbstandard = new javax.swing.JCheckBox();
        dddriver = new javax.swing.JComboBox<>();
        tbcharges = new javax.swing.JTextField();
        tbcost = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        orddet = new javax.swing.JTable();
        btclear = new javax.swing.JButton();
        tbexpenses = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        btdelete = new javax.swing.JButton();
        jPanelLocation = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btaddstop = new javax.swing.JButton();
        btupdatestop = new javax.swing.JButton();
        btdeletestop = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        ddstate = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        tbremarks = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        tbaddr2 = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        tbphone = new javax.swing.JTextField();
        jLabel93 = new javax.swing.JLabel();
        tbaddr1 = new javax.swing.JTextField();
        jLabel97 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        ddcountry = new javax.swing.JComboBox();
        tbmisc = new javax.swing.JTextField();
        jLabel91 = new javax.swing.JLabel();
        tbcity = new javax.swing.JTextField();
        jLabel94 = new javax.swing.JLabel();
        tbname = new javax.swing.JTextField();
        jLabel89 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        tbemail = new javax.swing.JTextField();
        tbzip = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        tbcontact = new javax.swing.JTextField();
        ddstoptype = new javax.swing.JComboBox<>();
        jPanel16 = new javax.swing.JPanel();
        dcdate = new com.toedter.calendar.JDateChooser();
        dddatetype = new javax.swing.JComboBox<>();
        ddtime1 = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        ddtimezone = new javax.swing.JComboBox<>();
        ddtime2 = new javax.swing.JComboBox<>();
        ddtimetype1 = new javax.swing.JComboBox<>();
        ddtimetype2 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        ddshipto = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel95 = new javax.swing.JLabel();
        tbstopqty = new javax.swing.JTextField();
        tbstopitem = new javax.swing.JTextField();
        tbstoppallets = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        tbstopitemdesc = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        tbstopweight = new javax.swing.JTextField();
        tbstopref = new javax.swing.JTextField();
        jLabel84 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        itemdet = new javax.swing.JTable();
        btadditem = new javax.swing.JButton();
        btdeleteitem = new javax.swing.JButton();
        ddstopsequence = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        btnewstop = new javax.swing.JButton();
        btclearstop = new javax.swing.JButton();

        jLabel4.setText("jLabel4");

        jTextField1.setText("jTextField1");

        jLabel3.setText("jLabel3");

        jLabel10.setText("jLabel10");

        setBackground(new java.awt.Color(0, 102, 204));

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });
        add(jTabbedPane1);

        jPanelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Freight Order Maintenance"));
        jPanelMain.setName("panelmain"); // NOI18N

        jLabel76.setText("Key");
        jLabel76.setName("lblid"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        btprint.setText("Print");
        btprint.setName("btprint"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        ddvehicle.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "test1", "test2", "test3" }));

        jLabel101.setText("Truck ID");
        jLabel101.setName("lblcarrier"); // NOI18N

        jLabel85.setText("EquipType");
        jLabel85.setName("lblequipmenttype"); // NOI18N

        ddequiptype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "test1", "test2", "test3" }));
        ddequiptype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddequiptypeActionPerformed(evt);
            }
        });

        ddservicetype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "test1", "test2", "test3" }));

        jLabel9.setText("Service");

        jLabel15.setText("Driver");

        jLabel16.setText("Trailer");

        jLabel77.setText("Client Order");
        jLabel77.setName("lblshipper"); // NOI18N

        jLabel23.setText("Rate");

        tbcustforev.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbcustforevFocusLost(evt);
            }
        });

        tbforate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbforateFocusLost(evt);
            }
        });

        ddcust.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "test1", "test2" }));

        jLabel5.setText("Client");

        jLabel6.setText("Revision");

        tbdrivercell.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbdrivercellFocusLost(evt);
            }
        });

        jLabel7.setText("Cell#");

        ddfotype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Spot", "Broker" }));

        jLabel24.setText("Type");

        ddbroker.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "test1", "test2" }));

        jLabel28.setText("Broker");

        jLabel29.setText("Broker Contact");

        jLabel30.setText("Broker Cell#");

        ddratetype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Flat Rate", "Mileage Rate" }));

        jLabel31.setText("Rate Type");

        tbdriverrate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbdriverrateFocusLost(evt);
            }
        });

        jLabel32.setText("Mileage");

        tbtotweight.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbtotweightFocusLost(evt);
            }
        });

        jLabel33.setText("Weight");

        dcloaddate.setDateFormatString("yyyy-MM-dd");

        dcunloaddate.setDateFormatString("yyyy-MM-dd");

        jLabel34.setText("Load Date");

        jLabel35.setText("Unload Date");

        cbhazmat.setText("Hazmat");

        ddorderstatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Open", "Quoted", "Tendered", "Accepted", "Declined", "Cancelled", "InTransit", "Delivered", "Close" }));

        jLabel36.setText("Status");

        tbmileage.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbmileageFocusLost(evt);
            }
        });

        jLabel37.setText("Driver Rate");

        cbstandard.setText("Standard");

        dddriver.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "test1", "test2" }));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel77, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel85, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel101, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbcustforev, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(ddequiptype, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(ddservicetype, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tbcustfo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                                .addComponent(ddvehicle, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel7)
                            .addComponent(jLabel24)
                            .addComponent(jLabel28)
                            .addComponent(jLabel29)
                            .addComponent(jLabel30)
                            .addComponent(jLabel36))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbbrokercell, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(ddbroker, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(ddfotype, javax.swing.GroupLayout.Alignment.LEADING, 0, 131, Short.MAX_VALUE)
                                    .addComponent(tbdrivercell, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ddorderstatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(tbbrokercontact, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(dddriver, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(jLabel33)
                            .addComponent(jLabel34))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcunloaddate, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcloaddate, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ddratetype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tbforate, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                    .addComponent(tbdriverrate, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                    .addComponent(tbtotweight, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                    .addComponent(tbmileage, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbstandard))))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(99, 99, 99)
                        .addComponent(cbhazmat)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(ddorderstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36)
                    .addComponent(ddratetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcustfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel77)
                            .addComponent(jLabel15)
                            .addComponent(tbforate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23)
                            .addComponent(dddriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcustforev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(tbdrivercell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(tbmileage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddservicetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(ddfotype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24)
                            .addComponent(tbdriverrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37)
                            .addComponent(cbstandard))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddequiptype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel85)
                            .addComponent(ddbroker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28)
                            .addComponent(tbtotweight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddvehicle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel101)
                            .addComponent(tbbrokercontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29)
                            .addComponent(jLabel34)))
                    .addComponent(dcloaddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbtrailer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbbrokercell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel30)
                        .addComponent(jLabel35))
                    .addComponent(jLabel16)
                    .addComponent(dcunloaddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbhazmat)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        tbcharges.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbchargesFocusLost(evt);
            }
        });

        jLabel13.setText("Charges");

        jLabel14.setText("Net Price");

        btcommit.setText("Commit");
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        orddet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        orddet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                orddetMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(orddet);

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        tbexpenses.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbexpensesFocusLost(evt);
            }
        });

        jLabel38.setText("Expenses");

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(jLabel76)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear))
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane8)))
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addComponent(btcommit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbexpenses, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jLabel13)
                        .addGap(3, 3, 3)
                        .addComponent(tbcharges, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbcost, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(79, 79, 79)
                        .addComponent(btprint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelete)))
                .addContainerGap())
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelMainLayout.createSequentialGroup()
                            .addGap(3, 3, 3)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel76)
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnew)
                            .addComponent(btclear)))
                    .addComponent(btlookup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btprint)
                        .addComponent(tbcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14))
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btupdate)
                        .addComponent(btadd))
                    .addComponent(btdelete)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbcharges, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)
                        .addComponent(tbexpenses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel38)
                        .addComponent(btcommit))))
        );

        add(jPanelMain);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btaddstop.setText("Add Stop");
        btaddstop.setName("btaddstop"); // NOI18N
        btaddstop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddstopActionPerformed(evt);
            }
        });

        btupdatestop.setText("Update Stop");

        btdeletestop.setText("Delete Stop");
        btdeletestop.setName("btdeleteitem"); // NOI18N
        btdeletestop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeletestopActionPerformed(evt);
            }
        });

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Address"));

        jLabel18.setText("Country");

        jLabel88.setText("Contact");
        jLabel88.setName("lblcontact"); // NOI18N

        jLabel96.setText("Email");
        jLabel96.setName("lblemail"); // NOI18N

        jLabel86.setText("Misc");
        jLabel86.setName("lblmisc"); // NOI18N

        jLabel93.setText("City");
        jLabel93.setName("lblcity"); // NOI18N

        jLabel97.setText("Remarks");
        jLabel97.setName("lblremarks"); // NOI18N

        jLabel17.setText("Location");

        jLabel91.setText("Addr1");
        jLabel91.setName("lbladdr1"); // NOI18N

        jLabel94.setText("State");
        jLabel94.setName("lblstate"); // NOI18N

        jLabel89.setText("Phone");
        jLabel89.setName("lblphone"); // NOI18N

        jLabel82.setText("Name");
        jLabel82.setName("lblname"); // NOI18N

        jLabel11.setText("Zip");
        jLabel11.setName("lblzip"); // NOI18N

        jLabel90.setText("Addr2");
        jLabel90.setName("lbladdr2"); // NOI18N

        ddstoptype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Load", "Unload Complete", "Unload Partial" }));

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("Scheduling"));

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel26.setText("Date");

        jLabel27.setText("Type");

        jLabel2.setText("TimeZone");

        jLabel21.setText("Time Event 1");

        jLabel25.setText("Time Event 2");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                            .addComponent(jLabel27)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel16Layout.createSequentialGroup()
                                    .addComponent(jLabel26)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(dddatetype, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddtimezone, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel21)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(ddtimetype2, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddtimetype1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ddtime2, 0, 71, Short.MAX_VALUE)
                                    .addComponent(ddtime1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dddatetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtimezone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtime1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddtimetype1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel25)
                .addGap(2, 2, 2)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtime2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddtimetype2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel12.setText("Stop Type");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel82)
                    .addComponent(jLabel91)
                    .addComponent(jLabel90)
                    .addComponent(jLabel93)
                    .addComponent(jLabel94)
                    .addComponent(jLabel97)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addGap(4, 4, 4)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbremarks)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(tbaddr2, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(tbaddr1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                                            .addComponent(tbname, javax.swing.GroupLayout.Alignment.LEADING)))
                                    .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(43, 43, 43)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel89)
                                    .addComponent(jLabel96)
                                    .addComponent(jLabel88)
                                    .addComponent(jLabel86)
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbmisc, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbcontact, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                                    .addComponent(tbemail)
                                    .addComponent(ddstoptype, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(ddstoptype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel82))
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel96)))
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(13, 13, 13)
                                        .addComponent(jLabel91))
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(tbaddr1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel90))
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(7, 7, 7)
                                        .addComponent(tbaddr2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(7, 7, 7)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel14Layout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addComponent(jLabel93))
                                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbcontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel88))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel89))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbmisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel86))))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel94))
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11)
                                .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18)))
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbremarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel97))
                .addContainerGap())
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Location Item/Order Info"));

        jLabel95.setText("Weight");
        jLabel95.setName("lblref"); // NOI18N

        jLabel8.setText("Desc");

        jLabel83.setText("Item");
        jLabel83.setName("lblref"); // NOI18N

        jLabel19.setText("Pallets");

        jLabel20.setText("Quantity");

        jLabel84.setText("Reference");
        jLabel84.setName("lblref"); // NOI18N

        itemdet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(itemdet);

        btadditem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btdeleteitem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel83)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbstopitem, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                            .addComponent(tbstopitemdesc))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel95)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbstopweight, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbstoppallets, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel84, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(tbstopref, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(127, 127, 127))
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(tbstopqty, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btdeleteitem, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btadditem, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbstopitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel83)
                    .addComponent(tbstopref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84)
                    .addComponent(tbstoppallets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbstopitemdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(tbstopweight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel95)
                            .addComponent(tbstopqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btadditem, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btdeleteitem, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        ddstopsequence.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ddstopsequence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddstopsequenceActionPerformed(evt);
            }
        });

        jLabel22.setText("Stop Sequence");

        btnewstop.setText("New Stop");
        btnewstop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewstopActionPerformed(evt);
            }
        });

        btclearstop.setText("Clear");
        btclearstop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearstopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btaddstop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btupdatestop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdeletestop)
                .addContainerGap())
            .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddstopsequence, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnewstop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btclearstop)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddstopsequence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(btnewstop)
                    .addComponent(btclearstop))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdeletestop)
                    .addComponent(btaddstop)
                    .addComponent(btupdatestop))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelLocationLayout = new javax.swing.GroupLayout(jPanelLocation);
        jPanelLocation.setLayout(jPanelLocationLayout);
        jPanelLocationLayout.setHorizontalGroup(
            jPanelLocationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLocationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelLocationLayout.setVerticalGroup(
            jPanelLocationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLocationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        add(jPanelLocation);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
     newAction("cfo");
    }//GEN-LAST:event_btnewActionPerformed

    private void btaddstopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddstopActionPerformed
        
        String datestr = "0000-00-00";
        if (dcdate.getDate() != null) {
            datestr = bsmf.MainFrame.dfdate.format(dcdate.getDate()).toString();
        }
        
        String shipper = "";
        
        String shiptocode = "";
        if ( ddshipto.getSelectedItem() != null) {
            shiptocode = ddshipto.getSelectedItem().toString();
        }
      
        String country = "";
        if ( ddcountry.getSelectedItem() != null) {
            country = ddcountry.getSelectedItem().toString();
        }
       
         Enumeration<TableColumn> en = orddet.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     tc.setCellRenderer(new SomeRenderer()); 
                 }
        
       
       // line = getmaxline();
       // line++;
            
            
        myorddetmodel.addRow(new Object[]{
            currentstopline, 
            ddstoptype.getSelectedItem().toString(), 
            datestr,
            tbname.getText(), 
            tbaddr1.getText(), 
            tbcity.getText(), 
            ddstate.getSelectedItem().toString(),
            tbzip.getText()
         });
        
        String[] stoparray = new String[]{String.valueOf(currentstopline), 
            String.valueOf(currentstopline), 
            ddstoptype.getSelectedItem().toString(), 
            shiptocode,
            tbname.getText(), 
            tbaddr1.getText(), 
            tbaddr2.getText(), 
            "", // line3 
            tbcity.getText(), 
            ddstate.getSelectedItem().toString(),
            tbzip.getText(),
            country,
            tbphone.getText(),
            tbemail.getText(),
            tbcontact.getText(),
            tbmisc.getText(),
            tbremarks.getText(),
            "", // ref
            "", // ordnum
            "", // weight
            "", // pallets
            "", // ladingqty
            "", // hazmat
            dddatetype.getSelectedItem().toString(),
            datestr,
            ddtimetype1.getSelectedItem().toString(),
            ddtime1.getSelectedItem().toString(),
            ddtimetype2.getSelectedItem().toString(),
            ddtime2.getSelectedItem().toString(),
            ddtimezone.getSelectedItem().toString()
         };
        kvstop.put(String.valueOf(currentstopline), stoparray);
        
         

        isLoad = true;
        ddstopsequence.addItem("STOP: " + currentstopline);
        isLoad = false;
        
        sumweight();
        clearStopFields();       
    }//GEN-LAST:event_btaddstopActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
         if (! validateInput(dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.add, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void btdeletestopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeletestopActionPerformed
        
        int[] rows = orddet.getSelectedRows();
        ArrayList<String[]> newlist = new ArrayList<String[]>();
        for (int i : rows) {
            if (orddet.getValueAt(i, 2).toString().equals("LD")) {
                bsmf.MainFrame.show(getMessageTag(1046));
                return;
            } else {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) orddet.getModel()).removeRow(i);
            
            for (String[] x : tablelist) {
                if (Integer.valueOf(x[0]) != i) {
                    newlist.add(x);
                }
            }
            
            }
        }
        tablelist = newlist;
       
         
         
    }//GEN-LAST:event_btdeletestopActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       if (! validateInput(dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText()});  
    }//GEN-LAST:event_btupdateActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
       OVData.printPurchaseOrder(tbkey.getText(), false);
    }//GEN-LAST:event_btprintActionPerformed

    private void orddetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orddetMouseClicked
       /*
        int row = orddet.rowAtPoint(evt.getPoint());
        int col = orddet.columnAtPoint(evt.getPoint());
        String[] v = null;
        for (String[] x : tablelist) {
          //  System.out.println("HERE: " + x[0] + "/" + orddet.getValueAt(row, 0).toString() );
            if (x[0].equals(orddet.getValueAt(row, 0).toString())) {
                v = x;
            }
        }
        
        
        tbname.setText(v[5]);
        tbaddr1.setText(v[6]);
        tbaddr2.setText(v[7]);
        tbcity.setText(v[8]);
        ddstate.setSelectedItem(v[9]);
        tbzip.setText(v[10]);
        
        tbcontact.setText(v[11]);
        tbphone.setText(v[12]);
        tbemail.setText(v[13]);
        tbforate.setText(v[14]);
       // tbweight.setText(orddet.getValueAt(row, 15).toString());
        if (v[16].isEmpty() || v[16].equals("0000-00-00")) {
         dcdate.setDate(null);   
        } else {
         dcdate.setDate(Date.valueOf(v[16]));   
        }
        if (v[18].isEmpty() || v[18].equals("0000-00-00") ) {
         dcdate.setDate(null);   
        } else {
         dcdate.setDate(Date.valueOf(v[18]));   
        }
        ddtime.setSelectedItem(v[19]);
        */
    }//GEN-LAST:event_orddetMouseClicked

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void tbchargesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbchargesFocusLost
        if (! tbcharges.getText().isEmpty()) {
        String x = BlueSeerUtils.bsformat("", tbcharges.getText(), "0");
        if (x.equals("error")) {
            tbcharges.setText("");
            tbcharges.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbcharges.requestFocus();
        } else {
            tbcharges.setText(x);
            tbcharges.setBackground(Color.white);
        }
        sumTotal();
        }
    }//GEN-LAST:event_tbchargesFocusLost

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
        java.util.Date now = new java.util.Date();
        for (int j = 0; j < orddet.getRowCount(); j++) {
            if (! orddet.getValueAt(j, 3).toString().isBlank()) {
            String[] message = confirmShipperTransaction("order", orddet.getValueAt(j, 3).toString(), now);
            updateFreightOrderStatus(tbkey.getText(),"Close");
            bsmf.MainFrame.show("committed shipper: " + orddet.getValueAt(j, 3).toString());
            }
         } 
        
    }//GEN-LAST:event_btcommitActionPerformed

    private void tbcustforevFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbcustforevFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbcustforevFocusLost

    private void tbforateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbforateFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbforateFocusLost

    private void tbdrivercellFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbdrivercellFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbdrivercellFocusLost

    private void tbdriverrateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbdriverrateFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbdriverrateFocusLost

    private void tbtotweightFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbtotweightFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbtotweightFocusLost

    private void tbmileageFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbmileageFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbmileageFocusLost

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void tbexpensesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbexpensesFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbexpensesFocusLost

    private void ddequiptypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddequiptypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ddequiptypeActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
         if (! validateInput(dbaction.delete)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.delete, new String[]{tbkey.getText()});  
    }//GEN-LAST:event_btdeleteActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
/*
        EDData.addEDIAttributeRecord(tbsndgs.getText(), tbrcvgs.getText(), dddoc.getSelectedItem().toString(), ddattributekey.getSelectedItem().toString(), tbattributevalue.getText());
        getAttributes(tbsndgs.getText(), tbrcvgs.getText(), dddoc.getSelectedItem().toString());
        ddattributekey.setSelectedIndex(0);
        tbattributevalue.setText("");
        */
    }//GEN-LAST:event_btadditemActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        boolean proceed = true;
/*
        if (listAttributes.isSelectionEmpty()) {
            proceed = false;
            bsmf.MainFrame.show(getMessageTag(1029));
        } else {
            proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        }
        if (proceed) {
            String[] z = listAttributes.getSelectedValue().toString().split(":");
            try {

                Connection con = null;
                if (ds != null) {
                    con = ds.getConnection();
                } else {
                    con = DriverManager.getConnection(url + db, user, pass);
                }
                Statement st = con.createStatement();
                try {

                    int i = st.executeUpdate("delete from edi_attr where exa_sndid = " + "'" + tbsndgs.getText() + "'" +
                        " and exa_rcvid = " + "'" + tbrcvgs.getText() + "'" +
                        " and exa_doc = " + "'" + dddoc.getSelectedItem().toString() + "'" +
                        " and exa_key = " + "'" + z[0].toString() + "'" +
                        ";");
                    if (i > 0) {
                        bsmf.MainFrame.show(getMessageTag(1031,listAttributes.getSelectedValue().toString()));
                        getAttributes(tbsndgs.getText(), tbrcvgs.getText(), dddoc.getSelectedItem().toString());
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                    bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
                } finally {
                    if (st != null) {
                        st.close();
                    }
                    con.close();
                }
            } catch (Exception e) {
                MainFrame.bslog(e);
            }
        }
*/
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void btnewstopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewstopActionPerformed
        currentstopline = orddet.getRowCount() + 1;
        clearStopFields();
        setStopState(true);
    }//GEN-LAST:event_btnewstopActionPerformed

    private void btclearstopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearstopActionPerformed
        clearStopFields();
    }//GEN-LAST:event_btclearstopActionPerformed

    private void ddstopsequenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddstopsequenceActionPerformed
        if (! isLoad && ddstopsequence.getItemCount() > 0 && ddstopsequence.getSelectedItem() != null && ! ddstopsequence.getSelectedItem().toString().isBlank()) {
           int stopnumber = Integer.valueOf(ddstopsequence.getSelectedItem().toString().substring(6));
           //for (int j = 0; j < orddet.getRowCount(); j++) {
            String[] v = kvstop.get(String.valueOf(stopnumber));
                
                ddstoptype.setSelectedItem(v[2]); 
                ddshipto.setSelectedItem(v[3]); 
                tbname.setText(v[4]); 
                tbaddr1.setText(v[5]); 
                tbaddr2.setText(v[6]);  
                tbcity.setText(v[8]);  
                ddstate.setSelectedItem(v[9]);
                tbzip.setText(v[10]); 
                ddcountry.setSelectedItem(v[11]);
                tbphone.setText(v[12]);
                tbemail.setText(v[13]);
                tbcontact.setText(v[14]);
                tbmisc.setText(v[15]); 
                tbremarks.setText(v[16]);
           
          setStopState(true); 
        }
        
         if (! isLoad && ddstopsequence.getItemCount() > 0 && ddstopsequence.getSelectedItem() != null && ddstopsequence.getSelectedItem().toString().isBlank()) {
          clearStopFields();
          setStopState(false);
         }
        
        
    }//GEN-LAST:event_ddstopsequenceActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
       if (! isLoad && jTabbedPane1.getSelectedIndex() == 1) {
            if (orddet.getRowCount() == 0) {
                setStopState(false);
            }
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btaddstop;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btclearstop;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btdeletestop;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btnewstop;
    private javax.swing.JButton btprint;
    private javax.swing.JButton btupdate;
    private javax.swing.JButton btupdatestop;
    private javax.swing.JCheckBox cbhazmat;
    private javax.swing.JCheckBox cbstandard;
    private com.toedter.calendar.JDateChooser dcdate;
    private com.toedter.calendar.JDateChooser dcloaddate;
    private com.toedter.calendar.JDateChooser dcunloaddate;
    private javax.swing.JComboBox<String> ddbroker;
    private javax.swing.JComboBox ddcountry;
    private javax.swing.JComboBox<String> ddcust;
    private javax.swing.JComboBox<String> dddatetype;
    private javax.swing.JComboBox<String> dddriver;
    private javax.swing.JComboBox ddequiptype;
    private javax.swing.JComboBox<String> ddfotype;
    private javax.swing.JComboBox ddorderstatus;
    private javax.swing.JComboBox<String> ddratetype;
    private javax.swing.JComboBox<String> ddservicetype;
    private javax.swing.JComboBox<String> ddshipto;
    private javax.swing.JComboBox ddstate;
    private javax.swing.JComboBox<String> ddstopsequence;
    private javax.swing.JComboBox<String> ddstoptype;
    private javax.swing.JComboBox<String> ddtime1;
    private javax.swing.JComboBox<String> ddtime2;
    private javax.swing.JComboBox<String> ddtimetype1;
    private javax.swing.JComboBox<String> ddtimetype2;
    private javax.swing.JComboBox<String> ddtimezone;
    private javax.swing.JComboBox ddvehicle;
    private javax.swing.JTable itemdet;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelLocation;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable orddet;
    private javax.swing.JTextField tbaddr1;
    private javax.swing.JTextField tbaddr2;
    private javax.swing.JTextField tbbrokercell;
    private javax.swing.JTextField tbbrokercontact;
    private javax.swing.JTextField tbcharges;
    private javax.swing.JTextField tbcity;
    private javax.swing.JTextField tbcontact;
    private javax.swing.JTextField tbcost;
    private javax.swing.JTextField tbcustfo;
    private javax.swing.JTextField tbcustforev;
    private javax.swing.JTextField tbdrivercell;
    private javax.swing.JTextField tbdriverrate;
    private javax.swing.JTextField tbemail;
    private javax.swing.JTextField tbexpenses;
    private javax.swing.JTextField tbforate;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbmileage;
    private javax.swing.JTextField tbmisc;
    private javax.swing.JTextField tbname;
    private javax.swing.JTextField tbphone;
    private javax.swing.JTextField tbremarks;
    private javax.swing.JTextField tbstopitem;
    private javax.swing.JTextField tbstopitemdesc;
    private javax.swing.JTextField tbstoppallets;
    private javax.swing.JTextField tbstopqty;
    private javax.swing.JTextField tbstopref;
    private javax.swing.JTextField tbstopweight;
    private javax.swing.JTextField tbtotweight;
    private javax.swing.JTextField tbtrailer;
    private javax.swing.JTextField tbzip;
    // End of variables declaration//GEN-END:variables
}
