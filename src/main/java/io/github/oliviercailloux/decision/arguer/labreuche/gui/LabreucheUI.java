package io.github.oliviercailloux.decision.arguer.labreuche.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.oliviercailloux.decision.Utils;
import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.ArgumentGenerator;
import io.github.oliviercailloux.decision.arguer.labreuche.Examples;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.decision.arguer.labreuche.Tools;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

import javax.swing.JEditorPane;
import java.awt.Color;

public class LabreucheUI {

	private JFrame frame;
	private JLabel lblNumberOfAlternatives;
	private JLabel lblNumberOfCriteria;
	private JTextField varNbrAlternatives;
	private JTextField varNbrCriteria;
	private JLabel lblRandomModel;
	private JLabel lblChooseExample;
	private JLabel lblOr;
	private JButton btnGenerate;
	private JButton btnLoad;
	private JComboBox<String> comboBox;
	private JButton btnResolve;
	private JLabel lblBestAlternative;
	private JLabel lblBestAlternative_1;
	private LabreucheModel lm = null;
	private ArgumentGenerator ag = null;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LabreucheModel.class);

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					LabreucheUI window = new LabreucheUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LabreucheUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 613, 516);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnHelp = new JButton("Help");
		
		btnHelp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame windowHelp = new JFrame();
				windowHelp.setTitle("Help");
				windowHelp.setSize(200,600);
				windowHelp.setLocationRelativeTo(null);
				windowHelp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				JPanel p = new JPanel();
				windowHelp.getContentPane().add(p);
				
				windowHelp.setVisible(true);
				
				
			}
		});
		btnHelp.setBounds(246, 442, 97, 25);
		frame.getContentPane().add(btnHelp);
		
		lblNumberOfAlternatives = new JLabel("Number of alternatives");
		lblNumberOfAlternatives.setBounds(12, 77, 169, 15);
		frame.getContentPane().add(lblNumberOfAlternatives);
		
		lblNumberOfCriteria = new JLabel("Number of criteria");
		lblNumberOfCriteria.setBounds(12, 106, 169, 15);
		frame.getContentPane().add(lblNumberOfCriteria);
		
		varNbrAlternatives = new JTextField();
		varNbrAlternatives.setBounds(188, 75, 60, 19);
		frame.getContentPane().add(varNbrAlternatives);
		varNbrAlternatives.setColumns(10);
		
		varNbrCriteria = new JTextField();
		varNbrCriteria.setBounds(188, 104, 60, 19);
		frame.getContentPane().add(varNbrCriteria);
		varNbrCriteria.setColumns(10);
		
		lblRandomModel = new JLabel("Random Example");
		lblRandomModel.setBounds(101, 12, 127, 15);
		frame.getContentPane().add(lblRandomModel);
		
		lblChooseExample = new JLabel("Choose Example");
		lblChooseExample.setBounds(386, 12, 127, 15);
		frame.getContentPane().add(lblChooseExample);
		
		lblOr = new JLabel("or");
		lblOr.setBounds(292, 77, 70, 15);
		frame.getContentPane().add(lblOr);
		
		btnGenerate = new JButton("Generate");
		btnGenerate.setBounds(101, 149, 117, 25);
		frame.getContentPane().add(btnGenerate);
		
		btnGenerate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(varNbrAlternatives.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "You need to give number the of alternatives you want for the example generated");
				}
				
				if(varNbrCriteria.getText().isEmpty()){
					JOptionPane.showMessageDialog(null, "You need to give number the of criteria you want for the example generated");
				}
				
				int alternatives = Integer.parseInt(varNbrAlternatives.getText());
				int criteria = Integer.parseInt(varNbrCriteria.getText());
				
				if(alternatives <= 0) {
					JOptionPane.showMessageDialog(null, "The number of alternatives must be higher than zero");
				}
				
				if(alternatives <= 0) {
					JOptionPane.showMessageDialog(null, "The number of criteria must be higher than zero");
				}

				ag = new ArgumentGenerator(alternatives,criteria);
				
				String display = "    Criteria    <-   Weight : \n";

				for (Criterion c : ag.getWeights().keySet())
					display += "\n" + "	" + c.getName() + "  <-  w_" + c.getId() + " = "
							+ ag.getWeights().get(c);

				display += "\n \n    Alternatives : ";

				for (Alternative a : ag.getAlternatives()) {
					display += "\n" + "	" + a.getName() + " " + " : "
							+ Utils.showVector(a.getEvaluations().values());
				}
				
				JFrame windowInfo = new JFrame();
				windowInfo.setTitle("Problem Informations");
				windowInfo.setSize(600,550);
				windowInfo.setLocationRelativeTo(null);
				windowInfo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				JPanel p = new JPanel();
				windowInfo.add(p);
				
				JTextArea text = new JTextArea(display);
				
				p.add(text);
				
				windowInfo.pack();
				windowInfo.setVisible(true);
			}
		});
		
		btnLoad = new JButton("Load");
		btnLoad.setBounds(407, 149, 117, 25);
		frame.getContentPane().add(btnLoad);
		
		btnLoad.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				lm = null;
				String display = "";
				
				switch(comboBox.getSelectedItem().toString()) {

				case "example1" :
					lm = Examples.getExampleAll();
					display = "				Example 1";
					break;
					
				case "example5" :
					lm = Examples.getExample5();
					display = "				Example 5";
					break;

				case "example6" :
					lm = Examples.getExample6();
					display = "				Example 6";
					break;
					
				case "example9" : 
					lm = Examples.getExample9();					
					display = "				Example 9";
					break;
					
				case "example10" : 
					lm = Examples.getExample10();
					display = "				Example 10";
					break;

				case "example13" : 
					lm = Examples.getExample13();
					display = "				Example 13";
					break;

				case "example14" : 
					lm = Examples.getExample14();
					display = "				Example 14";
					break;

				case "example15" : 
					lm = Examples.getExample15();
					display = "				Example 15";
					break;

				case "example16" : 
					lm = Examples.getExample16();
					display = "				Example 16";
					break;

				case "example17" :
					lm = Examples.getExample17();
					display = "				Example 17";
					break;

				case "example18" : 
					lm = Examples.getExample18();
					display = "				Example 18";
					break;

				default :
					LOGGER.info("ERROR example");
				}
				
				if(lm == null) {
					JOptionPane.showMessageDialog(null, "error loading problem");
					return;
				}
								
				display += "\n Criteria    <-   Weight : \n";

				for (Criterion c : lm.getAlternativesComparison().getWeight().keySet())
					display += "\n" + "	" + c.getName() + "  <-  w_" + c.getId() + " = "
							+ lm.getAlternativesComparison().getWeight().get(c);

				display += "\n \n Alternatives : ";

				display += "\n" + "	" + lm.getAlternativesComparison().getX().getName() + " " + " : "
						+ Utils.showVector(lm.getAlternativesComparison().getX().getEvaluations().values());
				display += "\n" + "	" + lm.getAlternativesComparison().getY().getName() + " " + " : "
						+ Utils.showVector(lm.getAlternativesComparison().getY().getEvaluations().values());
				
				JFrame windowInfo = new JFrame();
				windowInfo.setTitle("Problem Informations");
				windowInfo.setSize(600,550);
				windowInfo.setLocationRelativeTo(null);
				windowInfo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				JPanel p = new JPanel();
				windowInfo.add(p);
				
				JTextArea text = new JTextArea(display);
				
				p.add(text);
				
				windowInfo.pack();
				windowInfo.setVisible(true);
			}
		});
		
		comboBox = new JComboBox<>();
		comboBox.setBackground(Color.WHITE);
		comboBox.setBounds(407, 72, 106, 24);
		frame.getContentPane().add(comboBox);
		comboBox.addItem("example1");
		comboBox.addItem("example5");
		comboBox.addItem("example6");
		comboBox.addItem("example9");
		comboBox.addItem("example10");
		comboBox.addItem("example13");
		comboBox.addItem("example14");
		comboBox.addItem("example15");
		comboBox.addItem("example16");
		comboBox.addItem("example17");
		comboBox.addItem("example18");
		
		btnResolve = new JButton("Resolve");
		btnResolve.setBounds(246, 370, 117, 25);
		frame.getContentPane().add(btnResolve);
		
		btnResolve.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(lm != null) {
					
					
				}
				
			}
		});
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(147, 186, 437, 172);
		frame.getContentPane().add(panel);
		
		lblBestAlternative = new JLabel("not found");
		lblBestAlternative.setBounds(39, 226, 117, 15);
		frame.getContentPane().add(lblBestAlternative);
		
		lblBestAlternative_1 = new JLabel("Best Alternative :");
		lblBestAlternative_1.setBounds(12, 199, 137, 15);
		frame.getContentPane().add(lblBestAlternative_1);
	}
}
