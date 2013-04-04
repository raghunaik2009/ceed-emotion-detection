package thesis.ceed.server;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.filters.Filter;

public class FeatureExtraction extends JFrame{
	
	
	private JPanel aPanel;
	private JButton btnSelectPath;
	private JButton btnRunPraat;
	private JButton btnAttributeSelection;
	private JLabel lbPath;
	private JFileChooser fileChooser;
	
	private String path = "";
	private String cmd = "";
	public FeatureExtraction(){
		aPanel = new JPanel();
		btnSelectPath = new JButton("Select Database Folder");
		btnRunPraat = new JButton("Run Praat");
		btnAttributeSelection = new JButton("Attribute Selection");
		fileChooser = new JFileChooser();
		
		fileChooser.setCurrentDirectory(new File("D:\\Internship\\Emotion Detection\\Database\\example"));
		lbPath = new JLabel();
		aPanel.setLayout(new FlowLayout());
		aPanel.add(btnSelectPath);
		aPanel.add(btnRunPraat);
		aPanel.add(btnAttributeSelection);
		aPanel.add(lbPath);
		this.setContentPane(aPanel);
		this.setTitle("Demo Call Praat");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//this.setSize(400, 200);
		this.pack();
		
		btnSelectPath.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fileChooser.showOpenDialog(FeatureExtraction.this);
				if(returnVal == JFileChooser.APPROVE_OPTION){
					File file = fileChooser.getSelectedFile();
					path = file.getAbsolutePath();
					cmd = "C:\\praatcon.exe C:\\Eg_FeatureExtraction.praat \""+path+"\\";	
					lbPath.setText(cmd);
					FeatureExtraction.this.pack();
				}
			}
		});
		
		
		btnRunPraat.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try {	
					Process p = Runtime.getRuntime().exec(cmd);
					//p.getOutputStream()
					p.waitFor();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});		
		
		btnAttributeSelection.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try {
					BufferedReader reader = new BufferedReader(new FileReader("C:\\out.arff"));
					Instances data = new Instances(reader);
					reader.close();
					
					weka.filters.supervised.attribute.AttributeSelection attributeSelection = new weka.filters.supervised.attribute.AttributeSelection();
					CfsSubsetEval eval  = new CfsSubsetEval();
					BestFirst search = new BestFirst();
					
					attributeSelection.setEvaluator(eval);
					attributeSelection.setSearch(search);
					
					//attributeSelection.SelectAttributes(data);
					attributeSelection.setInputFormat(data);
					Instances newData = Filter.useFilter(data, attributeSelection);
					System.out.println(newData);
					//int[] index = attributeSelection.selectedAttributes();
					//System.out.println(Utils.arrayToString(index));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FeatureExtraction demo = new FeatureExtraction();
		demo.setVisible(true);
	}

}
