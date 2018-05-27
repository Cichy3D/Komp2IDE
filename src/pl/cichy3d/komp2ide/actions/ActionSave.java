package pl.cichy3d.komp2ide.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import pl.cichy3d.komp2ide.Asemblator;

public class ActionSave extends AbstractAction {

	private static final long serialVersionUID = 3916069443124633507L;

	public ActionSave(){
		super("Save");
		putValue(ACCELERATOR_KEY,
		        KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
	}
	
	@Override
	public void actionPerformed(ActionEvent a) {
		if(Asemblator.fileOpened == null){
			
			new ActionSaveAs().actionPerformed(null);
			
		} else {
			FileOutputStream fos = null;
			OutputStreamWriter osw = null;
			try {
				File f = Asemblator.fileOpened;
				
				fos = new FileOutputStream(f);
				osw = new OutputStreamWriter(fos);
				osw.write(Asemblator.textArea.getText());
				
				osw.flush();				
			} catch (Exception e) {
				JOptionPane.showMessageDialog(Asemblator.mainFrame, e.toString(), 
						"Error", JOptionPane.ERROR_MESSAGE);
			} finally {
				try {
					osw.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}

	@Override
	public boolean isEnabled() {
		return true;
	}



}
