package pl.cichy3d.komp2ide.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import pl.cichy3d.komp2ide.Asemblator;

public class ActionOpen extends AbstractAction {

	private static final long serialVersionUID = -6366108364638174409L;

	public ActionOpen(){
		super("Open...");
		putValue(ACCELERATOR_KEY,
		        KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
	}
	
	@Override
	public void actionPerformed(ActionEvent a) {
		JFileChooser fc = new JFileChooser(new File("."));
		int status = fc.showOpenDialog(Asemblator.mainFrame);
		if(status == JFileChooser.APPROVE_OPTION){
			File f = fc.getSelectedFile();
			Asemblator.fileOpened = null;
			otworz(f);
		}
	}

	public static void otworz(File f) {
		try {
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader irs = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(irs);
			List<String> lines = br.lines().collect(Collectors.toList());
			StringBuilder stringBuilder = new StringBuilder();
			for(String line : lines){
				stringBuilder.append(line).append("\r\n");
			}
			Asemblator.textArea.setText(stringBuilder.toString());
			
			br.close();
			irs.close();
			fis.close();
			
			Asemblator.fileOpened = f;
			
			Asemblator.translateToOriginalAssembly();
			Asemblator.translateToBinary();
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(Asemblator.mainFrame, e.getStackTrace(), 
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public boolean isEnabled() {
		return true;
	}



}
