package com.dubture.composer.core.ui.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.dubture.composer.core.model.PHPPackage;
import com.dubture.composer.core.model.ModelAccess;

public class DependencyGraphPart extends ViewPart
{
    private Graph graph;
    private int layout = 1;
    private Map<String, GraphNode> nodes;

    @Override
    public void createPartControl(Composite parent)
    {
        graph = new Graph(parent, SWT.NONE);
        
        List<PHPPackage> packages = ModelAccess.getInstance().getPackages(new Path("/sfsta"));
        nodes = new HashMap<String, GraphNode>();
        
        // first pass
        for (PHPPackage pHPPackage : packages) {
            if (pHPPackage.getName() != null) {
                String name = pHPPackage.getName();
                if (nodes.containsKey(name) == false) {
                    nodes.put(name, new GraphNode(graph, SWT.NONE, name));
                }
            }
        }
        

        // 2nd pass
        for (PHPPackage pHPPackage : packages) {
            if (pHPPackage.getName() != null) {
                
                Map<String, String> require = pHPPackage.getRequire();
                
                if (require != null) {
                    addRequire(require, pHPPackage);
                }
                
                if (pHPPackage.getRequireDev() != null) {
                    addRequire(pHPPackage.getRequireDev(), pHPPackage);
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
    
    protected void addRequire(Map<String, String> require, PHPPackage pHPPackage ) 
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
}
