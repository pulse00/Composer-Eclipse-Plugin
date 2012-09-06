package com.dubture.composer.eclipse.ui.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.getcomposer.core.PackageInterface;
import org.pex.core.log.Logger;

import com.dubture.composer.eclipse.model.EclipsePHPPackage;
import com.dubture.composer.eclipse.model.ModelAccess;

public class DependencyGraph extends ViewPart
{
    public static String VIEW_ID = "com.dubture.composer.eclipse.dependencyGraph";
    
    private IProject project;
    
    private Graph graph;
    private int layout = 1;
    private Map<String, GraphNode> nodes;
    
    private Composite parent;

    @Override
    public void createPartControl(Composite parent)
    {
        this.parent = parent;
        
        update();
    }
    
    protected void update() {

        if (graph != null) {
            for (Control child : graph.getChildren()) {
                if (!child.isDisposed()) {
                    child.dispose();
                }
            }
            
            if (!graph.isDisposed()) {
                graph.dispose();
            }
        }
        
        graph = new Graph(parent, SWT.NONE);
        
        if (project == null) {
            Logger.debug("Unable to retrieve project in dependency graph");
            return;
        }
        
        IPath path = project.getProjectRelativePath();
        
        System.err.println(path);
        List<EclipsePHPPackage> packages = ModelAccess.getInstance().getPackages(new Path("/" + project.getName()));
        nodes = new HashMap<String, GraphNode>();
        
        // first pass
        for (EclipsePHPPackage pHPPackage : packages) {
            if (pHPPackage.getName() != null) {
                String name = pHPPackage.getName();
                if (nodes.containsKey(name) == false) {
                    nodes.put(name, new GraphNode(graph, SWT.NONE, name));
                }
            }
        }
        

        // 2nd pass
        for (EclipsePHPPackage pHPPackage : packages) {
            
            
            if (pHPPackage.getName() != null) {
                
                Map<String, String> require = pHPPackage.getPhpPackage().getRequire();
                
                if (require != null) {
                    addRequire(require, pHPPackage.getPhpPackage());
                }
                
                if (pHPPackage.getPhpPackage().getRequireDev() != null) {
                    addRequire(pHPPackage.getPhpPackage().getRequireDev(), pHPPackage.getPhpPackage());
                }
            }
        }
        
        graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
        
        // Selection listener on graphConnect or GraphNode is not supported
        // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=236528
        graph.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                System.out.println(e);
            }

        });        
        
            
    }
    
    protected void addRequire(Map<String, String> require, PackageInterface pHPPackage ) 
    {
        Iterator<?> reIt = require.keySet().iterator();
        while (reIt.hasNext()) {
            String name = (String) reIt.next();
            if (nodes.containsKey(name)) {
                GraphNode leftNode = nodes.get(pHPPackage.getName());
                GraphNode rightNode = nodes.get(name);
                new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, leftNode,rightNode);
            }
        }
    }
    
    public void setLayoutManager() {
        switch (layout) {
        case 1:
            graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(
                    LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
            layout++;
            break;
        case 2:
            graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(
                    LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
            layout = 1;
            break;
        }
    }    

    @Override
    public void setFocus()
    {

    }

    public void setProject(IProject project)
    {
        this.project = project;
        update();
    }
}
