package pl.cichy3d.komp2ide;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import pl.cichy3d.komp2ide.actions.ActionBinaryToClipboard;
import pl.cichy3d.komp2ide.actions.ActionOpen;
import pl.cichy3d.komp2ide.actions.ActionSave;
import pl.cichy3d.komp2ide.actions.ActionSaveAs;
import pl.cichy3d.komp2ide.assembly.ExpandedAssemblyLang;
import pl.cichy3d.komp2ide.assembly.OriginalAssemblyLang;

public class Asemblator extends JFrame {

	private static final long serialVersionUID = 5510104142899026014L;

	public static JTextArea textArea = new JTextArea();
	public static JTextArea asemblyText = new JTextArea();
	public static JTextArea binaryText = new JTextArea();
	public static Asemblator mainFrame = new Asemblator();
	public static File fileOpened = null;

	public Asemblator() {
		super("Komp2 Asemblator");
		setSize(800, 600);
		setLocationRelativeTo(null);

		JMenuBar menu = new JMenuBar();
		JMenu menuFile = menu.add(new JMenu("File"));
		menuFile.add(new JMenuItem(new ActionOpen()));
		menuFile.add(new JMenuItem(new ActionSave()));
		menuFile.add(new JMenuItem(new ActionSaveAs()));
		menu.add(new JButton(new ActionBinaryToClipboard()));
		setJMenuBar(menu);
		
		JSplitPane split = new JSplitPane();
		{
			JScrollPane scroll = new JScrollPane();
			scroll.getViewport().add(textArea);
			scroll.setSize(new Dimension(800, 600));
			split.setLeftComponent(scroll);
			
			JSplitPane split2 = new JSplitPane();
			{
				scroll = new JScrollPane();
				scroll.getViewport().add(asemblyText);
				asemblyText.setEditable(false);
				scroll.setPreferredSize(new Dimension(30, 30));
				split2.setLeftComponent(scroll);
				
				scroll = new JScrollPane();
				scroll.getViewport().add(binaryText);
				binaryText.setEditable(false);
				scroll.setPreferredSize(new Dimension(20, 20));
				split2.setRightComponent(scroll);
				split2.setDividerLocation(80);
			}
			split.setRightComponent(split2);
			split.setDividerLocation(610);
		}
		add(split);
		binaryText.setLineWrap(true);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		
		textArea.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				translateToOriginalAssembly();
				translateToBinary();
			}
		});

	}
	
	public static void translateToOriginalAssembly() {
		StringBuilder result = new StringBuilder();
		String[] instructions = textArea.getText()
				.replaceAll("\r", "")
				.replaceAll("//.*\n", "\n")
				.split("[;\n]");
		
		for(String inst : instructions) {
			inst = inst.trim();
			if(!inst.isEmpty()) {
				
				
				boolean found = false;
				for(ExpandedAssemblyLang asm : ExpandedAssemblyLang.values()) {
					Matcher m = asm.getRegex().matcher(inst);
					if(found = m.matches()) {
						result.append(asm.getToAsm().apply(m) + ";\n");
						break;
					}
				}
				
				if(!found) {
					result.append(inst+";\n");
				}
			}
		}
		
		
		
		asemblyText.setText(result.toString());
	}
	
	public static void translateToBinary() {
		StringBuilder result = new StringBuilder();
		String[] instructions = asemblyText.getText()
				.replaceAll("\r", "")
				.replaceAll("//.*\n", "\n")
				.split("[;\n]");
		int lineNumber = 0;
		Map<String, String> labels = new HashMap<>();
		for(String inst : instructions) {
			inst = inst.trim();
			
			if(inst.startsWith(":")) {
				if(lineNumber <= 0) {
					labels.put(inst, "ff ff");
				} else {
					String s = String.format("%04x", lineNumber-1);
					labels.put(inst, s.substring(0, 2)+" "+s.substring(2));
				}
				continue;
			}
			
			if(inst.startsWith("\"") && inst.endsWith("\"")) {
				for(int i=1; i<inst.length()-1; i++) {
					result.append(String.format("%02x ", (int)inst.charAt(i)));
					lineNumber++;
				}
				continue;
			}
			
			boolean found = false;
			for(OriginalAssemblyLang asm : OriginalAssemblyLang.values()) {
				Matcher m = asm.getRegex().matcher(inst);
				if(found = m.matches()) {
					result.append(asm.getToBinary().apply(m) + " ");
					lineNumber += asm.getByteSize();
					break;
				}
			}
			
			if(!found) {
				result.append(inst);
			}
		}
		String output = result.toString();
		for(String label : labels.keySet()) {
			output = output.replaceAll(label, labels.get(label));
		}
		
		binaryText.setText(output);
	}

}
