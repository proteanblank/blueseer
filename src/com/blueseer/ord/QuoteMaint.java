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
package com.blueseer.ord;

import com.blueseer.fgl.*;
import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.dfdate;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import static com.blueseer.adm.admData.addCodeMstr;
import static com.blueseer.fap.fapData.VouchAndPayTransaction;
import com.blueseer.fap.fapData.ap_mstr;
import com.blueseer.fap.fapData.vod_mstr;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import static com.blueseer.utl.BlueSeerUtils.setDateFormat;
import com.blueseer.utl.DTData;
import com.blueseer.utl.IBlueSeerT;
import com.blueseer.utl.OVData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.blueseer.ctr.cusData;
import static com.blueseer.ord.ordData.addQuoteTransaction;
import static com.blueseer.ord.ordData.deleteQuoteMstr;
import static com.blueseer.ord.ordData.getQuoteDet;
import static com.blueseer.ord.ordData.getQuoteLines;
import static com.blueseer.ord.ordData.getQuoteMstr;
import static com.blueseer.ord.ordData.getQuoteSAC;
import com.blueseer.ord.ordData.quo_det;
import com.blueseer.ord.ordData.quo_mstr;
import com.blueseer.ord.ordData.quo_sac;
import static com.blueseer.ord.ordData.updateQuoteTransaction;
import static com.blueseer.utl.BlueSeerUtils.setDateFormatNull;
import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;


/**
 *
 * @author vaughnte
 */
public class QuoteMaint extends javax.swing.JPanel implements IBlueSeerT {
                
                 // global variable declarations
                boolean isLoad = false;
                double actamt = 0.00;
                int quoteline = 0;
                public static quo_mstr x = null;
                public static ArrayList<quo_det> quodetlist = null;
                public static ArrayList<quo_sac> saclist = null;
                
               
    
