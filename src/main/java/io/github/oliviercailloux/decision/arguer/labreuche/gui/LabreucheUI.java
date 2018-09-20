package io.github.oliviercailloux.decision.arguer.labreuche.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.oliviercailloux.decision.Utils;
import io.github.oliviercailloux.decision.arguer.LabreucheArguer;
import io.github.oliviercailloux.decision.arguer.labreuche.ArgumentGenerator;
import io.github.oliviercailloux.decision.arguer.labreuche.Examples;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.decision.arguer.labreuche.output.Anchor;
import io.github.oliviercailloux.decision.arguer.labreuche.output.LabreucheOutput;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;

public class LabreucheUI {

	private JFrame frame;
	private JLabel lblNumberOfAlternatives;
	private JLabel lblNumberOfCriteria;
	private JLabel lblRandomModel;
	private JLabel lblChooseExample;
	private JLabel lblOr;
	private JLabel lblBestAlternative;
	private JLabel lblPossibleType;
	private JLabel lblOfExplanations;
	private JLabel lblAnchorUsed;

	private JButton btnGenerate;
	private JButton btnLoad;
	private JButton btnResolve;
	private JButton btnRMGCOMP;
	private JButton btnRmgavg;
	private JButton btnIvt;
	private JButton btnNoa;
	private JButton btnAll;
	
	private JTextField varNbrAlternatives;
	private JTextField varNbrCriteria;
	private JComboBox<String> comboBox;
	
	private JEditorPane BestAlternativePane;
	private JEditorPane ExplanationPane;
	private JTextPane AnchorPane;

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
		frame.setTitle("Labreuche Arguer");
		frame.setBounds(100, 100, 613, 516);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JButton btnHelp = new JButton("Help");

		btnHelp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame windowHelp = new JFrame();
				windowHelp.setTitle("Help");
				windowHelp.setSize(200, 600);
				windowHelp.setLocationRelativeTo(null);
				windowHelp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JPanel p = new JPanel();
				windowHelp.getContentPane().add(p);

