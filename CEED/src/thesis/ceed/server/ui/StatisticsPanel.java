package thesis.ceed.server.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.jdatepicker.DateModel;
import net.sourceforge.jdatepicker.JDateComponentFactory;
import net.sourceforge.jdatepicker.JDatePicker;
import thesis.ceed.server.Attempt;
import thesis.ceed.server.ServerDbHelper;

public class StatisticsPanel extends JPanel {

	private static final long serialVersionUID = -2293841494260588433L;
	
	private JPanel upperPanel;
	private JLabel lbImei, lbEmo, lbFrom, lbTo;
	private JComboBox<Object> cbImei, cbEmo;
	private JDatePicker dpFrom, dpTo;
	private JButton btnFilter;
	private JScrollPane lowerScrollPane;
	private JTable tblAttempts;
	private String[][] tableData;
	private ServerDbHelper dbHelper;
	private Object[] imeis, emos;
	
	public StatisticsPanel() {
		upperPanel = new JPanel(new FlowLayout());
		dbHelper = new ServerDbHelper();
	    
	    prepareComboBoxData();
		lbImei = new JLabel("IMEI: ");
		cbImei = new JComboBox<Object>();
		cbImei.setModel(new DefaultComboBoxModel<Object>(imeis));
		cbImei.setSelectedItem(imeis[imeis.length - 1]);
		lbEmo = new JLabel("Emotion: ");
		cbEmo = new JComboBox<Object>();
		cbEmo.setModel(new DefaultComboBoxModel<Object>(emos));
		cbEmo.setSelectedItem(emos[emos.length - 1]);
		
		lbFrom = new JLabel("From: ");
		dpFrom = JDateComponentFactory.createJDatePicker();
	    dpFrom.setTextEditable(true);
	    dpFrom.setShowYearButtons(true);
	    dpFrom.getModel().setSelected(true);
	    lbTo = new JLabel("To: ");
	    dpTo = JDateComponentFactory.createJDatePicker();
	    dpTo.setTextEditable(true);
	    dpTo.setShowYearButtons(true);
	    dpTo.getModel().setSelected(true);
	    
	    btnFilter = new JButton("Search");
	    btnFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DateModel<?> dmFrom, dmTo;
				Calendar time;
				String imei, emo;
				Long timeFrom, timeTo;
				
				imei = (String) cbImei.getSelectedItem();
				emo = (String) cbEmo.getSelectedItem();
				dmFrom = dpFrom.getModel();
				dmTo = dpTo.getModel();
				time = Calendar.getInstance();
				time.set(dmFrom.getYear(), dmFrom.getMonth(), dmFrom.getDay());
				timeFrom = time.getTime().getTime();
				time.set(dmTo.getYear(), dmTo.getMonth(), dmTo.getDay());
				timeTo = time.getTime().getTime();
				if (timeFrom <= timeTo) {
					ArrayList<Attempt> selectedAttempts = dbHelper.queryDb(new String[] {imei, emo, String.valueOf(timeFrom), String.valueOf(timeTo)});
					convertToTableData(selectedAttempts);
					((AttemptModel)tblAttempts.getModel()).fireTableDataChanged();
				} else
					JOptionPane.showMessageDialog(null, "Date From is later than Date To.", "Wrong info", JOptionPane.WARNING_MESSAGE);
			}
		});
	    
	    upperPanel.add(lbImei);
	    upperPanel.add(cbImei);
	    upperPanel.add(lbEmo);
	    upperPanel.add(cbEmo);
	    upperPanel.add(lbFrom);
	    upperPanel.add((JComponent) dpFrom);
	    upperPanel.add(lbTo);
	    upperPanel.add((JComponent) dpTo);
	    upperPanel.add(btnFilter);
	    
	    tableData = new String[][] { {"", "", "", ""} };
	    tblAttempts = new JTable(new AttemptModel());
	    tblAttempts.setShowGrid(true);
	    Dimension viewport = tblAttempts.getPreferredScrollableViewportSize();
	    viewport.setSize(viewport.getWidth(), viewport.getHeight() / 2);
	    tblAttempts.setPreferredScrollableViewportSize(viewport);
	    tblAttempts.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	    tblAttempts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    lowerScrollPane = new JScrollPane(tblAttempts);
	    
	    setLayout(new BorderLayout());
		add(upperPanel, BorderLayout.NORTH);
		add(lowerScrollPane, BorderLayout.CENTER);
	}
	
	private void prepareComboBoxData() {
		Object[] temp = null;
		int numUsers = 0;
		temp = dbHelper.getAllUser();
		if (temp != null)
			numUsers = temp.length;
		imeis = new Object[numUsers + 1];
		for (int i = 0; i < numUsers; i++) {
			imeis[i] = temp[i];
		}
		imeis[numUsers] = "All";
		
		emos = new Object[] {"Anger", "Boredom ", "Disgust", "Fear", "Happiness", "Sadness", "Neutral", "All"};
	}
	
	private void convertToTableData(ArrayList<Attempt> selectedAttempts) {
		int numAttempts = selectedAttempts.size();
		String upTime = null;
		SimpleDateFormat dm = new SimpleDateFormat("dd-MM-yyyy");
		Calendar cal = Calendar.getInstance();
		
		tableData = new String[numAttempts][4];
		for (int i = 0; i < numAttempts; i++) {
			tableData[i][0] = selectedAttempts.get(i).getImei();
			tableData[i][1] = selectedAttempts.get(i).getPath();
			tableData[i][2] = selectedAttempts.get(i).getEmotion();
			upTime = selectedAttempts.get(i).getUpTime();
			cal.setTimeInMillis(Long.parseLong(upTime));
			tableData[i][3] = dm.format(cal.getTime());
		}
	}
	
	private class AttemptModel extends AbstractTableModel {
		private static final long serialVersionUID = -5101012647302663639L;
		
		private String[] colHeader = new String[] {"IMEI", "Path", "Emotion", "Upload time"};
		
		@Override
		public int getRowCount() {
			return tableData.length;
		}

		@Override
		public int getColumnCount() {
			return colHeader.length;
		}
		
		public String getColumnName(int col) {
            return colHeader[col];
        }

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return tableData[rowIndex][columnIndex];
		}
	}
}
