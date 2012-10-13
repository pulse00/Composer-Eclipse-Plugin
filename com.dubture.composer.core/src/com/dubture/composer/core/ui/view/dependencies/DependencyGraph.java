package com.dubture.composer.core.ui.view.dependencies;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.php.internal.ui.PHPUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.dubture.composer.core.log.Logger;
import com.dubture.composer.core.model.InstalledPackage;
import com.dubture.composer.core.model.ModelAccess;

@SuppressWarnings("restriction")
public class DependencyGraph extends ViewPart implements IZoomableWorkbenchPart
{
    public static String VIEW_ID = "com.dubture.composer.core.dependencyGraph";
    private IProject project;
    private IScriptProject scriptProject;
    private GraphViewer viewer;
    private Action toggleDevAction;
    
    private boolean showDev = true;
    private GraphContentProvider contentProvider;

    @Override
    public void createPartControl(Composite parent)
    {
        viewer = new GraphViewer(parent, SWT.BORDER);
        contentProvider = new GraphContentProvider(new ArrayList<InstalledPackage>());
        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(new GraphLabelProvider());
        viewer.setLayoutAlgorithm(setLayout());
        viewer.applyLayout();

        DevFilter filter = new DevFilter();
        ViewerFilter[] filters = new ViewerFilter[1];
        filters[0] = filter;
        viewer.setFilters(filters);
        
        createActions();
        fillToolBar();
        setupListeners();
    }
    
    protected void setupListeners()
    {
        
        viewer.getGraphControl().addMouseListener(new org.eclipse.swt.events.MouseAdapter()
        {
            @Override
            public void mouseUp(MouseEvent e)
            {
                InstalledPackage installed = getSelectedPackage();
            }
            
            @Override
            public void mouseDoubleClick(MouseEvent e)
            {
                InstalledPackage installed = getSelectedPackage();
                IResource composer = ModelAccess.getInstance().getComposer(installed, scriptProject);
                
                if (composer != null && composer instanceof IFile) {
                    try {
                        IFile file = (IFile) composer;
                        IWorkbenchPage page = PHPUiPlugin.getActivePage();
                        if (page != null) {
                            IEditorDescriptor editor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
                            page.openEditor(new FileEditorInput(file), editor.getId());
                        }
                    } catch (PartInitException e1) {
                        Logger.logException(e1);
                    }
                }
            }
        });
        
    }
    
    protected InstalledPackage getSelectedPackage() 
    {
        List<?> selection = viewer.getGraphControl().getSelection();
        
        if (selection != null && selection.size() == 1) {
            GraphNode node = (GraphNode) selection.get(0);
            return (InstalledPackage) node.getData();
        }
        
        return null;
    }

    protected void update()
    {
        IScriptProject scriptProject = DLTKCore.create(project);

        if (scriptProject == null) {
            return;
        }

        List<InstalledPackage> packages = ModelAccess.getInstance()
                .getPackageManager().getAllPackages(scriptProject);

        contentProvider.setPackages(packages);
        viewer.setInput(packages);
        applyFilter(true);
    }
    
    private void applyFilter(boolean showDev)
    {
        DevFilter filter = new DevFilter();
        
        if (showDev == false) {
            filter.hideDevPackages();
        }
        
        ViewerFilter[] filters = new ViewerFilter[1];
        filters[0] = filter;
        viewer.setFilters(filters);
        viewer.applyLayout();

    }

    private LayoutAlgorithm setLayout()
    {
        LayoutAlgorithm layout;
        layout = new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
        return layout;
    }

    @Override
    public void setFocus()
    {

    }

    public void setProject(IProject project)
    {
        this.project = project;
        this.scriptProject = DLTKCore.create(project);
        update();
    }

    private void fillToolBar()
    {
        ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem(this);
        IActionBars bars = getViewSite().getActionBars();
        bars.getMenuManager().add(toolbarZoomContributionViewItem);
        bars.getToolBarManager().add(toggleDevAction);
    }
    
    public void createActions() {
        toggleDevAction = new ToggleDevAction();
    }

    @Override
    public AbstractZoomableViewer getZoomableViewer()
    {
        return viewer;
    }
    
    private class DevFilter extends ViewerFilter
    {
        protected boolean showDev = true;
        
        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element)
        {
            if (showDev == true) {
                return true;
            }
            
            if (element instanceof InstalledPackage) {
                return ((InstalledPackage) element).isDev == false;
            }
            
            return true;
        }
        
        public void hideDevPackages()
        {
            showDev = false;
        }
    } 
    
    protected class ToggleDevAction extends Action {

        public ToggleDevAction()
        {
            super("Toggle dev packages");
            setDescription("Toggle dev packages"); 
            setToolTipText("Toggle dev packages"); 
            DLTKPluginImages.setLocalImageDescriptors(this, "th_showqualified.gif"); //$NON-NLS-1$
        }
        
        public void run() { 
            showDev = !showDev;
            applyFilter(showDev);
        }
    }
}