				windowHelp.setVisible(true);

			}
		});
		btnHelp.setBounds(34, 455, 97, 25);
		frame.getContentPane().add(btnHelp);

		lblNumberOfAlternatives = new JLabel("Number of alternatives");
		lblNumberOfAlternatives.setBounds(12, 59, 169, 15);
		frame.getContentPane().add(lblNumberOfAlternatives);

		lblNumberOfCriteria = new JLabel("Number of criteria");
		lblNumberOfCriteria.setBounds(12, 88, 169, 15);
		frame.getContentPane().add(lblNumberOfCriteria);

		varNbrAlternatives = new JTextField();
		varNbrAlternatives.setBounds(188, 57, 60, 19);
		frame.getContentPane().add(varNbrAlternatives);
		varNbrAlternatives.setColumns(10);

		varNbrCriteria = new JTextField();
		varNbrCriteria.setBounds(188, 86, 60, 19);
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
		
		comboBox = new JComboBox<>();
		comboBox.setBackground(Color.WHITE);
		comboBox.setBounds(396, 59, 106, 24);
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
		
		lblBestAlternative = new JLabel("Best alternative :");
		lblBestAlternative.setBounds(22, 182, 137, 15);
		frame.getContentPane().add(lblBestAlternative);
		
		ExplanationPane = new JEditorPane();
		ExplanationPane.setBounds(153, 248, 448, 221);
		ExplanationPane.setEditable(false);
		frame.getContentPane().add(ExplanationPane);
		
		BestAlternativePane = new JEditorPane();
		BestAlternativePane.setBounds(153, 182, 60, 21);
		BestAlternativePane.setEditable(false);
		frame.getContentPane().add(BestAlternativePane);
		
		JLabel lblExplanations = new JLabel("Explanations");
		lblExplanations.setBounds(310, 225, 144, 15);
		frame.getContentPane().add(lblExplanations);

		btnResolve = new JButton("Resolve");
		btnResolve.setBounds(24, 408, 117, 25);
		frame.getContentPane().add(btnResolve);
				
		btnResolve.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (lm != null) {
					LabreucheArguer largue = new LabreucheArguer();
					String message = "";
					try {
					 message = largue.argue(lm.getExplanation());
					}catch(IllegalStateException e5) {
						JOptionPane.showMessageDialog(null," Message Error :" + e5.getMessage());
					}					
					String old = ExplanationPane.getText();
					
					AnchorPane.setText(lm.getLabreucheOutput().getAnchor().toString());
					BestAlternativePane.setText(lm.getAlternativesComparison().getX().getName());
					ExplanationPane.setText(old + "\n" + message);
					return;
				}
				
				if(ag != null) {
					//TODO();
					
					return;
				}
				
				

			}
		});

		btnGenerate = new JButton("Generate");
		btnGenerate.setBounds(101, 131, 117, 25);
		frame.getContentPane().add(btnGenerate);

		btnGenerate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (varNbrAlternatives.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null,
							"You need to give the number of alternatives you want for the example generated");
				}

				if (varNbrCriteria.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null,
							"You need to give the number of criteria you want for the example generated");
				}

				int alternatives = 0;
				int criteria = 0;
				
				try {
					alternatives = Integer.parseInt(varNbrAlternatives.getText());
				}catch(NumberFormatException e1) {
					JOptionPane.showMessageDialog(null, " Required numeric number in Number of alternative");
					e1.printStackTrace();
					return;
				}
				
				try {
					criteria = Integer.parseInt(varNbrCriteria.getText());
				}catch(NumberFormatException e2) {
					JOptionPane.showMessageDialog(null, " Required numeric number in Number of criteria");
					e2.printStackTrace();
					return;
				}
				
				if (alternatives <= 0) {
					JOptionPane.showMessageDialog(null, "The number of alternatives must be higher than zero");
				}

				if (alternatives <= 0) {
					JOptionPane.showMessageDialog(null, "The number of criteria must be higher than zero");
				}

				ag = new ArgumentGenerator(alternatives, criteria);

				String display = "    Criteria    <-   Weight : \n";

				for (Criterion c : ag.getWeights().keySet())
					display += "\n" + "	" + c.getName() + "  <-  w_" + c.getId() + " = " + ag.getWeights().get(c);

				display += "\n \n    Alternatives : ";

				for (Alternative a : ag.getAlternatives()) {
					display += "\n" + "	" + a.getName() + " " + " : " + Utils.showVector(a.getEvaluations().values());
				}
								
				JFrame windowInfo = new JFrame();
				windowInfo.setTitle("Problem Informations");
				windowInfo.setSize(600, 550);
				windowInfo.setLocationRelativeTo(null);
				windowInfo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JPanel p = new JPanel();
				windowInfo.getContentPane().add(p);

				JTextArea text = new JTextArea(display);

				p.add(text);

				windowInfo.pack();
				windowInfo.setVisible(true);
			}
		});

		btnLoad = new JButton("Load");
		btnLoad.setBounds(386, 131, 117, 25);
		frame.getContentPane().add(btnLoad);
		
		btnRMGCOMP = new JButton("RMGCOMP");
		btnRMGCOMP.setBounds(24, 367, 117, 25);
		frame.getContentPane().add(btnRMGCOMP);
		
		btnRMGCOMP.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LabreucheArguer largue = new LabreucheArguer();
				String message = "";
				try {
				 message = largue.argue(lm.getRMGCOMPExplanation());
				}catch(IllegalStateException e3) {
					JOptionPane.showMessageDialog(null,
							"The anchor NOA is not applicable on this example" + "\n Message Error :" + e3.getMessage());
				}
				String old = ExplanationPane.getText();
				
				AnchorPane.setText(Anchor.RMGCOMP.toString());
				BestAlternativePane.setText(lm.getAlternativesComparison().getX().getName());
				ExplanationPane.setText(old + "\n" + message);				
			}
		});
		
		
		btnRmgavg = new JButton("RMGAVG");
		btnRmgavg.setBounds(24, 337, 117, 25);
		frame.getContentPane().add(btnRmgavg);
		
		btnRmgavg.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LabreucheArguer largue = new LabreucheArguer();
				String message = "";
				try {
				 message = largue.argue(lm.getRMGAVGExplanation());
				}catch(IllegalStateException e2) {
					JOptionPane.showMessageDialog(null,
							"The anchor NOA is not applicable on this example" + "\n Message Error :" + e2.getMessage());
				}
				String old = ExplanationPane.getText();
			
				AnchorPane.setText(Anchor.RMGAVG.toString());
				BestAlternativePane.setText(lm.getAlternativesComparison().getX().getName());
				ExplanationPane.setText(old + "\n" + message);
			}
		});
		
		
		btnIvt = new JButton("IVT");
		btnIvt.setBounds(24, 307, 117, 25);
		frame.getContentPane().add(btnIvt);
		
		btnIvt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LabreucheArguer largue = new LabreucheArguer();
				String message = "";
				try {
				 message = largue.argue(lm.getIVTExplanation());
				}catch(IllegalStateException e1) {
					JOptionPane.showMessageDialog(null,
							"The anchor NOA is not applicable on this example" + "\n Message Error :" + e1.getMessage());
				}
				String old = ExplanationPane.getText();
				
				AnchorPane.setText(Anchor.IVT.toString());
				BestAlternativePane.setText(lm.getAlternativesComparison().getX().getName());
				ExplanationPane.setText(old + "\n" + message);
			}
		});
		
		btnNoa = new JButton("NOA");
		btnNoa.setBounds(24, 277, 117, 25);
		frame.getContentPane().add(btnNoa);
		
		btnNoa.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LabreucheArguer largue = new LabreucheArguer();
				String message = "";
				try {
				 message = largue.argue(lm.getNOAExplanation());
				}catch(IllegalStateException e) {
					JOptionPane.showMessageDialog(null,
							"The anchor NOA is not applicable on this example" + "\n Message Error :" + e.getMessage());
				}
				String old = ExplanationPane.getText();
				
				AnchorPane.setText(Anchor.NOA.toString());
				BestAlternativePane.setText(lm.getAlternativesComparison().getX().getName());
				ExplanationPane.setText(old + "\n" + message);
			}
		});
		
		btnAll = new JButton("ALL");
		btnAll.setBounds(24, 248, 117, 25);
		frame.getContentPane().add(btnAll);
		
		btnAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LabreucheArguer largue = new LabreucheArguer();
				String message = "";
				try {
				 message = largue.argue(lm.getALLExplanation());
				}catch(IllegalStateException e) {
					JOptionPane.showMessageDialog(null,
							"The anchor ALL is not applicable on this example" + "\n Message Error :" + e.getMessage());
				}
				String old = ExplanationPane.getText();
				
				AnchorPane.setText(Anchor.ALL.toString());
				BestAlternativePane.setText(lm.getAlternativesComparison().getX().getName());
				ExplanationPane.setText(old + "\n" + message);
			}
		});
		
		lblPossibleType = new JLabel("Possible type");
		lblPossibleType.setBounds(32, 209, 127, 15);
		frame.getContentPane().add(lblPossibleType);
		
		lblOfExplanations = new JLabel("of explanations :");
		lblOfExplanations.setBounds(26, 225, 130, 15);
		frame.getContentPane().add(lblOfExplanations);
		
		lblAnchorUsed = new JLabel("Anchor used : ");
		lblAnchorUsed.setBounds(348, 182, 119, 15);
		frame.getContentPane().add(lblAnchorUsed);
		
		AnchorPane = new JTextPane();
		AnchorPane.setBounds(453, 182, 82, 21);
		AnchorPane.setEditable(false);
		frame.getContentPane().add(AnchorPane);

		btnLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lm = null;
				String display = "";

				switch (comboBox.getSelectedItem().toString()) {

				case "example1":
					lm = Examples.getExampleAll();
					display = "				Example 1";
					break;

				case "example5":
					lm = Examples.getExample5();
					display = "				Example 5";
					break;

				case "example6":
					lm = Examples.getExample6();
					display = "				Example 6";
					break;

				case "example9":
					lm = Examples.getExample9();
					display = "				Example 9";
					break;

				case "example10":
					lm = Examples.getExample10();
					display = "				Example 10";
					break;

				case "example13":
					lm = Examples.getExample13();
					display = "				Example 13";
					break;

				case "example14":
					lm = Examples.getExample14();
					display = "				Example 14";
					break;

				case "example15":
					lm = Examples.getExample15();
					display = "				Example 15";
					break;

				case "example16":
					lm = Examples.getExample16();
					display = "				Example 16";
					break;

				case "example17":
					lm = Examples.getExample17();
					display = "				Example 17";
					break;

				case "example18":
					lm = Examples.getExample18();
					display = "				Example 18";
					break;

				default:
					LOGGER.info("ERROR example");
				}

				if (lm == null) {
					JOptionPane.showMessageDialog(null, "error loading problem");
					return;
				}

				display += "\n Criteria         <-   Weights  : \n";

				for (Criterion c : lm.getAlternativesComparison().getWeight().keySet())
					display += "\n" + c.getName() + "  <-  w_" + c.getId() + " = "
							+ lm.getAlternativesComparison().getWeight().get(c);

				display += "\n \n Alternatives : ";

				display += "\n" + "	" + lm.getAlternativesComparison().getX().getName() + " " + " : "
						+ Utils.showVector(lm.getAlternativesComparison().getX().getEvaluations().values());
				display += "\n" + "	" + lm.getAlternativesComparison().getY().getName() + " " + " : "
						+ Utils.showVector(lm.getAlternativesComparison().getY().getEvaluations().values());
				
				
				ExplanationPane.setText("Explanation why " + lm.getAlternativesComparison().getX().getName() + " is better than "
						+ lm.getAlternativesComparison().getY().getName() + " : ");

				JFrame windowInfo = new JFrame();
				windowInfo.setTitle("Problem Informations");
				windowInfo.setSize(600, 550);
				windowInfo.setLocationRelativeTo(null);
				windowInfo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JPanel p = new JPanel();
				windowInfo.getContentPane().add(p);

				JTextArea text = new JTextArea(display);

				p.add(text);

				windowInfo.pack();
				windowInfo.setVisible(true);
			}
		});


	}
}
