package io.github.oliviercailloux.decision.arguer.labreuche.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.oliviercailloux.decision.Utils;
import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.ArgumentGenerator;
import io.github.oliviercailloux.decision.arguer.labreuche.Examples;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheArguer;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.decision.arguer.labreuche.output.Anchor;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class LabreucheGUI {

	private JFrame frame;

	private JTextField varNbrAlternatives;
	private JTextField varNbrCriteria;
	private JComboBox<String> comboBox;

	private JEditorPane bestAlternativePane;
	private JEditorPane explanationPane;
	private JTextPane anchorPane;

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
					LabreucheGUI window = new LabreucheGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					LOGGER.debug(" BUG : " + e.getMessage());
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LabreucheGUI() {
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

		JLabel lblNumberOfAlternatives = new JLabel("Number of alternatives");
		lblNumberOfAlternatives.setBounds(12, 59, 169, 15);
		frame.getContentPane().add(lblNumberOfAlternatives);

		JLabel lblNumberOfCriteria = new JLabel("Number of criteria");
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

		JLabel lblRandomModel = new JLabel("Random Example");
		lblRandomModel.setBounds(101, 12, 127, 15);
		frame.getContentPane().add(lblRandomModel);

		JLabel lblChooseExample = new JLabel("Choose Example");
		lblChooseExample.setBounds(386, 12, 127, 15);
		frame.getContentPane().add(lblChooseExample);

		JLabel lblOr = new JLabel("or");
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

		JLabel lblBestAlternative = new JLabel("Best alternative :");
		lblBestAlternative.setBounds(22, 182, 137, 15);
		frame.getContentPane().add(lblBestAlternative);

		explanationPane = new JEditorPane();
		explanationPane.setBounds(153, 248, 448, 221);
		explanationPane.setEditable(false);
		frame.getContentPane().add(explanationPane);

		bestAlternativePane = new JEditorPane();
		bestAlternativePane.setBounds(153, 182, 60, 21);
		bestAlternativePane.setEditable(false);
		frame.getContentPane().add(bestAlternativePane);

		JLabel lblExplanations = new JLabel("Explanations");
		lblExplanations.setBounds(310, 225, 144, 15);
		frame.getContentPane().add(lblExplanations);

		JButton btnResolve = new JButton("Resolve");
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
					} catch (IllegalStateException e5) {
						JOptionPane.showMessageDialog(null, " Message Error :" + e5.getMessage());
					}
					String old = explanationPane.getText();

					anchorPane.setText(lm.getLabreucheOutput().getAnchor().toString());
					bestAlternativePane.setText(lm.getAlternativesComparison().getX().getName());
					explanationPane.setText(old + "\n" + message);
				}
			}
		});

		JButton btnGenerate = new JButton("Generate");
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
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null, " Required numeric number in Number of alternative");
					LOGGER.debug(" BUG : " + e1.getMessage());
					return;
				}

				try {
					criteria = Integer.parseInt(varNbrCriteria.getText());
				} catch (NumberFormatException e2) {
					JOptionPane.showMessageDialog(null, " Required numeric number in Number of criteria");
					LOGGER.debug(" BUG : " + e2.getMessage());
					return;
				}

				if (alternatives <= 0) {
					JOptionPane.showMessageDialog(null, "The number of alternatives must be higher than zero");
				}

				if (alternatives <= 0) {
					JOptionPane.showMessageDialog(null, "The number of criteria must be higher than zero");
				}

				ag = new ArgumentGenerator(alternatives, criteria);

				StringBuilder display = new StringBuilder("    Criteria    <-   Weight : \n");

				for (Criterion c : ag.getWeights().keySet())
					display.append("\n" + "	" + c.getName() + "  <-  w_" + c.getId() + " = " + ag.getWeights().get(c));

				display.append("\n \n    Alternatives : ");

				for (Alternative a : ag.getAlternatives()) {
					display.append(
							"\n" + "	" + a.getName() + " " + " : " + Utils.showVector(a.getEvaluations().values()));
				}

				JFrame windowInfo = new JFrame();
				windowInfo.setTitle("Problem Informations");
				windowInfo.setSize(600, 550);
				windowInfo.setLocationRelativeTo(null);
				windowInfo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JPanel p = new JPanel();
				windowInfo.getContentPane().add(p);

				JTextArea text = new JTextArea(display.toString());

				p.add(text);

				Alternative best = null;
				Alternative second = null;

				try {
					best = ag.findUniqueBest();

				} catch (IllegalArgumentException e6) {
					LOGGER.debug(" BUG : " + e6.getMessage());

					Iterator<Alternative> itr = ag.findBest().iterator();

					best = itr.next();
					second = itr.next();
				}

				if (second == null) {
					Set<Alternative> copy = new HashSet<>(ag.getAlternatives());
					copy.remove(best);

					ag.setAlternatives(copy);

					try {
						second = ag.findUniqueBest();

					} catch (IllegalArgumentException e8) {
						LOGGER.debug(" BUG : " + e8.getMessage());

						Iterator<Alternative> itr = ag.findBest().iterator();

						second = itr.next();
					}
				}

				AlternativesComparison altComp = new AlternativesComparison(best, second, ag.getWeights());

				lm = new LabreucheModel(altComp);

				windowInfo.pack();
				windowInfo.setVisible(true);
			}
		});

		JButton btnLoad = new JButton("Load");
		btnLoad.setBounds(386, 131, 117, 25);
		frame.getContentPane().add(btnLoad);

		JButton btnRMGCOMP = new JButton("RMGCOMP");
		btnRMGCOMP.setBounds(24, 367, 117, 25);
		frame.getContentPane().add(btnRMGCOMP);

		btnRMGCOMP.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LabreucheArguer largue = new LabreucheArguer();
				String message = "";
				try {
					message = largue.argue(lm.getRMGCOMPExplanation());
				} catch (IllegalStateException e3) {
					JOptionPane.showMessageDialog(null, "The anchor RMGCOMP is not applicable on this example"
							+ "\n Message Error :" + e3.getMessage());
				}
				String old = explanationPane.getText();

				anchorPane.setText(Anchor.RMGCOMP.toString());
				bestAlternativePane.setText(lm.getAlternativesComparison().getX().getName());
				explanationPane.setText(old + "\n" + message);
			}
		});

		JButton btnRmgavg = new JButton("RMGAVG");
		btnRmgavg.setBounds(24, 337, 117, 25);
		frame.getContentPane().add(btnRmgavg);

		btnRmgavg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LabreucheArguer largue = new LabreucheArguer();
				String message = "";
				try {
					message = largue.argue(lm.getRMGAVGExplanation());
				} catch (IllegalStateException e2) {
					JOptionPane.showMessageDialog(null, "The anchor RMGAVG is not applicable on this example"
							+ "\n Message Error :" + e2.getMessage());
				}
				String old = explanationPane.getText();

				anchorPane.setText(Anchor.RMGAVG.toString());
				bestAlternativePane.setText(lm.getAlternativesComparison().getX().getName());
				explanationPane.setText(old + "\n" + message);
			}
		});

		JButton btnIvt = new JButton("IVT");
		btnIvt.setBounds(24, 307, 117, 25);
		frame.getContentPane().add(btnIvt);

		btnIvt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LabreucheArguer largue = new LabreucheArguer();
				String message = "";
				try {
					message = largue.argue(lm.getIVTExplanation());
				} catch (IllegalStateException e1) {
					JOptionPane.showMessageDialog(null, "The anchor IVT is not applicable on this example"
							+ "\n Message Error :" + e1.getMessage());
				}
				String old = explanationPane.getText();

				anchorPane.setText(Anchor.IVT.toString());
				bestAlternativePane.setText(lm.getAlternativesComparison().getX().getName());
				explanationPane.setText(old + "\n" + message);
			}
		});

		JButton btnNoa = new JButton("NOA");
		btnNoa.setBounds(24, 277, 117, 25);
		frame.getContentPane().add(btnNoa);

		btnNoa.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				LabreucheArguer largue = new LabreucheArguer();
				String message = "";
				try {
					message = largue.argue(lm.getNOAExplanation());
				} catch (IllegalStateException e) {
					JOptionPane.showMessageDialog(null,
							"The anchor NOA is not applicable on this example" + "\n Message Error :" + e.getMessage());
				}
				String old = explanationPane.getText();

				anchorPane.setText(Anchor.NOA.toString());
				bestAlternativePane.setText(lm.getAlternativesComparison().getX().getName());
				explanationPane.setText(old + "\n" + message);
			}
		});

		JButton btnAll = new JButton("ALL");
		btnAll.setBounds(24, 248, 117, 25);
		frame.getContentPane().add(btnAll);

		btnAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				LabreucheArguer largue = new LabreucheArguer();
				String message = "";
				try {
					message = largue.argue(lm.getALLExplanation());
				} catch (IllegalStateException e) {
					JOptionPane.showMessageDialog(null,
							"The anchor ALL is not applicable on this example" + "\n Message Error :" + e.getMessage());
				}
				String old = explanationPane.getText();

				anchorPane.setText(Anchor.ALL.toString());
				bestAlternativePane.setText(lm.getAlternativesComparison().getX().getName());
				explanationPane.setText(old + "\n" + message);
			}
		});

		JLabel lblPossibleType = new JLabel("Possible type");
		lblPossibleType.setBounds(32, 209, 127, 15);
		frame.getContentPane().add(lblPossibleType);

		JLabel lblOfExplanations = new JLabel("of explanations :");
		lblOfExplanations.setBounds(26, 225, 130, 15);
		frame.getContentPane().add(lblOfExplanations);

		JLabel lblAnchorUsed = new JLabel("Anchor used : ");
		lblAnchorUsed.setBounds(348, 182, 119, 15);
		frame.getContentPane().add(lblAnchorUsed);

		anchorPane = new JTextPane();
		anchorPane.setBounds(453, 182, 82, 21);
		anchorPane.setEditable(false);
		frame.getContentPane().add(anchorPane);

		btnLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lm = null;
				StringBuilder display = new StringBuilder();

				switch (comboBox.getSelectedItem().toString()) {

				case "example1":
					lm = Examples.getExampleAll();
					display.append("				Example 1");
					break;

				case "example5":
					lm = Examples.getExample5();
					display.append("				Example 5");
					break;

				case "example6":
					lm = Examples.getExample6();
					display.append("				Example 6");
					break;

				case "example9":
					lm = Examples.getExample9();
					display.append("				Example 9");
					break;

				case "example10":
					lm = Examples.getExample10();
					display.append("				Example 10");
					break;

				case "example13":
					lm = Examples.getExample13();
					display.append("				Example 13");
					break;

				case "example14":
					lm = Examples.getExample14();
					display.append("				Example 14");
					break;

				case "example15":
					lm = Examples.getExample15();
					display.append("				Example 15");
					break;

				case "example16":
					lm = Examples.getExample16();
					display.append("				Example 16");
					break;

				case "example17":
					lm = Examples.getExample17();
					display.append("				Example 17");
					break;

				case "example18":
					lm = Examples.getExample18();
					display.append("				Example 18");
					break;

				default:
					LOGGER.info("ERROR example");
				}

				if (lm == null) {
					JOptionPane.showMessageDialog(null, "error loading problem");
					return;
				}

				display.append("\n Criteria         <-   Weights  : \n");

				for (Criterion c : lm.getAlternativesComparison().getWeight().keySet())
					display.append("\n" + c.getName() + "  <-  w_" + c.getId() + " = "
							+ lm.getAlternativesComparison().getWeight().get(c));

				display.append("\n \n Alternatives : ");

				display.append("\n" + "	" + lm.getAlternativesComparison().getX().getName() + " " + " : "
						+ Utils.showVector(lm.getAlternativesComparison().getX().getEvaluations().values()));

				display.append("\n" + "	" + lm.getAlternativesComparison().getY().getName() + " " + " : "
						+ Utils.showVector(lm.getAlternativesComparison().getY().getEvaluations().values()));

				explanationPane.setText("Explanation why " + lm.getAlternativesComparison().getX().getName()
						+ " is better than " + lm.getAlternativesComparison().getY().getName() + " : ");

				JFrame windowInfo = new JFrame();
				windowInfo.setTitle("Problem Informations");
				windowInfo.setSize(600, 550);
				windowInfo.setLocationRelativeTo(null);
				windowInfo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JPanel p = new JPanel();
				windowInfo.getContentPane().add(p);

				JTextArea text = new JTextArea(display.toString());

				p.add(text);

				windowInfo.pack();
				windowInfo.setVisible(true);
			}
		});
	}
}