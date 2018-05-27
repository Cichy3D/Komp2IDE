package pl.cichy3d.komp2ide.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import pl.cichy3d.komp2ide.Asemblator;

public class ActionBinaryToClipboard extends AbstractAction {

	private static final long serialVersionUID = -599100735146646818L;

	public ActionBinaryToClipboard(){
		super("Binary to clipboard");
		putValue(ACCELERATOR_KEY,
		        KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK));
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		StringSelection selection = new StringSelection(Asemblator.binaryText.getText());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}

}
