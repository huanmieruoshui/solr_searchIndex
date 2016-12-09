package com.bmtech.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class HelpUnReDo {
	public static HelpUnReDo addUndoAction(JTextComponent pane, int undoVK, int redoVK) {
		return new HelpUnReDo(pane, undoVK, redoVK);
	}
	public static HelpUnReDo addUndoAction(JTextComponent pane) {
		return new HelpUnReDo(pane);
	}
	private UndoHandler undoHandler = new UndoHandler();
	private UndoManager undoManager = new UndoManager();
	public final UndoAction undoAction = new UndoAction();
	public final RedoAction redoAction  = new RedoAction();
	public HelpUnReDo(JTextComponent pane){
		this(pane, KeyEvent.VK_Z, KeyEvent.VK_R);
	}
	public HelpUnReDo(JTextComponent pane, int undoVK, int redoVK){
		pane.getDocument().addUndoableEditListener(undoHandler);
		undoManager.setLimit(10);
		if(undoVK > 0){
			ActionListener  unDolistener = new ActionListener () {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(undoManager.canUndo()) {
						undoManager.undo();
					}else {
						System.out.println("now can not undo");
					}
				}

			};
			pane.registerKeyboardAction(unDolistener,
					KeyStroke.getKeyStroke(undoVK,
							KeyEvent.CTRL_DOWN_MASK, false),
							JComponent.WHEN_FOCUSED);
		}
		if(redoVK > 0){
			ActionListener  reDolistener = new ActionListener () {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(undoManager.canRedo()) {
						undoManager.redo();
					}else {
						System.out.println("now can not redo");
					}
				}

			};
			pane.registerKeyboardAction(reDolistener,
					KeyStroke.getKeyStroke(redoVK,
							KeyEvent.CTRL_DOWN_MASK, false),
							JComponent.WHEN_FOCUSED);
		}

	}
	class UndoHandler implements UndoableEditListener{

		/**
		 * Messaged when the Document has created an edit, the edit is added to
		 * <code>undoManager</code>, an instance of UndoManager.
		 */
		public void undoableEditHappened(UndoableEditEvent e){
			undoManager.addEdit(e.getEdit());
			undoAction.update();
			redoAction.update();
		}
	}

	class UndoAction extends AbstractAction{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public UndoAction(){
			super("Undo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e){
			try{
				undoManager.undo();
			}catch (CannotUndoException ex){
				ex.printStackTrace();
			}
			update();
			redoAction.update();
		}

		protected void update() {
			if (undoManager.canUndo()){
				setEnabled(true);
				putValue(Action.NAME, undoManager.getUndoPresentationName());
			}else{
				setEnabled(false);
				putValue(Action.NAME, "Undo");
			}
		}
	}

	class RedoAction extends AbstractAction{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RedoAction(){
			super("Redo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e){
			try{
				undoManager.redo();
			}catch (CannotRedoException ex){
				ex.printStackTrace();
			}
			update();
			undoAction.update();
		}

		protected void update(){
			if (undoManager.canRedo()){
				setEnabled(true);
				putValue(Action.NAME, undoManager.getRedoPresentationName());
			}else{
				setEnabled(false);
				putValue(Action.NAME, "Redo");
			}
		}
	}

}
