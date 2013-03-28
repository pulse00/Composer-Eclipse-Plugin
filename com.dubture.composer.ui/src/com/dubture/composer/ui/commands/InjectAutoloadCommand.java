package com.dubture.composer.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.dubture.composer.core.log.Logger;

/**
 * Injects the statement "require_once __DIR__ '../../vendor/autoload.php'" into the
 * current cursor position with the correct path to the autoload.php file.
 *
 */
public class InjectAutoloadCommand extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IEditorInput input = HandlerUtil.getActiveEditorInput(event);
		
		if (!(input instanceof IFileEditorInput)) {
			return null;
		}
		
		IFileEditorInput fileEditor = (IFileEditorInput) input;
		IFile file = fileEditor.getFile();
		
		if (file == null) {
			return null;
		}
		
		IPath filePath = file.getFullPath();
		IProject project = file.getProject();
		IFile autoload = project.getFile("vendor/autoload.php");
		if (autoload == null || autoload.exists() == false) {
			return null;
		}
		
		IPath autoloadPath = autoload.getFullPath();
		IPath relativeTo = autoloadPath.makeRelativeTo(filePath);
		
		if (relativeTo != null) {
			insertText("require_once __DIR__ . '" + relativeTo.toString() + "';");
		}
		
		return null;
	}
	
    protected IScriptProject getProject()
    {
        IEditorPart  editorPart =PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

        if(editorPart  != null)
        {
            IFileEditorInput input = (IFileEditorInput)editorPart.getEditorInput() ;
            IFile file = input.getFile();
            IProject activeProject = file.getProject();
            return DLTKCore.create(activeProject);
        }
        return null;
    }
	

	protected void doInsert(ITextEditor editor, String text) {
		ISelectionProvider selectionProvider = editor.getSelectionProvider();
		ISelection selection = selectionProvider.getSelection();

		if (selection instanceof ITextSelection) {
			ITextSelection textSelection = (ITextSelection) selection;

			int offset = textSelection.getOffset();
			IDocumentProvider dp = editor.getDocumentProvider();
			IDocument doc = dp.getDocument(editor.getEditorInput());
			try {
				doc.replace(offset, 0, text);
			} catch (BadLocationException e) {
				Logger.logException(e);
			}
		}

	}
	
    protected void insertText(String text)
    {
        IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        
        if (editor instanceof MultiPageEditorPart) {
            MultiPageEditorPart multiEditor = (MultiPageEditorPart) editor;
            if (multiEditor.getSelectedPage() instanceof ITextEditor) {
                doInsert((ITextEditor) multiEditor.getSelectedPage() , text);
            }
        } else if (editor instanceof ITextEditor) {
            doInsert((ITextEditor) editor, text);
        }
    }        	

}
