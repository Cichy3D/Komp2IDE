package pl.cichy3d.komp2ide.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import pl.cichy3d.komp2ide.Asemblator;

public class ActionSaveAs extends AbstractAction {

	private static final long serialVersionUID = 8607086858229983384L;

	public ActionSaveAs(){
		super("Save as...");
	}
	
	@Override
	public void actionPerformed(ActionEvent a) {
		JFileChooser fc = new JFileChooser(new File("."));
		int status = fc.showSaveDialog(Asemblator.mainFrame);
		if(status == JFileChooser.APPROVE_OPTION){

				File f = fc.getSelectedFile();
				Asemblator.fileOpened = f;
				
				new ActionSave().actionPerformed(null);

		}
	}

	@Override
	public boolean isEnabled() {
		return true;
	}



}