    // global datatablemodel declarations       
                
                
                 javax.swing.table.DefaultTableModel detailmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("id"), 
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"),
                getGlobalColumnTag("qty"),
                getGlobalColumnTag("listprice"),
                getGlobalColumnTag("discount"),
                getGlobalColumnTag("netprice"),
                getGlobalColumnTag("uom")
            });
                 
                 javax.swing.table.DefaultTableModel sacmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("type"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("value"), 
                getGlobalColumnTag("amount")
            });
                
    /**
     * Creates new form ShipMaintPanel
     */
    public QuoteMaint() {
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
        tbkey.setText("");
      
        jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", jPanelMain);
        jTabbedPane1.add("SAC/Tax", jPanelCharges);
        
         
        lbvendor.setText("");
        lbmessage.setText("");
        lbmessage.setForeground(Color.blue);
        
        tarmks.setText("");
        tbref.setText("");
        tbitemservice.setText("");
        lblitemdesc.setText("");
        tbqty.setText("");
        tbprice.setText("");
        tbactualamt.setText("0");
        tbactualamt.setBackground(Color.white);
        tbactualamt.setEditable(false);
       
        tbsacdesc.setText("");
        tbsacamt.setText("0");
        
        detailmodel.setRowCount(0);
        detailtable.setModel(detailmodel);
        detailtable.getTableHeader().setReorderingAllowed(false);
        
        sacmodel.setRowCount(0);
        sactable.setModel(sacmodel);
        sactable.getTableHeader().setReorderingAllowed(false);
       
        
        java.util.Date now = new java.util.Date();
        dcquoteexpire.setDate(null);
        dcpricingexpire.setDate(null);
        
       
       dddisccode.removeAllItems();
        ArrayList<String> dc = cusData.getdisclist();
        for (int i = 0; i < dc.size(); i++) {
            dddisccode.addItem(dc.get(i)); 
        }
        dddisccode.insertItemAt("", 0);
        dddisccode.setSelectedIndex(0);
       
        ddtaxcode.removeAllItems();
        ArrayList<String> tc = OVData.gettaxcodelist();
        for (int i = 0; i < tc.size(); i++) {
            ddtaxcode.addItem(tc.get(i)); 
        }
        ddtaxcode.insertItemAt("", 0);
        ddtaxcode.setSelectedIndex(0);
       
        ddpricegroup.removeAllItems();
        ArrayList<String> pg = OVData.getPriceGroupList();
        for (int i = 0; i < pg.size(); i++) {
            ddpricegroup.addItem(pg.get(i)); 
        }
        ddpricegroup.insertItemAt("", 0);
        ddpricegroup.setSelectedIndex(0);
        
        ddcust.removeAllItems();
        ArrayList c = cusData.getcustmstrlist();
        for (int i = 0; i < c.size(); i++) {
            ddcust.addItem(c.get(i));
        }
        ddcust.insertItemAt("", 0);
        ddcust.setSelectedIndex(0);
          
        ddsite.removeAllItems();
        ArrayList mylist = OVData.getSiteList();
        for (int i = 0; i < mylist.size(); i++) {
            ddsite.addItem(mylist.get(i));
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
      
        dduom.removeAllItems();
        ArrayList<String> u = OVData.getUOMList();
        for (int i = 0; i < u.size(); i++) {
            dduom.addItem(u.get(i));
        }
        dduom.insertItemAt("", 0);
        dduom.setSelectedIndex(0);
        
        ddterms.removeAllItems();
        ArrayList<String> terms = cusData.gettermsmstrlist();
        for (int i = 0; i < terms.size(); i++) {
            ddterms.addItem(terms.get(i));
        }
        ddterms.insertItemAt("", 0);
        ddterms.setSelectedIndex(0);
        
        ddcurr.removeAllItems();
        ArrayList<String> curr = fglData.getCurrlist();
        for (int i = 0; i < curr.size(); i++) {
            ddcurr.addItem(curr.get(i));
        }
        ddcurr.insertItemAt("", 0);
        ddcurr.setSelectedItem(OVData.getDefaultCurrency());
        
        ddsactype.removeAllItems();
        ddsactype.addItem("charge");
        ddsactype.addItem("discount");
        ddsactype.addItem("tax");
        ddsactype.setSelectedIndex(0);
        
       isLoad = false;
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
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
                   //lbmessage.setText(getMessageTag(1170));
        } else {
                   tbkey.setForeground(Color.red); 
        }
       
    }
    
    public boolean validateInput(dbaction x) {
        boolean b = true;
                
                
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024, "ID"));
                    tbkey.requestFocus();
                    return b;
                }
                
                if (ddcust.getSelectedItem() == null || ddcust.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024, "Customer"));
                    return b;
                }
                
                if (ddsite.getSelectedItem() == null || ddsite.getSelectedItem().toString().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024, "Site"));
                    return b;
                }
               
               
        return b;
    }
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btclear.setEnabled(true);
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
     String[] m = addQuoteTransaction(createDetRecord(), createRecord(), createSACRecord()); 
     return m;
     }
     
    public String[] updateRecord(String[] x) {
     // first delete any sod_det line records that have been
        // disposed from the current orddet table
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<String> badlines = new ArrayList<String>();
        boolean goodLine = false;
        lines = getQuoteLines(tbkey.getText());
       for (String line : lines) {
          goodLine = false;
          for (int j = 0; j < detailtable.getRowCount(); j++) {
             if (detailtable.getValueAt(j, 1).toString().equals(line)) {
                 goodLine = true;
             }
          }
          if (! goodLine) {
              badlines.add(line);
          }
        }   
     String[] m = updateQuoteTransaction(tbkey.getText(), badlines, createDetRecord(), createRecord(), createSACRecord());
     return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteQuoteMstr(createRecord()); 
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
         return m;
     }
      
    public String[] getRecord(String[] key) {
       x = getQuoteMstr(key); 
       quodetlist = getQuoteDet(key[0]);
       saclist = getQuoteSAC(key[0]);
       return x.m();
    }
    
    public quo_mstr createRecord() {
        java.util.Date now = new java.util.Date();
        quo_mstr x = new quo_mstr(null, 
                tbkey.getText(), 
                ddcust.getSelectedItem().toString(),
                "", // ship
                ddsite.getSelectedItem().toString(),
                setDateFormat(now),
                setDateFormatNull(dcquoteexpire.getDate()),
                setDateFormatNull(dcpricingexpire.getDate()),
                ddstatus.getSelectedItem().toString(),
                tarmks.getText(),
                tbref.getText(),
                ddquotetype.getSelectedItem().toString(),
                ddtaxcode.getSelectedItem().toString(),
                dddisccode.getSelectedItem().toString(),
                ddpricegroup.getSelectedItem().toString(),
                ddcurr.getSelectedItem().toString(),
                "0", // approved
                "", // approver
                "", //varchar
                ddterms.getSelectedItem().toString()        
        ); 
        return x;  
    }
    
    public ArrayList<quo_det> createDetRecord() {
        ArrayList<quo_det> list = new ArrayList<quo_det>();
         for (int j = 0; j < detailtable.getRowCount(); j++) {
             quo_det x = new quo_det(null, 
                detailtable.getValueAt(j, 0).toString(), // key
                detailtable.getValueAt(j, 1).toString(), // line
                detailtable.getValueAt(j, 2).toString(), // item
                BlueSeerUtils.boolToString(OVData.isValidItem(detailtable.getValueAt(j, 2).toString())), // isinventory
                detailtable.getValueAt(j, 3).toString(), // item desc
                "", // price type
                detailtable.getValueAt(j, 5).toString().replace(defaultDecimalSeparator, '.'), // list price
                detailtable.getValueAt(j, 6).toString().replace(defaultDecimalSeparator, '.'), // disc
                detailtable.getValueAt(j, 7).toString().replace(defaultDecimalSeparator, '.'), // netprice
                detailtable.getValueAt(j, 4).toString().replace(defaultDecimalSeparator, '.'), // qty
                detailtable.getValueAt(j, 8).toString().replace(defaultDecimalSeparator, '.')  // uom
                );
        list.add(x);
         }
        return list;   
    }
    
    public ArrayList<quo_sac> createSACRecord() {
         ArrayList<quo_sac> list = new ArrayList<quo_sac>();
         for (int j = 0; j < sactable.getRowCount(); j++) {
             quo_sac x = new quo_sac(null, tbkey.getText().toString(),
                sactable.getValueAt(j, 1).toString(),
                sactable.getValueAt(j, 0).toString(),
                sactable.getValueAt(j, 2).toString(),
                sactable.getValueAt(j, 3).toString().replace(defaultDecimalSeparator, '.'));     
                list.add(x);
         }
        return list;
    }
    
    
    public void lookUpFrameItemDesc() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getItemDescBrowse(luinput.getText(), "it_item");
        } else {
         luModel = DTData.getItemDescBrowse(luinput.getText(), "it_desc");   
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
                tbitemservice.setText(target.getValueAt(row,1).toString());
                lblitemdesc.setText(target.getValueAt(row,2).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getGlobalColumnTag("item"), getGlobalColumnTag("description")); 
        
        
        
    }

    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getQuoteBrowseUtil(luinput.getText(),0, "quo_nbr"); 
        } else {
         luModel = DTData.getQuoteBrowseUtil(luinput.getText(),0, "quo_cust");    
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
                getClassLabelTag("lblcust", this.getClass().getSimpleName())); 
        
        
    }

    public void updateForm() throws ParseException {
        tbkey.setText(x.quo_nbr());
        ddcust.setSelectedItem(x.quo_cust());
        ddsite.setSelectedItem(x.quo_site());
        ddstatus.setSelectedItem(x.quo_status());
        ddquotetype.setSelectedItem(x.quo_type());
        ddpricegroup.setSelectedItem(x.quo_groupcode());
        dddisccode.setSelectedItem(x.quo_disccode());
        dcquoteexpire.setDate(BlueSeerUtils.parseDate(x.quo_expire()));
        dcpricingexpire.setDate(BlueSeerUtils.parseDate(x.quo_priceexpire()));
        ddtaxcode.setSelectedItem(x.quo_taxcode());
        ddterms.setSelectedItem(x.quo_terms());
        ddcurr.setSelectedItem(x.quo_curr());
        tarmks.setText(x.quo_rmks());
        tbref.setText(x.quo_ref());
        if (x.quo_type().equals("volume")) {
        cbvolume.setSelected(true);
        } else {
         cbvolume.setSelected(false);   
        }
        setAction(x.m()); 
        
         // now detail
        
        detailmodel.setRowCount(0);
        for (quo_det quod : quodetlist) {
                    detailmodel.addRow(new Object[]{
                      quod.quod_nbr(),   
                      quod.quod_line(), 
                      quod.quod_item(),
                      quod.quod_desc(),
                      quod.quod_qty(),
                      quod.quod_listprice(),
                      quod.quod_disc(),
                      quod.quod_netprice(),
                      quod.quod_uom()
                  });
                
        }
        quoteline = detailmodel.getRowCount();
        
        // summary charges and discounts
        if (saclist != null) {
            for (quo_sac sos : saclist) {
                sacmodel.addRow(new Object[]{
                          sos.quos_type(), 
                          sos.quos_desc(),
                          sos.quos_amttype(),
                          sos.quos_amt()});
            }
        }
        
        
        summarize();
        
    }
    
    
    // additional functions
    public void setvendorvariables(String vendor) {
        
        try {
     
            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;int i = 0;
            int d = 0;
            String uniqpo = null;
            try {
                res = st.executeQuery("select vd_name, vd_curr, vd_ap_acct, vd_ap_cc, vd_terms, vd_bank from vd_mstr where vd_addr = " + "'" + vendor + "'" + ";");
                while (res.next()) {
                    i++;
                   lbvendor.setText(res.getString("vd_name"));
                   
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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
      
    public void summarize() {
        actamt = 0;
        
        double totalcharges = 0.00;
        double totaldiscounts = 0.00;
        double totaltaxes = 0.00;
      
      for (int j = 0; j < sactable.getRowCount(); j++) {
          if (sactable.getValueAt(j, 0).equals("charge")) {   
          totalcharges += Double.valueOf(sactable.getValueAt(j, 3).toString());
          }
          if (sactable.getValueAt(j, 0).equals("discount")) {   
          totaldiscounts += Double.valueOf(sactable.getValueAt(j, 3).toString());
          }
          if (sactable.getValueAt(j, 0).equals("tax")) {   
          totaltaxes += Double.valueOf(sactable.getValueAt(j, 3).toString());
          }
      }  
      
      
        for (int j = 0; j < detailtable.getRowCount(); j++) {
            actamt += bsParseDouble(detailtable.getValueAt(j, 7).toString().replace(defaultDecimalSeparator, '.')) * 
                    bsParseDouble(detailtable.getValueAt(j, 4).toString().replace(defaultDecimalSeparator, '.'));
        }
        actamt += totalcharges + totaldiscounts + totaltaxes;
        
        tbtottax.setText(currformatDouble(totaltaxes));
        tbtotdisc.setText(currformatDouble(totaldiscounts));
        tbactualamt.setText(currformatDouble(actamt));
        
    }

     
      
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelMain = new javax.swing.JPanel();
        tbkey = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();
        btadditem = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        detailtable = new javax.swing.JTable();
        ddcust = new javax.swing.JComboBox();
        btdeleteitem = new javax.swing.JButton();
        dcquoteexpire = new com.toedter.calendar.JDateChooser();
        jLabel35 = new javax.swing.JLabel();
        tbactualamt = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        tbitemservice = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tbprice = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        lbvendor = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lbmessage = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        dcpricingexpire = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tarmks = new javax.swing.JTextArea();
        ddquotetype = new javax.swing.JComboBox<>();
        ddstatus = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        tbref = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        ddpricegroup = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        dddisccode = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        ddtaxcode = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        tbtotdisc = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        cbvolume = new javax.swing.JCheckBox();
        lblitemdesc = new javax.swing.JLabel();
        btLookUpItemDesc = new javax.swing.JButton();
        btprintquote = new javax.swing.JButton();
        dduom = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        ddterms = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        ddcurr = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        tbtottax = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jPanelCharges = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        sactable = new javax.swing.JTable();
        tbsacamt = new javax.swing.JTextField();
        tbsacdesc = new javax.swing.JTextField();
        percentlabel = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        btsacadd = new javax.swing.JButton();
        btsacdelete = new javax.swing.JButton();
        ddsactype = new javax.swing.JComboBox<>();
        ddsacamttype = new javax.swing.JComboBox<>();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));
        add(jTabbedPane1);

        jPanelMain.setName("panelmain"); // NOI18N

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        jLabel24.setText("Quote Nbr");
        jLabel24.setName("lblid"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel36.setText("Customer");
        jLabel36.setName("lblcust"); // NOI18N

        btadditem.setText("Add Item");
        btadditem.setName("btadditem"); // NOI18N
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        detailtable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane7.setViewportView(detailtable);

        ddcust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcustActionPerformed(evt);
            }
        });

        btdeleteitem.setText("Del Item");
        btdeleteitem.setName("btdeleteitem"); // NOI18N
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        dcquoteexpire.setDateFormatString("yyyy-MM-dd");

        jLabel35.setText("Quote Expire Date");
        jLabel35.setName("lblexpiredate"); // NOI18N

        jLabel28.setText("Total Amount");
        jLabel28.setName("lbltotalamt"); // NOI18N

        jLabel4.setText("Rmks");
        jLabel4.setName("lblremarks"); // NOI18N

        jLabel5.setText("Item/Service");
        jLabel5.setName("lblitem"); // NOI18N

        tbprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbpriceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpriceFocusLost(evt);
            }
        });

        jLabel6.setText("Price");
        jLabel6.setName("lblprice"); // NOI18N

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbqtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel7.setText("Qty");
        jLabel7.setName("lblqty"); // NOI18N

        ddsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsiteActionPerformed(evt);
            }
        });

        jLabel10.setText("Site");
        jLabel10.setName("lblsite"); // NOI18N

        jLabel1.setText("Type");
        jLabel1.setName("lbltype"); // NOI18N

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        dcpricingexpire.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("Pricing Expire Date");

        tarmks.setColumns(20);
        tarmks.setRows(5);
        jScrollPane1.setViewportView(tarmks);

        ddquotetype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "discrete", "volume" }));

        ddstatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "pending", "closed", "expired", "void" }));

        jLabel2.setText("Status");
        jLabel2.setName("lblstatus"); // NOI18N

        jLabel8.setText("Reference");
        jLabel8.setName("lblref"); // NOI18N

        btcommit.setText("Commit");
        btcommit.setName("btcommit"); // NOI18N
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel9.setText("Price Group");
        jLabel9.setName("lblpricegroup"); // NOI18N

        jLabel11.setText("Discount Code");
        jLabel11.setName("lbldiscode"); // NOI18N

        jLabel12.setText("Tax Code");
        jLabel12.setName("lbltaxcode"); // NOI18N

        jLabel13.setText("Discounts");
        jLabel13.setName("lbltotaldisc"); // NOI18N

        cbvolume.setText("Volume Based");
        cbvolume.setName("cbvolume"); // NOI18N

        btLookUpItemDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpItemDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpItemDescActionPerformed(evt);
            }
        });

        btprintquote.setText("Print Quote");
        btprintquote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintquoteActionPerformed(evt);
            }
        });

        jLabel14.setText("UOM");
        jLabel14.setName("lbluom"); // NOI18N

        jLabel15.setText("Terms");
        jLabel15.setName("lblterms"); // NOI18N

        jLabel16.setText("Currency");
        jLabel16.setName("lblcurr"); // NOI18N

        jLabel17.setText("Taxes");
        jLabel17.setName("lbltottaxes"); // NOI18N

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5)
                            .addComponent(jLabel24)
                            .addComponent(jLabel36)
                            .addComponent(jLabel10)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addComponent(jLabel7)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 594, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addComponent(tbitemservice, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btLookUpItemDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblitemdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelMainLayout.createSequentialGroup()
                                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(13, 13, 13)
                                        .addComponent(btnew)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btclear))
                                    .addGroup(jPanelMainLayout.createSequentialGroup()
                                        .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(lbvendor, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cbvolume)
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(48, 48, 48)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(54, 54, 54)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(146, 146, 146)
                                .addComponent(btadditem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdeleteitem))
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(ddterms, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ddstatus, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ddquotetype, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ddsite, javax.swing.GroupLayout.Alignment.LEADING, 0, 121, Short.MAX_VALUE))
                                .addGap(17, 17, 17)
                                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dcquoteexpire, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dcpricingexpire, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 146, Short.MAX_VALUE)
                                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ddpricegroup, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(dddisccode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ddtaxcode, 0, 103, Short.MAX_VALUE)))))
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanelMainLayout.createSequentialGroup()
                            .addComponent(btcommit)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel13)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(tbtotdisc, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, Short.MAX_VALUE)
                            .addComponent(jLabel17)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(tbtottax, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel28)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btprintquote)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btupdate)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btdelete)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btadd))
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 775, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 2, Short.MAX_VALUE))
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnew)
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel24)
                                .addComponent(btclear))
                            .addComponent(btlookup))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel36))
                            .addComponent(lbvendor, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lbmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10)
                        .addComponent(jLabel35))
                    .addComponent(dcquoteexpire, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddpricegroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)))
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcpricingexpire, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddquotetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1)
                                .addComponent(jLabel3))))
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(dddisccode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))))
                .addGap(7, 7, 7)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(ddtaxcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddterms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(4, 4, 4)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbvolume)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblitemdesc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbitemservice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5))
                            .addComponent(btLookUpItemDesc))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadditem)
                    .addComponent(btdeleteitem)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28)
                    .addComponent(btcommit)
                    .addComponent(btdelete)
                    .addComponent(btupdate)
                    .addComponent(tbtotdisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(btprintquote)
                    .addComponent(tbtottax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(18, 18, 18))
        );

        add(jPanelMain);

        jPanelCharges.setBorder(javax.swing.BorderFactory.createTitledBorder("Charges and Taxes"));
        jPanelCharges.setName("panelsummary"); // NOI18N

        sactable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(sactable);

        percentlabel.setText("Percent/Amount");
        percentlabel.setName("lblpercent"); // NOI18N

        jLabel41.setText("Desc");
        jLabel41.setName("lbldesc"); // NOI18N

        btsacadd.setText("add");
        btsacadd.setName("btadd"); // NOI18N
        btsacadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsacaddActionPerformed(evt);
            }
        });

        btsacdelete.setText("delete");
        btsacdelete.setName("btdelete"); // NOI18N
        btsacdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsacdeleteActionPerformed(evt);
            }
        });

        ddsacamttype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "amount", "percent" }));
        ddsacamttype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsacamttypeActionPerformed(evt);
            }
        });

        jLabel42.setText("Summary Type");

        jLabel43.setText("Amount Type");

        javax.swing.GroupLayout jPanelChargesLayout = new javax.swing.GroupLayout(jPanelCharges);
        jPanelCharges.setLayout(jPanelChargesLayout);
        jPanelChargesLayout.setHorizontalGroup(
            jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChargesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelChargesLayout.createSequentialGroup()
                        .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(percentlabel)
                            .addComponent(jLabel41)
                            .addComponent(jLabel42)
                            .addComponent(jLabel43))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddsacamttype, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbsacdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelChargesLayout.createSequentialGroup()
                                .addComponent(tbsacamt, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(btsacadd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btsacdelete))
                            .addComponent(ddsactype, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelChargesLayout.setVerticalGroup(
            jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChargesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsactype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsacamttype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsacdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelChargesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btsacadd)
                    .addComponent(btsacdelete)
                    .addComponent(tbsacamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percentlabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE))
        );

        add(jPanelCharges);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        newAction("quote"); 
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        boolean canproceed = true;
        
       // Pattern p = Pattern.compile("\\d\\.\\d\\d");
      //  Matcher m = p.matcher(tbprice.getText());
       // receiverdet  "Part", "PO", "line", "Qty",  listprice, disc, netprice, loc, serial, lot, recvID, recvLine
       // voucherdet   "PO", "Line", "Part", "Qty", "Price", "RecvID", "RecvLine", "Acct", "CC"
            quoteline++;
            actamt += bsParseDouble(tbqty.getText()) * bsParseDouble(tbprice.getText());
            detailmodel.addRow(new Object[] { tbkey.getText(), 
                                                quoteline,
                                                tbitemservice.getText(),
                                                lblitemdesc.getText(),
                                                tbqty.getText(),
                                                tbprice.getText(),
                                                "0", // disc
                                                tbprice.getText(),
                                                dduom.getSelectedItem().toString()
                                                });
       
        tbitemservice.setText("");
        lblitemdesc.setText("");
        tbqty.setText("");
        tbprice.setText("");
        dduom.setSelectedIndex(0);
        tbitemservice.requestFocus();
        summarize();
        
        
        
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
         if (! validateInput(dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.add, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void ddcustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcustActionPerformed
       if ( ddcust.getSelectedItem() != null && ! ddcust.getSelectedItem().toString().isEmpty()  && ! isLoad) {
            setvendorvariables(ddcust.getSelectedItem().toString());
        } // ddvend
    }//GEN-LAST:event_ddcustActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = detailtable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031, String.valueOf(i)));
             actamt -= bsParseDouble(detailtable.getModel().getValueAt(i,3).toString()) * bsParseDouble(detailtable.getModel().getValueAt(i,4).toString());
            ((javax.swing.table.DefaultTableModel) detailtable.getModel()).removeRow(i);
           quoteline--;
        }
        summarize();
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void ddsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsiteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ddsiteActionPerformed

    private void tbpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusLost
                 String x = BlueSeerUtils.bsformat("", tbprice.getText(), "2");
        if (x.equals("error")) {
            tbprice.setText("");
            tbprice.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbprice.requestFocus();
        } else {
            tbprice.setText(x);
            tbprice.setBackground(Color.white);
        }
       
    }//GEN-LAST:event_tbpriceFocusLost

    private void tbpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusGained
         if (tbprice.getText().equals("0")) {
            tbprice.setText("");
        }
    }//GEN-LAST:event_tbpriceFocusGained

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
                String x = BlueSeerUtils.bsformat("", tbqty.getText(), "0");
        if (x.equals("error")) {
            tbqty.setText("");
            tbqty.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbqty.requestFocus();
        } else {
            tbqty.setText(x);
            tbqty.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbqtyFocusLost

    private void tbqtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusGained
        if (tbqty.getText().equals("0")) {
            tbqty.setText("");
        }
    }//GEN-LAST:event_tbqtyFocusGained

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btLookUpItemDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpItemDescActionPerformed
        lookUpFrameItemDesc();
    }//GEN-LAST:event_btLookUpItemDescActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
         if (! validateInput(dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
          if (! validateInput(dbaction.delete)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.delete, new String[]{tbkey.getText()});  
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btprintquoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintquoteActionPerformed
        OVData.printQuote(tbkey.getText()); 
    }//GEN-LAST:event_btprintquoteActionPerformed

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btcommitActionPerformed

    private void btsacaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsacaddActionPerformed
        boolean proceed = true;
        double amount = 0;
        Pattern p = Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        Matcher m = p.matcher(tbsacamt.getText());
        if (!m.find() || tbsacamt.getText() == null) {
            bsmf.MainFrame.show(getMessageTag(1033));
            proceed = false;
            tbsacamt.requestFocus();
            return;
        }

        if (tbsacdesc.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            proceed = false;
            tbsacdesc.requestFocus();
            return;
        }

        if (ddsactype.getSelectedItem().toString().equals("discount") &&
            ddsacamttype.getSelectedItem().toString().equals("amount")) {
            amount = -1 * bsParseDouble(tbsacamt.getText());
        } else {
            amount = bsParseDouble(tbsacamt.getText());
        }

        if (proceed) {
            sacmodel.addRow(new Object[]{ ddsactype.getSelectedItem().toString(), tbsacdesc.getText(), ddsacamttype.getSelectedItem().toString(), String.valueOf(amount)});
        }
        summarize();

    }//GEN-LAST:event_btsacaddActionPerformed

    private void btsacdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsacdeleteActionPerformed
        int[] rows = sactable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) sactable.getModel()).removeRow(i);
        }
        summarize();
    }//GEN-LAST:event_btsacdeleteActionPerformed

    private void ddsacamttypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsacamttypeActionPerformed
        if (ddsacamttype.getSelectedItem().toString().equals("percent")) {
            percentlabel.setText("percent");
        } else {
            percentlabel.setText("amount");
        }
    }//GEN-LAST:event_ddsacamttypeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLookUpItemDesc;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btprintquote;
    private javax.swing.JButton btsacadd;
    private javax.swing.JButton btsacdelete;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbvolume;
    private com.toedter.calendar.JDateChooser dcpricingexpire;
    private com.toedter.calendar.JDateChooser dcquoteexpire;
    private javax.swing.JComboBox<String> ddcurr;
    private javax.swing.JComboBox ddcust;
    private javax.swing.JComboBox<String> dddisccode;
    private javax.swing.JComboBox<String> ddpricegroup;
    private javax.swing.JComboBox<String> ddquotetype;
    private javax.swing.JComboBox<String> ddsacamttype;
    private javax.swing.JComboBox<String> ddsactype;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox<String> ddstatus;
    private javax.swing.JComboBox<String> ddtaxcode;
    private javax.swing.JComboBox<String> ddterms;
    private javax.swing.JComboBox<String> dduom;
    private javax.swing.JTable detailtable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanelCharges;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblitemdesc;
    private javax.swing.JLabel lbmessage;
    private javax.swing.JLabel lbvendor;
    private javax.swing.JLabel percentlabel;
    private javax.swing.JTable sactable;
    private javax.swing.JTextArea tarmks;
    private javax.swing.JTextField tbactualamt;
    private javax.swing.JTextField tbitemservice;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbsacamt;
    private javax.swing.JTextField tbsacdesc;
    private javax.swing.JTextField tbtotdisc;
    private javax.swing.JTextField tbtottax;
    // End of variables declaration//GEN-END:variables
}
